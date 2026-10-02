package kpgr1.tool;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import kpgr1.model.ClipRectangle;
import kpgr1.model.Line;
import kpgr1.model.Point;
import kpgr1.model.Polygon;
import kpgr1.model.Scene;
import org.junit.jupiter.api.Test;

class VertexEditorTest {
    private static Polygon square() {
        return new Polygon(List.of(new Point(0, 0), new Point(100, 0), new Point(100, 100), new Point(0, 100)));
    }

    @Test
    void dragMovesPolygonVertex() {
        Scene s = new Scene();
        Polygon p = square();
        s.getPolygons().add(p);
        VertexEditor e = new VertexEditor(s);
        assertTrue(e.grab(new Point(103, 98)));
        e.drag(new Point(120, 130));
        e.release();
        e.drag(new Point(0, 0)); // after release nothing moves
        assertEquals(new Point(120, 130), p.getVertex(2));
    }

    @Test
    void grabFarFromEverythingFails() {
        Scene s = new Scene();
        s.getPolygons().add(square());
        assertFalse(new VertexEditor(s).grab(new Point(500, 500)));
    }

    @Test
    void dragMovesLineEndpoint() {
        Scene s = new Scene();
        s.getLines().add(new Line(new Point(0, 0), new Point(50, 50)));
        VertexEditor e = new VertexEditor(s);
        assertTrue(e.grab(new Point(52, 49)));
        e.drag(new Point(70, 80));
        e.release();
        assertEquals(new Line(new Point(0, 0), new Point(70, 80)), s.getLines().get(0));
    }

    @Test
    void dragMovesClipCorner() {
        Scene s = new Scene();
        s.setClipRectangle(new ClipRectangle(new Point(10, 10), new Point(50, 40)));
        VertexEditor e = new VertexEditor(s);
        assertTrue(e.grab(new Point(51, 41)));
        e.drag(new Point(90, 90));
        e.release();
        assertEquals(90, s.getClipRectangle().maxX());
        assertEquals(90, s.getClipRectangle().maxY());
        assertEquals(10, s.getClipRectangle().minX());
    }

    @Test
    void insertGoesBetweenRightPair() {
        Scene s = new Scene();
        Polygon p = square();
        s.getPolygons().add(p);
        VertexEditor e = new VertexEditor(s);
        assertTrue(e.insertOnNearestEdge(new Point(102, 50)));
        assertEquals(5, p.size());
        assertEquals(new Point(102, 50), p.getVertex(2));
        assertEquals(new Point(100, 0), p.getVertex(1));
        assertEquals(new Point(100, 100), p.getVertex(3));
    }

    @Test
    void insertOnClosingEdgeAppends() {
        Scene s = new Scene();
        Polygon p = square();
        s.getPolygons().add(p);
        assertTrue(new VertexEditor(s).insertOnNearestEdge(new Point(-2, 50)));
        assertEquals(new Point(-2, 50), p.getVertex(4));
    }

    @Test
    void insertFarFromEdgesDoesNothing() {
        Scene s = new Scene();
        s.getPolygons().add(square());
        assertFalse(new VertexEditor(s).insertOnNearestEdge(new Point(50, 50)));
    }

    @Test
    void deleteVertexRemovesIt() {
        Scene s = new Scene();
        Polygon p = square();
        s.getPolygons().add(p);
        new VertexEditor(s).deleteNearestVertex(new Point(1, 1));
        assertEquals(3, p.size());
        assertEquals(new Point(100, 0), p.getVertex(0));
    }

    @Test
    void deleteVertexRemovesPolygonBelowThree() {
        Scene s = new Scene();
        Polygon p = new Polygon(List.of(new Point(0, 0), new Point(100, 0), new Point(100, 100)));
        s.getPolygons().add(p);
        s.getScanLineFills().put(p, false);
        new VertexEditor(s).deleteNearestVertex(new Point(1, 1));
        assertTrue(s.getPolygons().isEmpty());
        assertTrue(s.getScanLineFills().isEmpty());
    }

    @Test
    void clipPolygonRefusesBelowMinimum() {
        Scene s = new Scene();
        Polygon clip = Scene.createDefaultClipPolygon(400, 400);
        s.setClipPolygon(clip);
        String msg = new VertexEditor(s).deleteNearestVertex(clip.getVertex(0));
        assertEquals(Scene.MIN_CLIP_VERTICES, clip.size());
        assertFalse(msg.isEmpty());
    }

    @Test
    void clipPolygonVertexDeletedWhenAboveMinimum() {
        Scene s = new Scene();
        Polygon clip = Scene.createDefaultClipPolygon(400, 400);
        clip.addVertex(new Point(5, 5));
        s.setClipPolygon(clip);
        String msg = new VertexEditor(s).deleteNearestVertex(new Point(5, 5));
        assertEquals(5, clip.size());
        assertTrue(msg.isEmpty());
    }

    @Test
    void deleteNearestPolygon() {
        Scene s = new Scene();
        s.getPolygons().add(square());
        VertexEditor e = new VertexEditor(s);
        assertFalse(e.deleteNearestPolygon(new Point(300, 300)));
        assertTrue(e.deleteNearestPolygon(new Point(50, 2)));
        assertTrue(s.getPolygons().isEmpty());
    }
}
