public class Vector {
    public double x;
    public double y;

    Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    Vector(Vector other) {
        this.x = other.x;
        this.y = other.y;
    }

    double getMag2() {
        return x * x + y * y;
    }

    Vector rotate(double angle) {
        final double cosAngle = Math.cos(angle);
        final double sinAngle = Math.sin(angle);

        return new Vector(
                x * cosAngle - y * sinAngle,
                x * sinAngle + y * cosAngle
        );
    }

    Vector rotate(Direction direction) {
        switch (direction) {
            case DOWN:
                return rotate(Math.PI);
            case RIGHT:
                return rotate(Math.PI / 2);
            case LEFT:
                return rotate(-Math.PI / 2);
            case UP:
            default:
                return new Vector(this);
        }
    }

    Vector scale(double lambda) {
        return new Vector(x * lambda, y * lambda);
    }
}
