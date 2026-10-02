package kpgr1.tool;

import static org.junit.jupiter.api.Assertions.*;

import kpgr1.model.Line;
import kpgr1.model.Point;
import kpgr1.model.Polygon;
import kpgr1.model.Scene;
import org.junit.jupiter.api.Test;

class GeometryToolsTest {
    private static Point pt(int x, int y) { return new Point(x, y); }

    private static void click(Tool t, Point p) {
        t.pressed(p, false);
        t.released(p, false);
    }

    @Test
    void lineDragSequenceAddsExactlyOneLine() {
        Scene scene = new Scene();
        LineTool tool = new LineTool(scene);
        tool.pressed(pt(10, 10), false);
        tool.dragged(pt(20, 15), false);
        assertEquals(1, tool.preview().size());
        assertTrue(scene.getLines().isEmpty());
        tool.dragged(pt(50, 40), false);
        tool.released(pt(50, 40), false);
        assertEquals(1, scene.getLines().size());
        assertEquals(new Line(pt(10, 10), pt(50, 40)), scene.getLines().get(0));
        assertTrue(tool.preview().isEmpty());
    }

    @Test
    void lineShiftSnapsEndPoint() {
        Scene scene = new Scene();
        LineTool tool = new LineTool(scene);
        tool.pressed(pt(100, 100), true);
        tool.dragged(pt(150, 105), true);
        assertEquals(new Line(pt(100, 100), pt(150, 100)), tool.preview().get(0));
        tool.released(pt(150, 105), true);
        assertEquals(new Line(pt(100, 100), pt(150, 100)), scene.getLines().get(0));
    }

    @Test
    void lineReleaseWithoutPressAddsNothing() {
        Scene scene = new Scene();
        new LineTool(scene).released(pt(1, 1), false);
        assertTrue(scene.getLines().isEmpty());
    }

    @Test
    void polygonOfThreeClicksHasThreeVerticesAndTwoPreviewLines() {
        Scene scene = new Scene();
        PolygonTool tool = new PolygonTool(scene);
        click(tool, pt(0, 0));
        click(tool, pt(100, 0));
        tool.pressed(pt(50, 80), false);
        tool.dragged(pt(50, 90), false);
        assertEquals(2, tool.preview().size());
        tool.released(pt(50, 90), false);
        assertEquals(1, scene.getPolygons().size());
        assertEquals(3, scene.getPolygons().get(0).size());
        assertTrue(tool.preview().isEmpty());
    }

    @Test
    void polygonFinishStartsNewPolygon() {
        Scene scene = new Scene();
        PolygonTool tool = new PolygonTool(scene);
        click(tool, pt(0, 0));
        click(tool, pt(10, 0));
        click(tool, pt(5, 9));
        tool.finish();
        click(tool, pt(50, 50));
        assertEquals(2, scene.getPolygons().size());
        assertEquals(3, scene.getPolygons().get(0).size());
        assertEquals(1, scene.getPolygons().get(1).size());
    }

    @Test
    void polygonResetDropsCurrentPolygon() {
        Scene scene = new Scene();
        PolygonTool tool = new PolygonTool(scene);
        click(tool, pt(0, 0));
        click(tool, pt(10, 0));
        click(tool, pt(5, 8));
        tool.reset();
        click(tool, pt(5, 5));
        assertEquals(2, scene.getPolygons().size());
    }

    @Test
    void clipRectangleSetOnRelease() {
        Scene scene = new Scene();
        ClipRectangleTool tool = new ClipRectangleTool(scene);
        tool.pressed(pt(10, 20), false);
        tool.dragged(pt(60, 70), false);
        assertEquals(4, tool.preview().size());
        assertNull(scene.getClipRectangle());
        tool.released(pt(60, 70), false);
        assertNotNull(scene.getClipRectangle());
        assertEquals(10, scene.getClipRectangle().minX());
        assertEquals(70, scene.getClipRectangle().maxY());
        assertTrue(tool.preview().isEmpty());
    }

    @Test
    void clipPolygonCreatesDefaultPentagon() {
        Scene scene = new Scene();
        new ClipPolygonTool(scene).activate();
        assertNotNull(scene.getClipPolygon());
        assertEquals(Scene.MIN_CLIP_VERTICES, scene.getClipPolygon().size());
    }

    @Test
    void clipPolygonKeepsExistingAndAppendsVertex() {
        Scene scene = new Scene();
        Polygon existing = new Polygon();
        scene.setClipPolygon(existing);
        ClipPolygonTool tool = new ClipPolygonTool(scene);
        assertSame(existing, scene.getClipPolygon());
        click(tool, pt(3, 4));
        assertEquals(1, existing.size());
    }

    @Test
    void toggleOrientationFlipsLabelAndReversesPolygon() {
        Scene scene = new Scene();
        ClipPolygonTool tool = new ClipPolygonTool(scene);
        tool.activate();
        String before = tool.orientationLabel();
        Point first = scene.getClipPolygon().getVertex(0);
        tool.toggleOrientation();
        assertNotEquals(before, tool.orientationLabel());
        assertEquals(first, scene.getClipPolygon().getVertex(Scene.MIN_CLIP_VERTICES - 1));
        tool.toggleOrientation();
        assertEquals(before, tool.orientationLabel());
        assertTrue(before.equals("CW") || before.equals("CCW"));
    }

    @Test
    void helpTextsAreNotEmpty() {
        Scene s = new Scene();
        assertFalse(new LineTool(s).help().isBlank());
        assertFalse(new PolygonTool(s).help().isBlank());
        assertFalse(new ClipRectangleTool(s).help().isBlank());
        assertFalse(new ClipPolygonTool(s).help().isBlank());
    }

    @Test
    void polygonToolStartsNewPolygonAfterCurrentWasRemoved() {
        Scene scene = new Scene();
        PolygonTool tool = new PolygonTool(scene);
        click(tool, pt(10, 10));
        click(tool, pt(50, 10));
        click(tool, pt(30, 40));
        scene.removePolygon(scene.getPolygons().get(0));
        click(tool, pt(100, 100));
        assertEquals(1, scene.getPolygons().size());
        assertEquals(1, scene.getPolygons().get(0).size());
    }

    @Test
    void polygonToolIgnoresPolygonDroppedFromScene() {
        Scene scene = new Scene();
        PolygonTool tool = new PolygonTool(scene);
        click(tool, pt(10, 10));
        scene.clear();
        click(tool, pt(20, 20));
        assertEquals(1, scene.getPolygons().size());
        assertEquals(1, scene.getPolygons().get(0).size());
    }

    @Test
    void resetRemovesUnfinishedPolygonWithFewerThanThreeVertices() {
        Scene scene = new Scene();
        PolygonTool tool = new PolygonTool(scene);
        click(tool, pt(10, 10));
        click(tool, pt(50, 10));
        tool.reset();
        assertTrue(scene.getPolygons().isEmpty());
    }

    @Test
    void finishRemovesTwoVertexPolygonButKeepsTriangle() {
        Scene scene = new Scene();
        PolygonTool tool = new PolygonTool(scene);
        click(tool, pt(10, 10));
        click(tool, pt(50, 10));
        tool.finish();
        assertTrue(scene.getPolygons().isEmpty());
        click(tool, pt(10, 10));
        click(tool, pt(50, 10));
        click(tool, pt(30, 40));
        tool.finish();
        assertEquals(1, scene.getPolygons().size());
    }

    @Test
    void clipPolygonToolDoesNotCreatePentagonImplicitly() {
        Scene scene = new Scene();
        ClipPolygonTool tool = new ClipPolygonTool(scene);
        assertNull(scene.getClipPolygon());
        tool.reset();
        assertNull(scene.getClipPolygon());
    }

    @Test
    void clipPolygonToolActivateCreatesPentagonExplicitly() {
        Scene scene = new Scene();
        ClipPolygonTool tool = new ClipPolygonTool(scene);
        tool.activate();
        assertEquals(Scene.MIN_CLIP_VERTICES, scene.getClipPolygon().size());
    }

    @Test
    void clipPolygonToolClickAfterClearCreatesPentagonThenExtends() {
        Scene scene = new Scene();
        ClipPolygonTool tool = new ClipPolygonTool(scene);
        tool.activate();
        scene.clear();
        tool.reset();
        assertNull(scene.getClipPolygon());
        assertTrue(scene.getPolygons().isEmpty());
        click(tool, pt(3, 4));
        assertEquals(Scene.MIN_CLIP_VERTICES + 1, scene.getClipPolygon().size());
    }
}
