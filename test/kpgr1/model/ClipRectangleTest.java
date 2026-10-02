package kpgr1.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class ClipRectangleTest {
    @Test
    void normalizesMinMax() {
        ClipRectangle r = new ClipRectangle(new Point(10, 20), new Point(2, 4));
        assertEquals(2, r.minX());
        assertEquals(4, r.minY());
        assertEquals(10, r.maxX());
        assertEquals(20, r.maxY());
    }

    @Test
    void cornersAreTlTrBrBl() {
        ClipRectangle r = new ClipRectangle(new Point(2, 4), new Point(10, 20));
        assertEquals(List.of(new Point(2, 4), new Point(10, 4), new Point(10, 20), new Point(2, 20)), r.corners());
    }

    @Test
    void moveCornerKeepsOppositeCorner() {
        for (int i = 0; i < 4; i++) {
            ClipRectangle r = new ClipRectangle(new Point(2, 4), new Point(10, 20));
            Point opposite = r.corners().get((i + 2) % 4);
            r.moveCorner(i, new Point(30, 40));
            assertTrue(r.corners().contains(opposite), "corner " + i);
            assertTrue(r.corners().contains(new Point(30, 40)), "corner " + i);
        }
    }

    @Test
    void moveCornerPastOppositeRenormalizes() {
        ClipRectangle r = new ClipRectangle(new Point(2, 4), new Point(10, 20));
        r.moveCorner(0, new Point(15, 25));
        assertEquals(10, r.minX());
        assertEquals(20, r.minY());
        assertEquals(15, r.maxX());
        assertEquals(25, r.maxY());
    }
}
