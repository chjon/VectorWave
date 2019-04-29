import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Map;
import java.util.TreeMap;

public class InputLayer implements KeyListener {
    private Map<Integer, String> keyBindings;
    private Map<String, Boolean> keyStates;
    private String lastKeyPress = null;

    InputLayer() {
        keyBindings = new TreeMap<>();
        keyStates = new TreeMap<>();
        initializeKeyBindings();
    }

    private void initializeKeyBindings() {
        bind(KeyEvent.VK_W, "UP");
        bind(KeyEvent.VK_S, "DOWN");
        bind(KeyEvent.VK_A, "LEFT");
        bind(KeyEvent.VK_D, "RIGHT");
        bind(KeyEvent.VK_ESCAPE, "EXIT");
    }

    private void bind(int key, String action) {
        keyBindings.put(key, action);
        keyStates.put(action, false);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    private void setKeyState(KeyEvent e, boolean isPressed) {
        String action = keyBindings.get(e.getKeyCode());
        if (action != null) {
            keyStates.put(action, isPressed);

            if (isPressed) {
                lastKeyPress = action;
            }
        }
    }

    Boolean getKeyState(String action) {
        return keyStates.get(action);
    }

    String getLastKeyPress() {
        return lastKeyPress;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        setKeyState(e, true);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        setKeyState(e, false);
    }
}
