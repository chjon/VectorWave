class Entity {
    final long endTime;
    final long duration;
    final Direction direction;

    Entity(long endTime, long duration, Direction direction) {
        this.endTime = endTime;
        this.duration = duration;
        this.direction = direction;
    }

    double getRemaining(long curTime) {
        return (endTime - curTime) / (double) duration;
    }
}
