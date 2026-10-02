package kpgr1.tool;

import java.util.List;
import kpgr1.model.Line;
import kpgr1.model.Point;
import kpgr1.model.Scene;
import kpgr1.rasterize.LineSnap;

/** Line mode: press = start, drag = rubber band, release = commit (Shift snaps to 8 directions). */
public final class LineTool implements Tool {
    private final Scene scene;
    private Point start;
    private Point end;

    public LineTool(Scene scene) { this.scene = scene; }

    private Point endFor(Point p, boolean shift) { return shift ? LineSnap.snap(start, p) : p; }

    @Override
    public void pressed(Point p, boolean shift) {
        start = p;
        end = p;
    }

    @Override
    public void dragged(Point p, boolean shift) {
        if (start == null) return;
        end = endFor(p, shift);
    }

    @Override
    public void released(Point p, boolean shift) {
        if (start == null) return;
        scene.getLines().add(new Line(start, endFor(p, shift)));
        reset();
    }

    @Override
    public List<Line> preview() {
        return start == null ? List.of() : List.of(new Line(start, end));
    }

    @Override
    public void reset() {
        start = null;
        end = null;
    }

    @Override
    public String help() {
        return "Úsečka (L): táhni myší; Shift = přichytávání na 0/45/90°.";
    }
}
