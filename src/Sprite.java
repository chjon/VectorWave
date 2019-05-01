public class Sprite {
    private Vector pos;
    private Vector size;
    private double angle;
    private String sourceImage;
    private boolean shouldCentre;

    Sprite(String sourceImage) {
        pos = new Vector(0, 0);
        size = new Vector(1, 1);
        this.sourceImage = sourceImage;
        shouldCentre = false;
    }

    void setPos(double x, double y) {
        pos.x = x;
        pos.y = y;
    }

    void setPos(Vector pos) {
        this.pos.x = pos.x;
        this.pos.y = pos.y;
    }

    void setWidth(double width) {
        size.x = width;
    }

    void setHeight(double height) {
        size.y = height;
    }

    void setSize(double width, double height) {
        size.x = width;
        size.y = height;
    }

    void setCentre(boolean shouldCentre) {
        this.shouldCentre = shouldCentre;
    }

    void setAngle(double angle) {
        this.angle = angle;
    }

    Vector getPos() {
        return pos;
    }

    double getWidth() {
        return size.x;
    }

    double getHeight() {
        return size.y;
    }

    boolean shouldCentre() {
        return shouldCentre;
    }

    double getAngle() {
        return angle;
    }

    String getSourceImage() {
        return sourceImage;
    }
}
