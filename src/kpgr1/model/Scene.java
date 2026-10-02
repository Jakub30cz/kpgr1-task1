package kpgr1.model;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/** Retained scene content; holds data only, no rendering. */
public final class Scene {
    public static final int MIN_CLIP_VERTICES = 5;

    private final List<Line> lines = new ArrayList<>();
    private final List<Polygon> polygons = new ArrayList<>();
    /** Identity map; the value tells whether the fill uses the pattern. */
    private final Map<Polygon, Boolean> scanLineFills = new IdentityHashMap<>();
    private final List<SeedFillCommand> seedFills = new ArrayList<>();
    private ClipRectangle clipRectangle;
    private Polygon clipPolygon;

    public List<Line> getLines() { return lines; }

    public List<Polygon> getPolygons() { return polygons; }

    public Map<Polygon, Boolean> getScanLineFills() { return scanLineFills; }

    public List<SeedFillCommand> getSeedFills() { return seedFills; }

    public ClipRectangle getClipRectangle() { return clipRectangle; }

    public void setClipRectangle(ClipRectangle clipRectangle) { this.clipRectangle = clipRectangle; }

    public Polygon getClipPolygon() { return clipPolygon; }

    public void setClipPolygon(Polygon clipPolygon) { this.clipPolygon = clipPolygon; }

    /** Regular pentagon centered in a width x height canvas, first vertex at the top. */
    public static Polygon createDefaultClipPolygon(int width, int height) {
        double cx = width / 2.0;
        double cy = height / 2.0;
        double r = Math.min(width, height) * 0.35;
        Polygon p = new Polygon();
        for (int i = 0; i < MIN_CLIP_VERTICES; i++) {
            double a = -Math.PI / 2 + 2 * Math.PI * i / MIN_CLIP_VERTICES;
            p.addVertex(new Point((int) Math.round(cx + r * Math.cos(a)), (int) Math.round(cy + r * Math.sin(a))));
        }
        return p;
    }

    /** Removes the polygon (by identity) together with its scan-line fill. */
    public void removePolygon(Polygon polygon) {
        polygons.removeIf(p -> p == polygon);
        scanLineFills.remove(polygon);
    }

    public void clear() {
        lines.clear();
        polygons.clear();
        scanLineFills.clear();
        seedFills.clear();
        clipRectangle = null;
        clipPolygon = null;
    }
}
