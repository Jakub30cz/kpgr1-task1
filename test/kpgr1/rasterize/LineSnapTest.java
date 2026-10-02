package kpgr1.rasterize;

import static org.junit.jupiter.api.Assertions.*;

import kpgr1.model.Point;
import org.junit.jupiter.api.Test;

class LineSnapTest {
    private static final Point S = new Point(100, 100);

    private static Point snap(int dx, int dy) {
        return LineSnap.snap(S, new Point(100 + dx, 100 + dy));
    }

    @Test
    void nearHorizontalSnapsHorizontal() {
        assertEquals(new Point(150, 100), snap(50, 5));
        assertEquals(new Point(150, 100), snap(50, -5));
        assertEquals(new Point(50, 100), snap(-50, 5));
    }

    @Test
    void nearVerticalSnapsVertical() {
        assertEquals(new Point(100, 150), snap(5, 50));
        assertEquals(new Point(100, 50), snap(-5, -50));
    }

    @Test
    void nearDiagonalSnapsToMaxAbsWithSigns() {
        assertEquals(new Point(150, 150), snap(50, 45));
        assertEquals(new Point(145, 145), snap(40, 45));
        assertEquals(new Point(50, 150), snap(-50, 45));
        assertEquals(new Point(150, 50), snap(50, -48));
        assertEquals(new Point(60, 60), snap(-40, -35));
    }

    @Test
    void exactDirectionsAreKept() {
        assertEquals(new Point(130, 100), snap(30, 0));
        assertEquals(new Point(100, 70), snap(0, -30));
        assertEquals(new Point(130, 130), snap(30, 30));
    }

    @Test
    void zeroVectorReturnsStart() {
        assertEquals(S, snap(0, 0));
    }
}
