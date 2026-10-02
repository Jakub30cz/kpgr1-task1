package kpgr1.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class PolygonTest {
    private static final Point A = new Point(0, 0), B = new Point(1, 0), C = new Point(1, 1), D = new Point(5, 5);

    @Test
    void addInsertSetRemove() {
        Polygon p = new Polygon();
        p.addVertex(A);
        p.addVertex(C);
        p.insertVertex(1, B);
        assertEquals(List.of(A, B, C), p.getVertices());
        p.setVertex(1, D);
        assertEquals(D, p.getVertex(1));
        p.removeVertex(0);
        assertEquals(2, p.size());
        assertEquals(List.of(D, C), p.getVertices());
    }

    @Test
    void reverseReversesOrder() {
        Polygon p = new Polygon(List.of(A, B, C));
        p.reverse();
        assertEquals(List.of(C, B, A), p.getVertices());
    }

    @Test
    void getVerticesIsUnmodifiableCopy() {
        Polygon p = new Polygon(List.of(A, B));
        List<Point> copy = p.getVertices();
        assertThrows(UnsupportedOperationException.class, () -> copy.add(C));
        p.addVertex(C);
        assertEquals(2, copy.size());
        assertEquals(3, p.size());
    }

    @Test
    void constructorCopiesInput() {
        List<Point> src = new ArrayList<>(List.of(A, B));
        Polygon p = new Polygon(src);
        src.add(C);
        assertEquals(2, p.size());
    }
}
