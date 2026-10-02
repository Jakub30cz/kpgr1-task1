package kpgr1.clip;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import kpgr1.model.ClipRectangle;
import kpgr1.model.Line;
import kpgr1.model.Point;
import org.junit.jupiter.api.Test;

class CohenSutherlandClipperTest {
    private final ClipRectangle r = new ClipRectangle(new Point(10, 10), new Point(20, 20));

    private static Line l(int x1, int y1, int x2, int y2) {
        return new Line(new Point(x1, y1), new Point(x2, y2));
    }

    private void assertInside(Point p) {
        assertTrue(p.x() >= r.minX() && p.x() <= r.maxX() && p.y() >= r.minY() && p.y() <= r.maxY(), p.toString());
    }

    @Test
    void outcodesOfNineRegions() {
        int left = CohenSutherlandClipper.LEFT;
        int right = CohenSutherlandClipper.RIGHT;
        int bottom = CohenSutherlandClipper.BOTTOM;
        int top = CohenSutherlandClipper.TOP;
        assertEquals(1, left);
        assertEquals(2, right);
        assertEquals(4, bottom);
        assertEquals(8, top);
        // screen coordinates: TOP means y < minY, BOTTOM means y > maxY
        assertEquals(left | top, CohenSutherlandClipper.outcode(new Point(0, 0), r));
        assertEquals(top, CohenSutherlandClipper.outcode(new Point(15, 0), r));
        assertEquals(right | top, CohenSutherlandClipper.outcode(new Point(30, 0), r));
        assertEquals(left, CohenSutherlandClipper.outcode(new Point(0, 15), r));
        assertEquals(0, CohenSutherlandClipper.outcode(new Point(15, 15), r));
        assertEquals(right, CohenSutherlandClipper.outcode(new Point(30, 15), r));
        assertEquals(left | bottom, CohenSutherlandClipper.outcode(new Point(0, 30), r));
        assertEquals(bottom, CohenSutherlandClipper.outcode(new Point(15, 30), r));
        assertEquals(right | bottom, CohenSutherlandClipper.outcode(new Point(30, 30), r));
    }

    @Test
    void fullyInsideUnchanged() {
        Line line = l(12, 12, 18, 19);
        assertEquals(Optional.of(line), CohenSutherlandClipper.clip(line, r));
    }

    @Test
    void trivialRejectEachSide() {
        assertTrue(CohenSutherlandClipper.clip(l(0, 12, 5, 18), r).isEmpty());
        assertTrue(CohenSutherlandClipper.clip(l(25, 12, 30, 18), r).isEmpty());
        assertTrue(CohenSutherlandClipper.clip(l(12, 0, 18, 5), r).isEmpty());
        assertTrue(CohenSutherlandClipper.clip(l(12, 25, 18, 30), r).isEmpty());
    }

    @Test
    void crossingOneEdge() {
        Line c = CohenSutherlandClipper.clip(l(0, 15, 15, 15), r).orElseThrow();
        assertEquals(new Point(10, 15), c.a());
        assertEquals(new Point(15, 15), c.b());
    }

    @Test
    void crossingTwoEdges() {
        Line c = CohenSutherlandClipper.clip(l(0, 15, 30, 15), r).orElseThrow();
        assertEquals(new Point(10, 15), c.a());
        assertEquals(new Point(20, 15), c.b());
        Line d = CohenSutherlandClipper.clip(l(5, 5, 25, 25), r).orElseThrow();
        assertEquals(new Point(10, 10), d.a());
        assertEquals(new Point(20, 20), d.b());
    }

    @Test
    void cornerCaseMissingWindowIsEmpty() {
        // endpoints in the LEFT and BOTTOM regions (AND == 0), but the line passes outside the corner
        assertTrue(CohenSutherlandClipper.clip(l(5, 18, 12, 25), r).isEmpty());
    }

    @Test
    void verticalAndHorizontal() {
        Line v = CohenSutherlandClipper.clip(l(15, 0, 15, 30), r).orElseThrow();
        assertEquals(new Point(15, 10), v.a());
        assertEquals(new Point(15, 20), v.b());
        Line h = CohenSutherlandClipper.clip(l(30, 12, 0, 12), r).orElseThrow();
        assertEquals(new Point(20, 12), h.a());
        assertEquals(new Point(10, 12), h.b());
    }

    @Test
    void resultsLieInsideRectangle() {
        Line[] lines = {l(0, 0, 30, 25), l(-5, 22, 24, 3), l(15, 0, 40, 18), l(2, 14, 18, 40)};
        for (Line line : lines) {
            CohenSutherlandClipper.clip(line, r).ifPresent(c -> {
                assertInside(c.a());
                assertInside(c.b());
            });
        }
    }
}
