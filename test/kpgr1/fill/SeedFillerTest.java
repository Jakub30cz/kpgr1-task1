package kpgr1.fill;

import static org.junit.jupiter.api.Assertions.*;

import kpgr1.model.Point;
import kpgr1.raster.ArrayRaster;
import org.junit.jupiter.api.Test;

class SeedFillerTest {
    private static final int BG = 0x000000;
    private static final int BORDER = 0xFF0000;
    private static final int FILL = 0x00FF00;
    private static final int STROKE = 0x0000FF;

    private static void square(ArrayRaster r, int x0, int y0, int x1, int y1, int color) {
        for (int x = x0; x <= x1; x++) { r.setPixel(x, y0, color); r.setPixel(x, y1, color); }
        for (int y = y0; y <= y1; y++) { r.setPixel(x0, y, color); r.setPixel(x1, y, color); }
    }

    private static ArrayRaster squareWithStroke() {
        ArrayRaster r = new ArrayRaster(20, 20);
        square(r, 5, 5, 14, 14, BORDER);
        for (int x = 6; x < 14; x++) r.setPixel(x, 9, STROKE);
        return r;
    }

    private static SeedFiller filler(ArrayRaster r, Point seed, Pattern p, SeedFillMode mode) {
        return new SeedFiller(r, seed, p, mode, BORDER, BG);
    }

    @Test
    void closedSquareFilledInsideOnly() {
        ArrayRaster r = new ArrayRaster(20, 20);
        square(r, 5, 5, 14, 14, BORDER);
        filler(r, new Point(10, 10), new SolidPattern(FILL), SeedFillMode.STOP_ON_BOUNDARY).fill();
        for (int y = 0; y < 20; y++)
            for (int x = 0; x < 20; x++) {
                boolean inside = x > 5 && x < 14 && y > 5 && y < 14;
                boolean border = x >= 5 && x <= 14 && y >= 5 && y <= 14 && !inside;
                assertEquals(inside ? FILL : border ? BORDER : BG, r.getPixel(x, y), x + "," + y);
            }
    }

    @Test
    void backgroundOnlyStopsAtAnyNonBackgroundPixel() {
        ArrayRaster r = squareWithStroke();
        filler(r, new Point(10, 7), new SolidPattern(FILL), SeedFillMode.FILL_BACKGROUND_ONLY).fill();
        assertEquals(FILL, r.getPixel(10, 7));
        assertEquals(STROKE, r.getPixel(10, 9));
        assertEquals(BG, r.getPixel(10, 11), "area behind the stroke stays untouched");
    }

    @Test
    void stopOnBoundaryCrossesDifferentlyColoredStroke() {
        ArrayRaster r = squareWithStroke();
        filler(r, new Point(10, 7), new SolidPattern(FILL), SeedFillMode.STOP_ON_BOUNDARY).fill();
        assertEquals(FILL, r.getPixel(10, 9));
        assertEquals(FILL, r.getPixel(10, 11));
    }

    @Test
    void openBoundaryLeaksToRasterEdge() {
        ArrayRaster r = new ArrayRaster(20, 20);
        square(r, 5, 5, 14, 14, BORDER);
        r.setPixel(14, 10, BG); // gap
        assertDoesNotThrow(() ->
            filler(r, new Point(10, 10), new SolidPattern(FILL), SeedFillMode.STOP_ON_BOUNDARY).fill());
        assertEquals(FILL, r.getPixel(0, 0));
        assertEquals(FILL, r.getPixel(19, 19));
        assertEquals(BORDER, r.getPixel(5, 5));
    }

    @Test
    void largeEmptyRasterDoesNotOverflowStack() {
        ArrayRaster r = new ArrayRaster(1000, 1000);
        assertDoesNotThrow(() ->
            filler(r, new Point(500, 500), new SolidPattern(FILL), SeedFillMode.STOP_ON_BOUNDARY).fill());
        assertEquals(FILL, r.getPixel(0, 0));
        assertEquals(FILL, r.getPixel(999, 999));
    }

    @Test
    void checkerPatternMatchesPatternColorAt() {
        ArrayRaster r = new ArrayRaster(12, 12);
        MatrixPattern p = MatrixPattern.checker(0x111111, 0x222222, 2);
        filler(r, new Point(3, 3), p, SeedFillMode.STOP_ON_BOUNDARY).fill();
        for (int y = 0; y < 12; y++)
            for (int x = 0; x < 12; x++) assertEquals(p.colorAt(x, y), r.getPixel(x, y), x + "," + y);
    }

    @Test
    void patternEqualToBackgroundStillTerminates() {
        ArrayRaster r = new ArrayRaster(10, 10);
        assertTimeoutPreemptively(java.time.Duration.ofSeconds(5), () ->
            filler(r, new Point(5, 5), new SolidPattern(BG), SeedFillMode.FILL_BACKGROUND_ONLY).fill());
        assertEquals(BG, r.getPixel(0, 0));
    }

    @Test
    void patternContainingBoundaryColorTerminates() {
        ArrayRaster r = new ArrayRaster(10, 10);
        MatrixPattern p = MatrixPattern.checker(BORDER, FILL, 1);
        assertTimeoutPreemptively(java.time.Duration.ofSeconds(5), () ->
            filler(r, new Point(5, 5), p, SeedFillMode.STOP_ON_BOUNDARY).fill());
        for (int y = 0; y < 10; y++)
            for (int x = 0; x < 10; x++) assertEquals(p.colorAt(x, y), r.getPixel(x, y));
    }

    @Test
    void seedOutsideOrOnBoundaryIsNoOp() {
        ArrayRaster r = new ArrayRaster(10, 10);
        r.setPixel(5, 5, BORDER);
        SolidPattern p = new SolidPattern(FILL);
        filler(r, new Point(-1, 3), p, SeedFillMode.STOP_ON_BOUNDARY).fill();
        filler(r, new Point(10, 3), p, SeedFillMode.STOP_ON_BOUNDARY).fill();
        filler(r, new Point(5, 5), p, SeedFillMode.STOP_ON_BOUNDARY).fill();
        assertEquals(BG, r.getPixel(0, 0));
        assertEquals(BORDER, r.getPixel(5, 5));
    }
}
