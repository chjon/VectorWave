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
}
