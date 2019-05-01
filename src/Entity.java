class Entity {
    final long endTime;
    final long duration;
    final Direction direction;
    final int type;

    Entity(long endTime, long duration, Direction direction, int type) {
        this.endTime = endTime;
        this.direction = direction;
        this.type = type;

        if (type == 0) {
            this.duration = duration + 500 * (int) (3 * Math.random());
        } else {
            this.duration = duration + 500;
        }
    }

    double getRemaining(long curTime) {
        return (endTime - curTime) / (double) duration;
    }
}
