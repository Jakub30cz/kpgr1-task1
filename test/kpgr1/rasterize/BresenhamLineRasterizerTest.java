package kpgr1.rasterize;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import kpgr1.model.Line;
import kpgr1.model.Point;
import kpgr1.raster.ArrayRaster;
import org.junit.jupiter.api.Test;

class BresenhamLineRasterizerTest {
    private static final int COLOR = 0x123456;
    private static final int OFFSET = 50;

    private static List<int[]> draw(int x1, int y1, int x2, int y2) {
        ArrayRaster r = new ArrayRaster(120, 120);
        new BresenhamLineRasterizer(r, 0xFFFFFF).rasterize(x1 + OFFSET, y1 + OFFSET, x2 + OFFSET, y2 + OFFSET, COLOR);
        List<int[]> pixels = new ArrayList<>();
        for (int y = 0; y < 120; y++)
            for (int x = 0; x < 120; x++)
                if (r.getPixel(x, y) == COLOR) pixels.add(new int[] {x - OFFSET, y - OFFSET});
        return pixels;
    }

    private static boolean has(List<int[]> px, int x, int y) {
        return px.stream().anyMatch(p -> p[0] == x && p[1] == y);
    }

    private static void assertValidLine(int x1, int y1, int x2, int y2) {
        List<int[]> px = draw(x1, y1, x2, y2);
        int dx = x2 - x1, dy = y2 - y1;
        int steps = Math.max(Math.abs(dx), Math.abs(dy));
        assertEquals(steps + 1, px.size(), "pixel count");
        assertTrue(has(px, x1, y1), "start set");
        assertTrue(has(px, x2, y2), "end set");
        if (steps > 0) {
            for (int[] p : px) {
                boolean neighbour = px.stream().anyMatch(q -> q != p
                        && Math.abs(q[0] - p[0]) <= 1 && Math.abs(q[1] - p[1]) <= 1);
                assertTrue(neighbour, "8-connected");
            }
        }
        boolean xMajor = Math.abs(dx) >= Math.abs(dy);
        for (int i = 0; i <= steps; i++) {
            int major = xMajor ? x1 + Integer.signum(dx) * i : y1 + Integer.signum(dy) * i;
            long count = px.stream().filter(p -> p[xMajor ? 0 : 1] == major).count();
            assertEquals(1, count, "one pixel per major step " + major);
        }
        for (int[] p : px) {
            double ideal = xMajor
                    ? y1 + (double) dy * (p[0] - x1) / dx
                    : x1 + (double) dx * (p[1] - y1) / dy;
            double actual = xMajor ? p[1] : p[0];
            assertTrue(Math.abs(actual - ideal) <= 0.5 + 1e-9, "within 0.5 px: " + p[0] + "," + p[1]);
        }
    }

    @Test
    void degenerateIsSinglePixel() {
        List<int[]> px = draw(5, 7, 5, 7);
        assertEquals(1, px.size());
        assertTrue(has(px, 5, 7));
    }

    @Test
    void horizontalVerticalAndDiagonals() {
        assertValidLine(0, 0, 20, 0);
        assertValidLine(0, 0, -20, 0);
        assertValidLine(0, 0, 0, 20);
        assertValidLine(0, 0, 0, -20);
        assertValidLine(0, 0, 15, 15);
        assertValidLine(0, 0, -15, 15);
        assertValidLine(0, 0, 15, -15);
        assertValidLine(0, 0, -15, -15);
    }

    @Test
    void onePerOctant() {
        int[][] ends = {{30, 10}, {10, 30}, {-10, 30}, {-30, 10}, {-30, -10}, {-10, -30}, {10, -30}, {30, -10}};
        for (int[] e : ends) assertValidLine(0, 0, e[0], e[1]);
    }

    @Test
    void oddSlopes() {
        assertValidLine(3, 4, 40, 17);
        assertValidLine(-7, 2, 22, -35);
        assertValidLine(1, 1, 2, 40);
    }

    @Test
    void reversedEndpointsGiveSameCountAndEndpoints() {
        int[][] lines = {{0, 0, 30, 10}, {0, 0, -10, 30}, {5, 5, -25, -12}, {0, 0, 7, 7}};
        for (int[] l : lines) {
            List<int[]> a = draw(l[0], l[1], l[2], l[3]);
            List<int[]> b = draw(l[2], l[3], l[0], l[1]);
            assertEquals(a.size(), b.size());
            assertTrue(has(b, l[0], l[1]));
            assertTrue(has(b, l[2], l[3]));
        }
    }

    @Test
    void lineOverloadUsesCurrentColor() {
        ArrayRaster r = new ArrayRaster(10, 10);
        BresenhamLineRasterizer br = new BresenhamLineRasterizer(r, 0x00FF00);
        br.rasterize(new Line(new Point(1, 1), new Point(4, 1)));
        assertEquals(0x00FF00, r.getPixel(3, 1));
    }
}
