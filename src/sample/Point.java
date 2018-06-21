package sample;

public class Point {
    public Double x;
    public Double y;

    public Point(Double x, Double y) {
        this.x = x;
        this.y = y;
    }

    public Point(Double y) {
        this.y = y;
    }

    public Point(double v) {
        this.x = v;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public void setY(Double y) {
        this.y = y;
    }
}
