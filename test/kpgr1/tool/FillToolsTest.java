package kpgr1.tool;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import kpgr1.fill.SeedFillMode;
import kpgr1.model.Point;
import kpgr1.model.Polygon;
import kpgr1.model.Scene;
import kpgr1.model.SeedFillCommand;
import org.junit.jupiter.api.Test;

class FillToolsTest {
    private static Polygon square(int x, int y, int size) {
        return new Polygon(List.of(new Point(x, y), new Point(x + size, y),
                new Point(x + size, y + size), new Point(x, y + size)));
    }

    @Test
    void settingsToggle() {
        FillSettings s = new FillSettings();
        SeedFillMode first = s.mode;
        s.toggleMode();
        assertNotEquals(first, s.mode);
        s.toggleMode();
        assertEquals(first, s.mode);
        assertFalse(s.pattern);
        s.togglePattern();
        assertTrue(s.pattern);
    }

    @Test
    void seedToolRecordsModeAndPattern() {
        Scene scene = new Scene();
        FillSettings s = new FillSettings();
        SeedFillTool tool = new SeedFillTool(scene, s);
        tool.pressed(new Point(5, 6), false);
        s.toggleMode();
        s.togglePattern();
        tool.pressed(new Point(7, 8), false);
        List<SeedFillCommand> c = scene.getSeedFills();
        assertEquals(2, c.size());
        assertEquals(new SeedFillCommand(new Point(5, 6), SeedFillMode.STOP_ON_BOUNDARY, false), c.get(0));
        assertEquals(new SeedFillCommand(new Point(7, 8), s.mode, true), c.get(1));
        assertNotEquals(c.get(0).mode(), c.get(1).mode());
        assertTrue(tool.preview().isEmpty());
    }

    @Test
    void scanLineToolMarksOnlyContainingPolygon() {
        Scene scene = new Scene();
        Polygon a = square(0, 0, 10);
        Polygon b = square(50, 50, 10);
        scene.getPolygons().add(a);
        scene.getPolygons().add(b);
        FillSettings s = new FillSettings();
        s.togglePattern();
        ScanLineTool tool = new ScanLineTool(scene, s);
        tool.pressed(new Point(55, 55), false);
        assertEquals(1, scene.getScanLineFills().size());
        assertEquals(Boolean.TRUE, scene.getScanLineFills().get(b));
        assertFalse(scene.getScanLineFills().containsKey(a));
        tool.pressed(new Point(30, 30), false);
        assertEquals(1, scene.getScanLineFills().size());
    }
}
