package kpgr1.model;

import java.util.List;

/** Axis-aligned rectangle given by two opposite corners; corners 0..3 = TL, TR, BR, BL. */
public final class ClipRectangle {
    private int minX;
    private int minY;
    private int maxX;
    private int maxY;

    public ClipRectangle(Point p1, Point p2) {
        set(p1, p2);
    }

    private void set(Point p1, Point p2) {
        minX = Math.min(p1.x(), p2.x());
        maxX = Math.max(p1.x(), p2.x());
        minY = Math.min(p1.y(), p2.y());
        maxY = Math.max(p1.y(), p2.y());
    }

    public int minX() { return minX; }

    public int minY() { return minY; }

    public int maxX() { return maxX; }

    public int maxY() { return maxY; }

    /** Corners in order TL, TR, BR, BL. */
    public List<Point> corners() {
        return List.of(new Point(minX, minY), new Point(maxX, minY), new Point(maxX, maxY), new Point(minX, maxY));
    }

    /** Moves corner {@code index} to {@code p}; the opposite corner stays fixed. */
    public void moveCorner(int index, Point p) {
        Point opposite = corners().get((index + 2) % 4);
        set(p, opposite);
    }
}
