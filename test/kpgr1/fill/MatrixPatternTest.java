package kpgr1.fill;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class MatrixPatternTest {
    @Test
    void tilesOverPlane() {
        MatrixPattern p = new MatrixPattern(new int[][] {{1, 2, 3}, {4, 5, 6}});
        assertEquals(1, p.colorAt(0, 0));
        assertEquals(6, p.colorAt(2, 1));
        assertEquals(1, p.colorAt(3, 2));
        assertEquals(5, p.colorAt(4, 3));
    }

    @Test
    void negativeCoordinatesWrap() {
        MatrixPattern p = new MatrixPattern(new int[][] {{1, 2, 3}, {4, 5, 6}});
        assertEquals(3, p.colorAt(-1, 0));
        assertEquals(4, p.colorAt(0, -1));
        assertEquals(6, p.colorAt(-1, -1));
    }

    @Test
    void checkerAlternatesByCell() {
        MatrixPattern p = MatrixPattern.checker(0xA, 0xB, 2);
        assertEquals(0xA, p.colorAt(0, 0));
        assertEquals(0xA, p.colorAt(1, 1));
        assertEquals(0xB, p.colorAt(2, 0));
        assertEquals(0xB, p.colorAt(0, 2));
        assertEquals(0xA, p.colorAt(2, 2));
        assertEquals(0xA, p.colorAt(4, 0));
    }

    @Test
    void emptyMatrixRejected() {
        assertThrows(IllegalArgumentException.class, () -> new MatrixPattern(new int[0][0]));
        assertThrows(IllegalArgumentException.class, () -> new MatrixPattern(new int[][] {{}}));
    }
}
