import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Deque;

public class GraphicsLayer implements Runnable {
    private JFrame window;
    private Image bufferImage;
    private Graphics buffer;
    private volatile boolean isRunning;
    private long nextFrameTime;
    private byte targetFPS = 60;
    private Dimension windowDimensions;
    private final InputLayer inputLayer;
    private final GameController gameController;

    private BufferedImage arrowImage;

    GraphicsLayer(JFrame window, InputLayer inputLayer, GameController gameController) {
        this.window = window;
        this.inputLayer = inputLayer;
        this.gameController = gameController;

        try {
            File file = new File("res/arrow.png");
            arrowImage = ImageIO.read(file);
        } catch (java.io.IOException e) {
            System.err.println(e.getMessage());
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
            draw();
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

    private void drawArrow(int width, int height) {
        final GameController.Direction direction = gameController.getCurDirection();

        BufferedImage rotatedArrowImage;
        switch (direction) {
            case DOWN:
                rotatedArrowImage = rotateImage(arrowImage, Math.PI);
                break;
            case LEFT:
                rotatedArrowImage = rotateImage(arrowImage, -Math.PI / 2);
                break;
            case RIGHT:
                rotatedArrowImage = rotateImage(arrowImage, Math.PI / 2);
                break;
            case UP:
            default:
                rotatedArrowImage = rotateImage(arrowImage, 0);
                break;
        }

        final int size = (int) (0.1 * height);
        buffer.drawImage(rotatedArrowImage, width / 2 - size / 2, height / 2 - size / 2, size, size, null);
    }

    private void drawEntity(Entity e, int width, int height) {
        final Vector pos = e.getPos();
        final int scaleFactor = Math.min(width, height) / 2;
        pos.x *= scaleFactor;
        pos.y *= scaleFactor;
        final int size = (int) (0.03 * scaleFactor);
        buffer.setColor(new Color(0xFF7700));
        buffer.fillArc((int) pos.x + width / 2 - size / 2, (int) pos.y + height / 2 - size / 2, size, size, 0, 360);
    }

    private void draw() {
        final Graphics g = window.getGraphics();
        final int width = windowDimensions.width;
        final int height = windowDimensions.height;
        buffer.clearRect(0, 0, width, height);

        buffer.setColor(new Color(0x333333));
        buffer.fillRect(
            (int) (0.1 * width),
            (int) (0.1 * height),
            (int) (0.8 * width),
            (int) (0.8 * height)
        );

        drawArrow(width, height);
        Deque<Entity> entities = gameController.getEntities();
        for (Entity e : entities) {
            drawEntity(e, width, height);
        }

        g.drawImage(bufferImage, 0, 0, null);
    }

    void stop() {
        isRunning = false;
    }
}