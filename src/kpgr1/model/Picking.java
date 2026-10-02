package kpgr1.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Pure hit-testing helpers over a {@link Scene}. */
public final class Picking {
    private Picking() {}

    /** Nearest editable vertex within {@code maxDist}: line endpoints, polygon, clip-rectangle and clip-polygon vertices. */
    public static Optional<VertexHandle> nearestVertex(Scene scene, Point p, double maxDist) {
        VertexHandle best = null;
        double bestDist = Double.MAX_VALUE;
        for (VertexHandle h : handles(scene)) {
            double d = dist(h.get(), p);
            if (d <= maxDist && d < bestDist) {
                bestDist = d;
                best = h;
            }
        }
        return Optional.ofNullable(best);
    }

    private static List<VertexHandle> handles(Scene scene) {
        List<VertexHandle> result = new ArrayList<>();
        List<Line> lines = scene.getLines();
        for (int i = 0; i < lines.size(); i++) {
            final int idx = i;
            result.add(new VertexHandle() {
                public Point get() { return lines.get(idx).a(); }
                public void set(Point q) { lines.set(idx, new Line(q, lines.get(idx).b())); }
            });
            result.add(new VertexHandle() {
                public Point get() { return lines.get(idx).b(); }
                public void set(Point q) { lines.set(idx, new Line(lines.get(idx).a(), q)); }
            });
        }
        for (Polygon poly : scene.getPolygons()) addPolygonHandles(result, poly);
        if (scene.getClipPolygon() != null) addPolygonHandles(result, scene.getClipPolygon());
        ClipRectangle rect = scene.getClipRectangle();
        if (rect != null) {
            for (int i = 0; i < 4; i++) {
                final int idx = i;
                result.add(new VertexHandle() {
                    public Point get() { return rect.corners().get(idx); }
                    public void set(Point q) { rect.moveCorner(idx, q); }
                });
            }
        }
        return result;
    }

    private static void addPolygonHandles(List<VertexHandle> out, Polygon poly) {
        for (int i = 0; i < poly.size(); i++) {
            final int idx = i;
            out.add(new VertexHandle() {
                public Point get() { return poly.getVertex(idx); }
                public void set(Point q) { poly.setVertex(idx, q); }
            });
        }
    }

    /** Nearest edge of any polygon or the clip polygon within {@code maxDist}. */
    public static Optional<EdgeHit> nearestEdge(Scene scene, Point p, double maxDist) {
        EdgeHit best = null;
        List<Polygon> all = new ArrayList<>(scene.getPolygons());
        if (scene.getClipPolygon() != null) all.add(scene.getClipPolygon());
        for (Polygon poly : all) {
            EdgeHit hit = nearestEdgeOf(poly, p);
            if (hit != null && hit.distance() <= maxDist && (best == null || hit.distance() < best.distance())) {
                best = hit;
            }
        }
        return Optional.ofNullable(best);
    }

    private static EdgeHit nearestEdgeOf(Polygon poly, Point p) {
        int n = poly.size();
        if (n < 2) return null;
        EdgeHit best = null;
        for (int i = 0; i < n; i++) {
            double d = segmentDist(poly.getVertex(i), poly.getVertex((i + 1) % n), p);
            if (best == null || d < best.distance()) best = new EdgeHit(poly, i + 1, d);
        }
        return best;
    }

    /** Even-odd containment; points on a vertex or edge count as contained. */
    public static boolean contains(Polygon poly, Point p) {
        int n = poly.size();
        if (n == 0) return false;
        for (int i = 0; i < n; i++) {
            if (segmentDist(poly.getVertex(i), poly.getVertex((i + 1) % n), p) == 0) return true;
        }
        boolean inside = false;
        for (int i = 0, j = n - 1; i < n; j = i++) {
            Point a = poly.getVertex(i);
            Point b = poly.getVertex(j);
            if ((a.y() > p.y()) != (b.y() > p.y())) {
                double x = a.x() + (double) (p.y() - a.y()) * (b.x() - a.x()) / (b.y() - a.y());
                if (p.x() < x) inside = !inside;
            }
        }
        return inside;
    }

    /** Last (topmost) scene polygon containing the point. */
    public static Optional<Polygon> topmostContaining(Scene scene, Point p) {
        List<Polygon> polys = scene.getPolygons();
        for (int i = polys.size() - 1; i >= 0; i--) {
            if (contains(polys.get(i), p)) return Optional.of(polys.get(i));
        }
        return Optional.empty();
    }

    /** Scene polygon with the closest edge within {@code maxDist}. */
    public static Optional<Polygon> nearestPolygon(Scene scene, Point p, double maxDist) {
        Polygon best = null;
        double bestDist = Double.MAX_VALUE;
        for (Polygon poly : scene.getPolygons()) {
            EdgeHit hit = nearestEdgeOf(poly, p);
            if (hit != null && hit.distance() <= maxDist && hit.distance() < bestDist) {
                bestDist = hit.distance();
                best = poly;
            }
        }
        return Optional.ofNullable(best);
    }

    private static double dist(Point a, Point b) {
        return Math.hypot(a.x() - b.x(), a.y() - b.y());
    }

    private static double segmentDist(Point a, Point b, Point p) {
        double dx = b.x() - a.x();
        double dy = b.y() - a.y();
        double len2 = dx * dx + dy * dy;
        double t = len2 == 0 ? 0 : ((p.x() - a.x()) * dx + (p.y() - a.y()) * dy) / len2;
        t = Math.max(0, Math.min(1, t));
        return Math.hypot(p.x() - (a.x() + t * dx), p.y() - (a.y() + t * dy));
    }
}
