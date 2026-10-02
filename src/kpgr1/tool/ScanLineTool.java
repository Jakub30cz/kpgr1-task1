package kpgr1.tool;

import java.util.List;
import kpgr1.model.Line;
import kpgr1.model.Picking;
import kpgr1.model.Point;
import kpgr1.model.Scene;

/** Scan-line mode: a left click marks the topmost polygon under the cursor for filling. */
public final class ScanLineTool implements Tool {
    private final Scene scene;
    private final FillSettings settings;

    public ScanLineTool(Scene scene, FillSettings settings) {
        this.scene = scene;
        this.settings = settings;
    }

    @Override
    public void pressed(Point p, boolean shift) {
        Picking.topmostContaining(scene, p)
                .ifPresent(poly -> scene.getScanLineFills().put(poly, settings.pattern));
    }

    @Override
    public void dragged(Point p, boolean shift) {}

    @Override
    public void released(Point p, boolean shift) {}

    @Override
    public List<Line> preview() {
        return List.of();
    }

    @Override
    public void reset() {}

    @Override
    public String help() {
        return "Scan-line: klikněte dovnitř polygonu. T = plná / vzorek.";
    }
}
