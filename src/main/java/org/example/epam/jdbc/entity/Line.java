package org.example.epam.jdbc.entity;

public class Line {
    private final double a;
    private final double b;
    private final double c;

    public Line(final double a, final double b, final double c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public Line(final double a, final double b) {
        this(a, b, 0.0);
    }

    public boolean contains(Point point) {
        return Double.compare(a * point.getX() + b * point.getY() - c, 0.0) == 0;
    }

    @Override
    public String toString() {
        return String.format("%.0fx + %.0fy = %.0f", a, b, c);
    }
}
