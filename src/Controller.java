import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class Controller implements WindowListener {
    private final GraphicsLayer graphicsLayer;
    private final GameController gameController;

    Controller() {
        final JFrame window = new JFrame(Main.GAME_NAME);
        final InputLayer inputLayer = new InputLayer();
        gameController = new GameController(inputLayer);
        graphicsLayer = new GraphicsLayer(window, inputLayer, gameController);

        window.addKeyListener(inputLayer);
        window.addWindowListener(this);
    }

    void start() {
        new Thread(graphicsLayer).start();
        new Thread(gameController).start();
    }

    // Window listener methods

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
        graphicsLayer.stop();
        gameController.stop();
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }
}
