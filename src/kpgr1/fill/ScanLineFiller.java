package kpgr1.fill;

import java.util.ArrayList;
import java.util.List;
import kpgr1.model.Point;
import kpgr1.model.Polygon;
import kpgr1.raster.Raster;
import kpgr1.rasterize.LineRasterizer;

/** Scan-line polygon fill; works for concave polygons. */
public final class ScanLineFiller implements Filler {
    private final Raster raster;
    private final Polygon polygon;
    private final Pattern pattern;
    private final LineRasterizer outline;
    private final int boundaryColor;

    public ScanLineFiller(Raster raster, Polygon polygon, Pattern pattern, LineRasterizer outline, int boundaryColor) {
        this.raster = raster;
        this.polygon = polygon;
        this.pattern = pattern;
        this.outline = outline;
        this.boundaryColor = boundaryColor;
    }

    @Override
    public void fill() {
        int n = polygon.size();
        if (n >= 3) fillInterior();
        drawOutline();
    }

    private void fillInterior() {
        int n = polygon.size();

        // Build edges between consecutive vertices; Edge.of drops horizontal ones
        // and orients the rest top-down (y1 < y2) with x = k*y + q.
        List<Edge> edges = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            Point a = polygon.getVertex(i);
            Point b = polygon.getVertex((i + 1) % n);
            Edge e = Edge.of(a.x(), a.y(), b.x(), b.y());
            if (e != null) edges.add(e);
        }
        if (edges.isEmpty()) return;

        // Find the vertical extent of the polygon.
        int yMin = Integer.MAX_VALUE;
        int yMax = Integer.MIN_VALUE;
        for (Edge e : edges) {
            yMin = Math.min(yMin, e.y1);
            yMax = Math.max(yMax, e.y2);
        }
        yMin = Math.max(yMin, 0);
        yMax = Math.min(yMax, raster.getHeight() - 1);

        int[] xs = new int[edges.size()];
        for (int y = yMin; y <= yMax; y++) {
            // Collect intersections; Edge.covers shortens each edge by 1 px
            // (y >= y1 && y < y2) so a shared vertex is counted once.
            int count = 0;
            for (Edge e : edges) {
                if (e.covers(y)) xs[count++] = e.xAt(y);
            }

            // Sort intersections with our own insertion sort.
            IntSort.insertionSort(xs, count);

            // Fill between pairs of intersections (inclusive) with the pattern.
            for (int i = 0; i + 1 < count; i += 2) {
                for (int x = xs[i]; x <= xs[i + 1]; x++) {
                    if (raster.contains(x, y)) raster.setPixel(x, y, pattern.colorAt(x, y));
                }
            }
        }
    }

    /** Redraws the polygon outline on top of the fill in the boundary color. */
    private void drawOutline() {
        int n = polygon.size();
        if (n < 2) return;
        // Two vertices form a single segment; otherwise close the polygon.
        int segments = n == 2 ? 1 : n;
        for (int i = 0; i < segments; i++) {
            Point a = polygon.getVertex(i);
            Point b = polygon.getVertex((i + 1) % n);
            outline.rasterize(a.x(), a.y(), b.x(), b.y(), boundaryColor);
        }
    }
}
