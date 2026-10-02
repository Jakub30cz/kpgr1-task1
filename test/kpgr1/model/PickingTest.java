package kpgr1.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class PickingTest {
    private static Polygon square() {
        return new Polygon(List.of(new Point(0, 0), new Point(10, 0), new Point(10, 10), new Point(0, 10)));
    }

    @Test
    void lineEndpointHandleMovesLine() {
        Scene s = new Scene();
        s.getLines().add(new Line(new Point(0, 0), new Point(50, 50)));
        Optional<VertexHandle> h = Picking.nearestVertex(s, new Point(48, 51), 5);
        assertTrue(h.isPresent());
        assertEquals(new Point(50, 50), h.get().get());
        h.get().set(new Point(70, 80));
        assertEquals(new Line(new Point(0, 0), new Point(70, 80)), s.getLines().get(0));
    }

    @Test
    void clipRectangleCornerHandleMovesCorner() {
        Scene s = new Scene();
        s.setClipRectangle(new ClipRectangle(new Point(10, 10), new Point(50, 40)));
        VertexHandle h = Picking.nearestVertex(s, new Point(51, 41), 5).orElseThrow();
        assertEquals(new Point(50, 40), h.get());
        h.set(new Point(60, 60));
        assertEquals(60, s.getClipRectangle().maxX());
        assertEquals(60, s.getClipRectangle().maxY());
        assertEquals(10, s.getClipRectangle().minX());
    }

    @Test
    void polygonAndClipPolygonVerticesArePickable() {
        Scene s = new Scene();
        Polygon p = square();
        s.getPolygons().add(p);
        Polygon c = new Polygon(List.of(new Point(100, 100), new Point(120, 100), new Point(110, 120)));
        s.setClipPolygon(c);
        Picking.nearestVertex(s, new Point(9, 9), 3).orElseThrow().set(new Point(8, 8));
        assertEquals(new Point(8, 8), p.getVertex(2));
        Picking.nearestVertex(s, new Point(110, 121), 3).orElseThrow().set(new Point(111, 125));
        assertEquals(new Point(111, 125), c.getVertex(2));
    }

    @Test
    void nothingWithinRadiusIsEmpty() {
        Scene s = new Scene();
        s.getLines().add(new Line(new Point(0, 0), new Point(50, 50)));
        s.getPolygons().add(square());
        assertTrue(Picking.nearestVertex(s, new Point(200, 200), 5).isEmpty());
        assertTrue(Picking.nearestEdge(s, new Point(200, 200), 5).isEmpty());
        assertTrue(Picking.nearestPolygon(s, new Point(200, 200), 5).isEmpty());
    }

    @Test
    void nearestEdgeGivesInsertIndex() {
        Scene s = new Scene();
        Polygon p = square();
        s.getPolygons().add(p);
        EdgeHit top = Picking.nearestEdge(s, new Point(5, 2), 5).orElseThrow();
        assertSame(p, top.polygon());
        assertEquals(1, top.insertIndex());
        assertEquals(2.0, top.distance(), 1e-9);
        EdgeHit closing = Picking.nearestEdge(s, new Point(-1, 5), 5).orElseThrow();
        assertEquals(4, closing.insertIndex());
        assertEquals(1.0, closing.distance(), 1e-9);
    }

    @Test
    void containsConvexConcaveAndVertex() {
        Polygon sq = square();
        assertTrue(Picking.contains(sq, new Point(5, 5)));
        assertFalse(Picking.contains(sq, new Point(11, 5)));
        assertTrue(Picking.contains(sq, new Point(10, 10)));
        // concave "U" shape opening upwards
        Polygon u = new Polygon(List.of(new Point(0, 0), new Point(30, 0), new Point(30, 30), new Point(20, 30),
                new Point(20, 10), new Point(10, 10), new Point(10, 30), new Point(0, 30)));
        assertTrue(Picking.contains(u, new Point(5, 20)));
        assertFalse(Picking.contains(u, new Point(15, 20)));
        assertTrue(Picking.contains(u, new Point(15, 5)));
    }

    @Test
    void topmostAndNearestPolygon() {
        Scene s = new Scene();
        Polygon a = square();
        Polygon b = new Polygon(List.of(new Point(20, 0), new Point(30, 0), new Point(30, 10)));
        s.getPolygons().add(a);
        s.getPolygons().add(b);
        assertSame(a, Picking.topmostContaining(s, new Point(5, 5)).orElseThrow());
        assertTrue(Picking.topmostContaining(s, new Point(15, 5)).isEmpty());
        assertSame(a, Picking.nearestPolygon(s, new Point(12, 5), 3).orElseThrow());
        assertSame(b, Picking.nearestPolygon(s, new Point(18, 2), 3).orElseThrow());
    }

    @Test
    void topmostIsLastInList() {
        Scene s = new Scene();
        Polygon a = square();
        Polygon b = square();
        s.getPolygons().add(a);
        s.getPolygons().add(b);
        assertSame(b, Picking.topmostContaining(s, new Point(5, 5)).orElseThrow());
    }
}
