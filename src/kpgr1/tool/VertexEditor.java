package kpgr1.tool;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import kpgr1.model.EdgeHit;
import kpgr1.model.Picking;
import kpgr1.model.Point;
import kpgr1.model.Polygon;
import kpgr1.model.Scene;
import kpgr1.model.VertexHandle;

/** Vertex and polygon editing shared by every mode (right mouse button, Ctrl+click, Delete, X). */
public final class VertexEditor {
    /** Pick radius in pixels. */
    public static final double PICK_RADIUS = 10;

    private final Scene scene;
    private VertexHandle grabbed;

    public VertexEditor(Scene scene) {
        this.scene = scene;
    }

    /** Grabs the nearest vertex within the pick radius; returns whether one was found. */
    public boolean grab(Point p) {
        grabbed = Picking.nearestVertex(scene, p, PICK_RADIUS).orElse(null);
        return grabbed != null;
    }

    public void drag(Point p) {
        if (grabbed != null) grabbed.set(p);
    }

    public void release() {
        grabbed = null;
    }

    /** Inserts a vertex at {@code p} onto the nearest polygon or clip-polygon edge. */
    public boolean insertOnNearestEdge(Point p) {
        Optional<EdgeHit> hit = Picking.nearestEdge(scene, p, PICK_RADIUS);
        hit.ifPresent(h -> h.polygon().insertVertex(h.insertIndex(), p));
        return hit.isPresent();
    }

    /**
     * Deletes the polygon or clip-polygon vertex nearest to {@code p}. A polygon left with fewer than 3 vertices is
     * removed; the clip polygon refuses to shrink below {@link Scene#MIN_CLIP_VERTICES}.
     *
     * @return empty string on success or when nothing is near, otherwise a Czech status message
     */
    public String deleteNearestVertex(Point p) {
        Polygon best = null;
        int bestIndex = -1;
        double bestDist = Double.MAX_VALUE;
        List<Polygon> all = new ArrayList<>(scene.getPolygons());
        if (scene.getClipPolygon() != null) all.add(scene.getClipPolygon());
        for (Polygon poly : all) {
            for (int i = 0; i < poly.size(); i++) {
                Point v = poly.getVertex(i);
                double d = Math.hypot(v.x() - p.x(), v.y() - p.y());
                if (d <= PICK_RADIUS && d < bestDist) {
                    bestDist = d;
                    best = poly;
                    bestIndex = i;
                }
            }
        }
        if (best == null) return "";
        if (best == scene.getClipPolygon()) {
            if (best.size() <= Scene.MIN_CLIP_VERTICES) {
                return "Ořezávací polygon musí mít alespoň " + Scene.MIN_CLIP_VERTICES + " vrcholů.";
            }
            best.removeVertex(bestIndex);
            return "";
        }
        best.removeVertex(bestIndex);
        if (best.size() < 3) scene.removePolygon(best);
        return "";
    }

    /** Deletes the scene polygon whose edge is nearest to {@code p}; returns whether one was removed. */
    public boolean deleteNearestPolygon(Point p) {
        Optional<Polygon> poly = Picking.nearestPolygon(scene, p, PICK_RADIUS);
        poly.ifPresent(scene::removePolygon);
        return poly.isPresent();
    }
}
