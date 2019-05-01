import java.util.Queue;

public class GameController implements Runnable {
    private volatile boolean isRunning;
    private long nextTickTime;
    private long nextSpawnTime;
    private static final byte TARGET_TICKRATE = 120;
    private static final long SPAWN_COOLDOWN = 350;
    private static final long START_DELAY = 3000;
    private static final long MIN_DURATION = 1000;
    private InputLayer inputLayer;

    private Direction curDirection = Direction.UP;
    private AsyncPriorityQueue<Entity> entityQueue;
    private int hit = 0;
    private int miss = 0;
    private long startTime;
    private double highscore;

    GameController(InputLayer inputLayer) {
        this.inputLayer = inputLayer;
        isRunning = true;
        entityQueue = new AsyncPriorityQueue<>((e1, e2) -> (int)(e1.endTime - e2.endTime));
    }

    @Override
    public void run() {
        startTime = System.currentTimeMillis();
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
                if (first.direction == curDirection) {
                    hit++;
                } else {
                    miss++;
                    highscore = Math.max(highscore, getTimeElapsed());
                    startTime = curTime;
                }

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

        return new Entity(System.currentTimeMillis() + START_DELAY, MIN_DURATION, direction, (int) Math.round(Math.random()));
    }

    void stop() {
        isRunning = false;
        System.out.println("Hit " + hit + ", Missed " + miss);
    }

    Direction getCurDirection() {
        return curDirection;
    }

    void getEntities(Queue<Entity> container) {
        container.clear();
        entityQueue.addAllTo(container);
    }

    int getScoreHit() {
        return hit;
    }

    int getScoreMiss() {
        return miss;
    }

    double getTimeElapsed() {
        return (System.currentTimeMillis() - startTime) / 1000d;
    }

    double getHighscore() {
        return highscore;
    }
}
