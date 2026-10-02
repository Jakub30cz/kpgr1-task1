package kpgr1.clip;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import kpgr1.model.Point;
import kpgr1.model.Polygon;
import org.junit.jupiter.api.Test;

class PolygonGeometryTest {
    private static Polygon pentagon() {
        return new Polygon(List.of(new Point(50, 0), new Point(100, 40), new Point(80, 100),
                new Point(20, 100), new Point(0, 40)));
    }

    @Test
    void signedAreaFlipsAfterReverse() {
        Polygon p = pentagon();
        double a = PolygonGeometry.signedArea(p);
        p.reverse();
        assertEquals(-a, PolygonGeometry.signedArea(p), 1e-9);
        assertNotEquals(0, a);
    }

    @Test
    void clockwiseOnScreenHasPositiveArea() {
        Polygon sq = new Polygon(List.of(new Point(0, 0), new Point(10, 0), new Point(10, 10), new Point(0, 10)));
        assertEquals(100.0, PolygonGeometry.signedArea(sq), 1e-9);
        assertTrue(PolygonGeometry.isClockwise(sq));
        sq.reverse();
        assertFalse(PolygonGeometry.isClockwise(sq));
    }

    @Test
    void pentagonIsConvexInBothOrientations() {
        Polygon p = pentagon();
        assertTrue(PolygonGeometry.isConvex(p));
        p.reverse();
        assertTrue(PolygonGeometry.isConvex(p));
    }

    @Test
    void arrowIsNotConvex() {
        Polygon arrow = new Polygon(List.of(new Point(0, 0), new Point(20, 10), new Point(0, 20), new Point(5, 10)));
        assertFalse(PolygonGeometry.isConvex(arrow));
    }
}
