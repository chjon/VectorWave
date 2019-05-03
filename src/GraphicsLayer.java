import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.TreeMap;

public class GraphicsLayer implements Runnable {
    private JFrame window;
    private Image bufferImage;
    private Graphics buffer;
    private final InputLayer inputLayer;
    private final GameController gameController;

    private volatile boolean isRunning;
    private long nextFrameTime;
    private Dimension windowDimensions;

    private Deque<Entity> entitiesToDraw;

    private byte targetFPS = 60;
    private static final double PLAYER_RADIUS = 0.1;
    private static final double ENTITY_RADIUS = 0.06;
    private static final Font SCORE_FONT = new Font("Arial", Font.PLAIN, 1);

    private Map<String, BufferedImage> imageMap;
    private Map<String, String> imagePathMap;

    GraphicsLayer(JFrame window, InputLayer inputLayer, GameController gameController) {
        this.window = window;
        this.inputLayer = inputLayer;
        this.gameController = gameController;
        entitiesToDraw = new ArrayDeque<>();
        imageMap = new TreeMap<>();
        imagePathMap = new TreeMap<>();

        imagePathMap.put("arrow", "res/arrow.png");
        imagePathMap.put("enemy_0", "res/enemy_0.png");
        imagePathMap.put("enemy_1", "res/enemy_1.png");

        for (Map.Entry<String, String> entry : imagePathMap.entrySet()) {
            try {
                File file = new File(entry.getValue());
                imageMap.put(entry.getKey(), ImageIO.read(file));
            } catch (java.io.IOException e) {
                System.err.println(e.getMessage() + " " + entry.getValue());
            }
        }

        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setExtendedState(JFrame.MAXIMIZED_BOTH);
        window.setUndecorated(true);
        window.pack();
        window.setVisible(true);
        windowDimensions = window.getSize();
        bufferImage = window.createImage(windowDimensions.width, windowDimensions.height);
        buffer = bufferImage.getGraphics();

        isRunning = true;
    }

    @Override
    public void run() {
        while (isRunning) {
            if (inputLayer.getKeyState("EXIT")) {
                window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
                window.dispose();
                return;
            }

            final long curTime = System.currentTimeMillis();
            if (curTime < nextFrameTime) {
                continue;
            }

            nextFrameTime = curTime + 1000 / targetFPS;
            draw(curTime);
        }
    }

    private BufferedImage rotateImage(BufferedImage image, double angle) {
        AffineTransform transformation = new AffineTransform();
        transformation.rotate(
                angle,
                image.getWidth(null) / 2,
                image.getHeight(null) / 2
        );

        AffineTransformOp operation = new AffineTransformOp(transformation, AffineTransformOp.TYPE_BILINEAR);
        return operation.filter(image, null);
    }

    private double getAngle(Direction direction) {
        switch (direction) {
            case DOWN:
                return Math.PI;
            case LEFT:
                return -Math.PI / 2;
            case RIGHT:
                return Math.PI / 2;
            case UP:
            default:
                return 0;
        }
    }

    private void drawArrow() {
        final double angle = getAngle(gameController.getCurDirection());

        Sprite arrowSprite = new Sprite("arrow");
        arrowSprite.setCentre(true);
        arrowSprite.setSize(PLAYER_RADIUS, PLAYER_RADIUS);
        arrowSprite.setAngle(angle);
        drawSprite(arrowSprite, Math.min(windowDimensions.width, windowDimensions.height));
    }

    private void drawEntity(Entity e, long curTime) {
        if (curTime + e.duration < e.endTime) {
            return;
        }

        Sprite entitySprite = new Sprite("enemy_" + e.type);
        double angle = getAngle(e.direction) + Math.PI;
        final double remaining = e.getRemaining(curTime);

        switch (e.type) {
            case 1:
                final double BEGIN_SPIN = 0.6;
                final double END_SPIN = 0.3;
                final double SPIN_TIME = BEGIN_SPIN - END_SPIN;

                if (remaining >= BEGIN_SPIN) {
                    angle += Math.PI;
                } else if (remaining >= END_SPIN) {
                    angle += Math.PI * (remaining - END_SPIN) / SPIN_TIME;
                }
                break;
            default:
                break;
        }

        final double scaleFactor = (1 - PLAYER_RADIUS - ENTITY_RADIUS / 4) * Math.min(windowDimensions.width, windowDimensions.height) / 2d;
        entitySprite.setAngle(angle);
        entitySprite.setPos(new Vector(0, remaining + PLAYER_RADIUS + ENTITY_RADIUS / 4).rotate(angle));
        entitySprite.setCentre(true);
        entitySprite.setSize(ENTITY_RADIUS, ENTITY_RADIUS);
        drawSprite(entitySprite, scaleFactor);
    }

    private void drawSprite(Sprite sprite, double scaleFactor) {
        BufferedImage sourceImage = imageMap.get(sprite.getSourceImage());
        if (sourceImage == null) {
            sourceImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        }

        final BufferedImage transformedImage = rotateImage(sourceImage, sprite.getAngle());
        final Vector pos = sprite.getPos().scale(scaleFactor);
        final double newWidth = sprite.getWidth() * scaleFactor;
        final double newHeight = sprite.getHeight() * scaleFactor;
        if (sprite.shouldCentre()) {
            pos.x -= newWidth / 2;
            pos.y -= newHeight / 2;
        }

        buffer.drawImage(transformedImage, (int) pos.x, (int) pos.y, (int) newWidth, (int) newHeight, null);
    }

    private void drawText(String[] text, Font font, Vector pos) {
        buffer.setFont(font);
        FontMetrics metrics = buffer.getFontMetrics(font);
        int height = metrics.getHeight();
        for (int i = 0; i < text.length; i++) {
            final int y = (int) pos.y + (i + 1) * height;
            buffer.drawString(text[i], (int) pos.x, y);
        }
    }

    private void drawCentredText(String[] text, Font font, Vector pos) {
        buffer.setFont(font);
        FontMetrics metrics = buffer.getFontMetrics(font);
        int height = metrics.getHeight();
        for (int i = 0; i < text.length; i++) {
            int x = (int) pos.x - metrics.stringWidth(text[i]) / 2;
            final int y = (int) pos.y + (i + 1) * height;
            buffer.drawString(text[i], x, y);
        }
    }

    private void draw(long curTime) {
        final Graphics g = window.getGraphics();
        final int width = windowDimensions.width;
        final int height = windowDimensions.height;
        buffer.clearRect(0, 0, width, height);

        buffer.setColor(new Color(0x333333));

        buffer.translate(width / 2, height / 2);

        final int size = Math.min(width, height);
        buffer.fillArc(-size / 2, -size / 2, size, size , 0, 360);

        // Draw entities
        gameController.getEntities(entitiesToDraw);
        for (Entity e : entitiesToDraw) {
            drawEntity(e, curTime);
        }

        // Draw player
        drawArrow();

        buffer.setColor(Color.WHITE);
        drawCentredText(new String[]{
                String.format("%.2f", gameController.getTimeElapsed()),
                String.format("%.2f", gameController.getHighscore()),
        }, SCORE_FONT.deriveFont(0.03f * size), new Vector(0, -size / 4));

        // Draw score
        buffer.translate(-width / 2, -height / 2);
        buffer.setColor(Color.BLACK);
        drawText(new String[]{
                "Hit: " + gameController.getScoreHit(),
                "Miss: " + gameController.getScoreMiss(),
        }, SCORE_FONT.deriveFont(0.04f * size), new Vector(0, 0));

        g.drawImage(bufferImage, 0, 0, null);
    }

    void stop() {
        isRunning = false;
    }
}
