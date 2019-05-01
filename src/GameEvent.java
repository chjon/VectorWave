public class GameEvent {
    final String eventName;
    final Direction direction;

    GameEvent(String eventName, Direction direction) {
        this.eventName = eventName;
        this.direction = direction;
    }
}
