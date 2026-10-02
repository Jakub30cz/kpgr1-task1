package kpgr1.tool;

import java.util.ArrayList;
import java.util.List;
import kpgr1.model.Line;
import kpgr1.model.Point;
import kpgr1.model.Polygon;
import kpgr1.model.Scene;

/**
 * Polygon mode: a press starts a new polygon (if none is open) and previews edges to its first and last
 * vertex; release appends the vertex. Enter closes the polygon so the next click starts a new one.
 */
public class PolygonTool implements Tool {
    private final Scene scene;
    private Polygon current;
    private Point cursor;

    public PolygonTool(Scene scene) { this.scene = scene; }

    /** Polygon being edited; subclasses may supply a fixed one. Null means "start a new polygon". */
    Polygon target() { return current; }

    /** Starts a new polygon in the scene. */
    Polygon open() {
        current = new Polygon();
        scene.getPolygons().add(current);
        return current;
    }

    @Override
    public void pressed(Point p, boolean shift) {
        if (target() == null) open();
        cursor = p;
    }

    @Override
    public void dragged(Point p, boolean shift) {
        if (cursor != null) cursor = p;
    }

    @Override
    public void released(Point p, boolean shift) {
        if (cursor == null) return;
        Polygon polygon = target();
        if (polygon != null) polygon.addVertex(p);
        cursor = null;
    }

    @Override
    public void finish() {
        current = null;
        cursor = null;
    }

    @Override
    public List<Line> preview() {
        Polygon polygon = target();
        if (cursor == null || polygon == null || polygon.size() == 0) return List.of();
        List<Line> lines = new ArrayList<>();
        Point first = polygon.getVertex(0);
        Point last = polygon.getVertex(polygon.size() - 1);
        lines.add(new Line(last, cursor));
        if (polygon.size() > 1) lines.add(new Line(cursor, first));
        return lines;
    }

    @Override
    public void reset() { finish(); }

    @Override
    public String help() {
        return "Polygon (P): klikáním přidáváš vrcholy; Enter polygon uzavře a další klik začne nový.";
    }
}
