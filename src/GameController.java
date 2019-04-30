import java.util.PriorityQueue;
import java.util.Queue;

public class GameController implements Runnable {
    private volatile boolean isRunning;
    private long nextTickTime;
    private long nextSpawnTime;
    private static final byte TARGET_TICKRATE = 120;
    private static final long SPAWN_COOLDOWN = 400;
    private InputLayer inputLayer;

    private Direction curDirection = Direction.UP;
    private Queue<Entity> entityQueue;

    GameController(InputLayer inputLayer) {
        this.inputLayer = inputLayer;
        isRunning = true;
        entityQueue = new PriorityQueue<>((e1, e2) -> (int)(e1.endTime - e2.endTime));
    }

    @Override
    public void run() {
        while (isRunning) {
            final long curTime = System.currentTimeMillis();
            if (curTime < nextTickTime) {
                continue;
            }

            nextTickTime = curTime + 1000 / TARGET_TICKRATE;
            update(curTime);
        }
    }

    private void update(long curTime) {
        // Spawn entities
        if (curTime > nextSpawnTime) {
            nextSpawnTime = curTime + SPAWN_COOLDOWN;
            entityQueue.add(generateEntity());
        }

        // Set direction
        String lastKeyPress = inputLayer.getLastKeyPress();
        if (lastKeyPress != null) {
            try {
                curDirection = Direction.valueOf(lastKeyPress);
            } catch (Exception ignored) {}
        }

        if (entityQueue.size() > 0) {
            Entity first = entityQueue.peek();
            if (first.endTime < curTime) {
                entityQueue.remove();
            }
        }
    }

    private Entity generateEntity() {
        Direction direction = null;

        switch((int) (4 * Math.random())) {
            case 0: // UP
                direction = Direction.UP;
                break;
            case 1: // DOWN
                direction = Direction.DOWN;
                break;
            case 2: // LEFT
                direction = Direction.LEFT;
                break;
            case 3: // RIGHT
                direction = Direction.RIGHT;
                break;
        }

        return new Entity(System.currentTimeMillis() + 3000, 1000 + 500 * (int) (3 * Math.random()), direction);
    }

    void stop() {
        isRunning = false;
    }

    Direction getCurDirection() {
        return curDirection;
    }

    void getEntities(Queue<Entity> container) {
        container.clear();
        container.addAll(entityQueue);
    }
}
