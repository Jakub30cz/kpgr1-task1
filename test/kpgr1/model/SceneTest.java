package kpgr1.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import kpgr1.fill.SeedFillMode;
import org.junit.jupiter.api.Test;

class SceneTest {
    @Test
    void clearEmptiesEverything() {
        Scene s = new Scene();
        Polygon p = new Polygon(List.of(new Point(0, 0), new Point(5, 0), new Point(0, 5)));
        s.getLines().add(new Line(new Point(0, 0), new Point(1, 1)));
        s.getPolygons().add(p);
        s.getScanLineFills().put(p, true);
        s.getSeedFills().add(new SeedFillCommand(new Point(1, 1), SeedFillMode.STOP_ON_BOUNDARY, false));
        s.setClipRectangle(new ClipRectangle(new Point(0, 0), new Point(5, 5)));
        s.setClipPolygon(Scene.createDefaultClipPolygon(100, 100));
        s.clear();
        assertTrue(s.getLines().isEmpty());
        assertTrue(s.getPolygons().isEmpty());
        assertTrue(s.getScanLineFills().isEmpty());
        assertTrue(s.getSeedFills().isEmpty());
        assertNull(s.getClipRectangle());
        assertNull(s.getClipPolygon());
    }

    @Test
    void removePolygonDropsItsFill() {
        Scene s = new Scene();
        Polygon p = new Polygon(List.of(new Point(0, 0), new Point(5, 0), new Point(0, 5)));
        Polygon q = new Polygon(List.of(new Point(0, 0), new Point(5, 0), new Point(0, 5)));
        s.getPolygons().add(p);
        s.getPolygons().add(q);
        s.getScanLineFills().put(p, false);
        s.getScanLineFills().put(q, true);
        s.removePolygon(p);
        assertEquals(1, s.getPolygons().size());
        assertSame(q, s.getPolygons().get(0));
        assertEquals(1, s.getScanLineFills().size());
        assertTrue(s.getScanLineFills().containsKey(q));
    }

    @Test
    void defaultClipPolygonIsCenteredPentagon() {
        Polygon p = Scene.createDefaultClipPolygon(400, 300);
        assertEquals(Scene.MIN_CLIP_VERTICES, p.size());
        double cx = 0, cy = 0;
        for (Point v : p.getVertices()) {
            cx += v.x();
            cy += v.y();
            assertTrue(v.x() >= 0 && v.x() < 400 && v.y() >= 0 && v.y() < 300);
        }
        assertEquals(200, cx / 5, 5);
        assertEquals(150, cy / 5, 15);
    }
}
