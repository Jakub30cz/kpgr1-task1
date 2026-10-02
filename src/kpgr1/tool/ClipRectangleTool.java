package kpgr1.tool;

import java.util.ArrayList;
import java.util.List;
import kpgr1.model.ClipRectangle;
import kpgr1.model.Line;
import kpgr1.model.Point;
import kpgr1.model.Scene;

/** Clip-rectangle mode: press-drag-release defines the clipping window. */
public final class ClipRectangleTool implements Tool {
    private final Scene scene;
    private Point start;
    private Point end;

    public ClipRectangleTool(Scene scene) { this.scene = scene; }

    @Override
    public void pressed(Point p, boolean shift) {
        start = p;
        end = p;
    }

    @Override
    public void dragged(Point p, boolean shift) {
        if (start != null) end = p;
    }

    @Override
    public void released(Point p, boolean shift) {
        if (start == null) return;
        scene.setClipRectangle(new ClipRectangle(start, p));
        reset();
    }

    @Override
    public List<Line> preview() {
        if (start == null) return List.of();
        List<Point> c = new ClipRectangle(start, end).corners();
        List<Line> lines = new ArrayList<>();
        for (int i = 0; i < c.size(); i++) lines.add(new Line(c.get(i), c.get((i + 1) % c.size())));
        return lines;
    }

    @Override
    public void reset() {
        start = null;
        end = null;
    }

    @Override
    public String help() {
        return "Ořezávací obdélník (R): táhni myší; úsečky se ořežou Cohen-Sutherlandem.";
    }
}
