public class Entity {
    private Vector pos;
    private Vector vel;

    Entity(Vector pos, Vector vel) {
        this.pos = pos;
        this.vel = vel;
    }

    void update() {
        pos.x += vel.x;
        pos.y += vel.y;
    }

    Vector getPos() {
        return new Vector(pos);
    }
}
