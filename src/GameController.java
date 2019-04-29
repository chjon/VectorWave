import java.util.ArrayDeque;
import java.util.Deque;

public class GameController implements Runnable {
    enum Direction {
        UP, DOWN, LEFT, RIGHT,
    }

    private volatile boolean isRunning;
    private long nextTickTime;
    private long nextSpawnTime;
    private static final byte TARGET_TICKRATE = 120;
    private static final double SPAWN_DIST = 1;
    private static final long SPAWN_COOLDOWN = 400;
    private static final double ENTITY_VEL = 0.005;
    private static final double COLLISION_RADIUS_SQUARED = 0.01;
    private InputLayer inputLayer;

    private Direction curDirection = Direction.UP;
    private Deque<Entity> entityQueue;

    GameController(InputLayer inputLayer) {
        this.inputLayer = inputLayer;
        isRunning = true;
        entityQueue = new ArrayDeque<>();
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
            entityQueue.addLast(generateEntity());
        }

        // Set direction
        String lastKeyPress = inputLayer.getLastKeyPress();
        if (lastKeyPress != null) {
            try {
                curDirection = Direction.valueOf(lastKeyPress);
            } catch (Exception ignored) {}
        }

        // Update entities
        for (Entity e : entityQueue) {
            e.update();
        }

        if (entityQueue.size() > 0) {
            Entity first = entityQueue.peekFirst();
            final double mag2 = first.getPos().getMag2();
            if (mag2 < COLLISION_RADIUS_SQUARED) {
                entityQueue.removeFirst();
            }
        }
    }

    private Entity generateEntity() {
        final Vector pos = new Vector(0, 0);
        final Vector vel = new Vector(0, 0);

        switch((int) (4 * Math.random())) {
            case 0: // UP
                pos.y = -SPAWN_DIST;
                vel.y = ENTITY_VEL;
                break;
            case 1: // DOWN
                pos.y = SPAWN_DIST;
                vel.y = -ENTITY_VEL;
                break;
            case 2: // LEFT
                pos.x = -SPAWN_DIST;
                vel.x = ENTITY_VEL;
                break;
            case 3: // RIGHT
                pos.x = SPAWN_DIST;
                vel.x = -ENTITY_VEL;
                break;
        }

        return new Entity(pos, vel);
    }

    void stop() {
        isRunning = false;
    }

    Direction getCurDirection() {
        return curDirection;
    }

    Deque<Entity> getEntities() {
        return entityQueue;
    }
}
