package kpgr1.clip;

import kpgr1.model.Point;
import kpgr1.model.Polygon;

/** Orientation and convexity helpers for polygons in screen coordinates (y axis points down). */
public final class PolygonGeometry {
    private PolygonGeometry() {}

    /**
     * Shoelace signed area. In screen coordinates (y down) a POSITIVE value means the
     * vertices run clockwise on screen; a negative value means counter-clockwise.
     */
    public static double signedArea(Polygon p) {
        long sum = 0;
        int n = p.size();
        for (int i = 0; i < n; i++) {
            Point a = p.getVertex(i);
            Point b = p.getVertex((i + 1) % n);
            sum += (long) a.x() * b.y() - (long) b.x() * a.y();
        }
        return sum / 2.0;
    }

    /** True when the vertices run clockwise on screen (positive signed area). */
    public static boolean isClockwise(Polygon p) {
        return signedArea(p) > 0;
    }

    /** True when all turns have the same direction (collinear vertices are tolerated). */
    public static boolean isConvex(Polygon p) {
        int n = p.size();
        if (n < 3) return false;
        int sign = 0;
        for (int i = 0; i < n; i++) {
            Point a = p.getVertex(i);
            Point b = p.getVertex((i + 1) % n);
            Point c = p.getVertex((i + 2) % n);
            long cross = (long) (b.x() - a.x()) * (c.y() - b.y()) - (long) (b.y() - a.y()) * (c.x() - b.x());
            int s = Long.signum(cross);
            if (s == 0) continue;
            if (sign == 0) sign = s;
            else if (sign != s) return false;
        }
        return sign != 0;
    }
}
