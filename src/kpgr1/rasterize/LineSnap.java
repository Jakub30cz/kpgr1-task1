package kpgr1.rasterize;

import kpgr1.model.Point;

/** Snaps a line end point to the nearest of 8 directions (0, 45, 90, ... degrees) around the start. */
public final class LineSnap {
    // tan(22.5 deg) and tan(67.5 deg) split the plane into 45 degree sectors.
    private static final double TAN_22_5 = Math.tan(Math.PI / 8);
    private static final double TAN_67_5 = Math.tan(3 * Math.PI / 8);

    private LineSnap() {}

    public static Point snap(Point start, Point cursor) {
        int dx = cursor.x() - start.x();
        int dy = cursor.y() - start.y();
        int adx = Math.abs(dx);
        int ady = Math.abs(dy);
        if (adx == 0 && ady == 0) return start;
        if (ady <= adx * TAN_22_5) return new Point(cursor.x(), start.y());
        if (ady >= adx * TAN_67_5) return new Point(start.x(), cursor.y());
        int d = Math.max(adx, ady);
        return new Point(start.x() + Integer.signum(dx) * d, start.y() + Integer.signum(dy) * d);
    }
}
