package kpgr1.model;

import java.util.List;

/**
 * Axis-aligned rectangle. The two raw corner points are stored as dragged, so each corner handle keeps its identity
 * even when it is moved past the opposite corner; min/max are derived. Handle indices 0..3 = (x0,y0), (x1,y0),
 * (x1,y1), (x0,y1), which are TL, TR, BR, BL while the rectangle is not flipped.
 */
public final class ClipRectangle {
    private int x0;
    private int y0;
    private int x1;
    private int y1;

    public ClipRectangle(Point p1, Point p2) {
        x0 = p1.x();
        y0 = p1.y();
        x1 = p2.x();
        y1 = p2.y();
    }

    public int minX() { return Math.min(x0, x1); }

    public int minY() { return Math.min(y0, y1); }

    public int maxX() { return Math.max(x0, x1); }

    public int maxY() { return Math.max(y0, y1); }

    /** Normalized corners in order TL, TR, BR, BL, for clipping and rendering. */
    public List<Point> corners() {
        return List.of(new Point(minX(), minY()), new Point(maxX(), minY()), new Point(maxX(), maxY()),
                new Point(minX(), maxY()));
    }

    /** Stored position of corner handle {@code index}. */
    public Point handle(int index) {
        return switch (index) {
            case 0 -> new Point(x0, y0);
            case 1 -> new Point(x1, y0);
            case 2 -> new Point(x1, y1);
            case 3 -> new Point(x0, y1);
            default -> throw new IndexOutOfBoundsException(index);
        };
    }

    /** Moves corner handle {@code index} to {@code p}; the opposite handle stays fixed. */
    public void moveCorner(int index, Point p) {
        switch (index) {
            case 0 -> { x0 = p.x(); y0 = p.y(); }
            case 1 -> { x1 = p.x(); y0 = p.y(); }
            case 2 -> { x1 = p.x(); y1 = p.y(); }
            case 3 -> { x0 = p.x(); y1 = p.y(); }
            default -> throw new IndexOutOfBoundsException(index);
        }
    }
}
