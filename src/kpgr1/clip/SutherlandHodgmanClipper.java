package kpgr1.clip;

import java.util.ArrayList;
import java.util.List;
import kpgr1.model.Point;
import kpgr1.model.Polygon;

/**
 * Sutherland-Hodgman polygon clipping against a convex clip polygon of either orientation.
 * The subject may be concave or self-intersecting; an empty result is a Polygon with 0 vertices.
 */
public final class SutherlandHodgmanClipper {
    private SutherlandHodgmanClipper() {}

    public static Polygon clip(Polygon subject, Polygon clip) {
        int m = clip.size();
        if (m < 3 || subject.size() == 0) return new Polygon();
        // Orientation sign of the clip polygon: the inside is on the side where cross * sign >= 0.
        int sign = PolygonGeometry.signedArea(clip) >= 0 ? 1 : -1;
        List<Point> output = subject.getVertices();
        for (int e = 0; e < m && !output.isEmpty(); e++) {
            Point a = clip.getVertex(e);
            Point b = clip.getVertex((e + 1) % m);
            List<Point> input = output;
            output = new ArrayList<>();
            Point prev = input.get(input.size() - 1);
            for (Point cur : input) {
                boolean curIn = inside(a, b, cur, sign);
                boolean prevIn = inside(a, b, prev, sign);
                if (curIn) {
                    if (!prevIn) output.add(intersect(a, b, prev, cur));
                    output.add(cur);
                } else if (prevIn) {
                    output.add(intersect(a, b, prev, cur));
                }
                prev = cur;
            }
        }
        return new Polygon(output);
    }

    private static boolean inside(Point a, Point b, Point p, int sign) {
        long cross = (long) (b.x() - a.x()) * (p.y() - a.y()) - (long) (b.y() - a.y()) * (p.x() - a.x());
        return cross * sign >= 0;
    }

    /** Intersection of the infinite line a-b with segment p-q, rounded to the integer grid. */
    private static Point intersect(Point a, Point b, Point p, Point q) {
        double dx = b.x() - a.x(), dy = b.y() - a.y();
        double sp = dx * (p.y() - a.y()) - dy * (p.x() - a.x());
        double sq = dx * (q.y() - a.y()) - dy * (q.x() - a.x());
        double t = sp / (sp - sq);
        return new Point((int) Math.round(p.x() + t * (q.x() - p.x())),
                (int) Math.round(p.y() + t * (q.y() - p.y())));
    }
}
