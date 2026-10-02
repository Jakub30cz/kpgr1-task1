package kpgr1.fill;

import static org.junit.jupiter.api.Assertions.assertEquals;

import kpgr1.model.Point;
import kpgr1.model.Polygon;
import kpgr1.raster.ArrayRaster;
import kpgr1.raster.Raster;
import kpgr1.rasterize.LineRasterizer;
import org.junit.jupiter.api.Test;

class ScanLineFillerTest {
    private static final int BG = 0, FILL = 0xFF0000, BORDER = 0x00FF00;

    /** Test-local rasterizer: draws nothing. */
    private static final class NoopLine extends LineRasterizer {
        NoopLine(Raster r) { super(r, 0); }

        @Override public void rasterize(int x1, int y1, int x2, int y2, int color) {}
    }

    /** Test-local rasterizer: marks only the two endpoints. */
    private static final class EndpointLine extends LineRasterizer {
        EndpointLine(Raster r) { super(r, 0); }

        @Override public void rasterize(int x1, int y1, int x2, int y2, int color) {
            raster.setPixel(x1, y1, color);
            raster.setPixel(x2, y2, color);
        }
    }

    private static Polygon poly(int... xy) {
        Polygon p = new Polygon();
        for (int i = 0; i < xy.length; i += 2) p.addVertex(new Point(xy[i], xy[i + 1]));
        return p;
    }

    private static ArrayRaster fill(Polygon p, Pattern pat, boolean endpoints) {
        ArrayRaster r = new ArrayRaster(20, 20);
        r.clear(BG);
        LineRasterizer o = endpoints ? new EndpointLine(r) : new NoopLine(r);
        new ScanLineFiller(r, p, pat, o, BORDER).fill();
        return r;
    }

    private static int count(Raster r, int color) {
        int n = 0;
        for (int y = 0; y < r.getHeight(); y++)
            for (int x = 0; x < r.getWidth(); x++) if (r.getPixel(x, y) == color) n++;
        return n;
    }

    private static int rowCount(Raster r, int y, int color) {
        int n = 0;
        for (int x = 0; x < r.getWidth(); x++) if (r.getPixel(x, y) == color) n++;
        return n;
    }

    private static void assertContiguousRows(Raster r, int yFrom, int yTo) {
        for (int y = yFrom; y <= yTo; y++) {
            int first = -1, last = -1, n = 0;
            for (int x = 0; x < r.getWidth(); x++) {
                if (r.getPixel(x, y) == FILL) {
                    if (first < 0) first = x;
                    last = x;
                    n++;
                }
            }
            if (n > 0) assertEquals(last - first + 1, n, "row " + y + " must be one contiguous span");
        }
    }

    @Test void rectangleAreaExact() {
        ArrayRaster r = fill(poly(2, 2, 6, 2, 6, 5, 2, 5), new SolidPattern(FILL), false);
        // Rows 2..4 (bottom edge shortened by 1 px), columns 2..6 inclusive.
        assertEquals(3 * 5, count(r, FILL));
        assertEquals(FILL, r.getPixel(2, 2));
        assertEquals(FILL, r.getPixel(6, 4));
        assertEquals(BG, r.getPixel(2, 5));
        assertEquals(BG, r.getPixel(7, 3));
    }

    @Test void triangleWithHorizontalTopEdge() {
        ArrayRaster r = fill(poly(2, 2, 10, 2, 6, 6), new SolidPattern(FILL), false);
        // Row 2: 2..10, row 3: 3..9, row 4: 4..8, row 5: 5..7; apex row 6 excluded.
        assertEquals(9, rowCount(r, 2, FILL));
        assertEquals(7, rowCount(r, 3, FILL));
        assertEquals(5, rowCount(r, 4, FILL));
        assertEquals(3, rowCount(r, 5, FILL));
        assertEquals(0, rowCount(r, 6, FILL));
        assertEquals(24, count(r, FILL));
    }

    @Test void sharedVertexNotDoubleCounted() {
        // Apex (5,8) joins two edges; no stray span anywhere.
        ArrayRaster r = fill(poly(1, 2, 9, 2, 5, 8), new SolidPattern(FILL), false);
        assertContiguousRows(r, 0, 19);
        assertEquals(BG, r.getPixel(5, 8));
    }

    @Test void passThroughVertexNotDoubleCounted() {
        // Vertex (6,5) continues the right-hand chain downwards: one span per row.
        ArrayRaster r = fill(poly(2, 2, 6, 5, 4, 9, 0, 9), new SolidPattern(FILL), false);
        assertContiguousRows(r, 0, 19);
        assertEquals(1, rowCount(r, 5, FILL) > 0 ? 1 : 0);
    }

    @Test void concaveNotchStaysEmpty() {
        // "M"-like: two peaks at the top, notch between them reaching down to y=6.
        ArrayRaster r = fill(poly(0, 0, 4, 0, 6, 6, 8, 0, 12, 0, 12, 10, 0, 10), new SolidPattern(FILL), false);
        assertEquals(BG, r.getPixel(6, 1));
        assertEquals(BG, r.getPixel(6, 3));
        assertEquals(FILL, r.getPixel(6, 7));
        assertEquals(FILL, r.getPixel(1, 1));
        assertEquals(FILL, r.getPixel(11, 1));
    }

    @Test void patternColorsUsed() {
        Pattern checker = (x, y) -> ((x + y) % 2 == 0) ? 0x111111 : 0x222222;
        ArrayRaster r = fill(poly(2, 2, 6, 2, 6, 5, 2, 5), checker, false);
        assertEquals(0x111111, r.getPixel(2, 2));
        assertEquals(0x222222, r.getPixel(3, 2));
        assertEquals(0x111111, r.getPixel(3, 3));
    }

    @Test void outlineDrawnInBoundaryColorOnTop() {
        ArrayRaster r = fill(poly(2, 2, 6, 2, 6, 5, 2, 5), new SolidPattern(FILL), true);
        assertEquals(BORDER, r.getPixel(2, 2));
        assertEquals(BORDER, r.getPixel(6, 2));
        assertEquals(BORDER, r.getPixel(6, 5));
        assertEquals(BORDER, r.getPixel(2, 5));
        assertEquals(FILL, r.getPixel(4, 3));
    }

    @Test void fewerThanThreeVerticesOnlyOutline() {
        ArrayRaster r = fill(poly(2, 2, 8, 2), new SolidPattern(FILL), true);
        assertEquals(0, count(r, FILL));
        assertEquals(BORDER, r.getPixel(2, 2));
        assertEquals(BORDER, r.getPixel(8, 2));
    }
}
