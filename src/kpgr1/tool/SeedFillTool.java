package kpgr1.tool;

import java.util.List;
import kpgr1.model.Line;
import kpgr1.model.Point;
import kpgr1.model.Scene;
import kpgr1.model.SeedFillCommand;

/** Seed fill mode: a left click records a seed fill with the current settings. */
public final class SeedFillTool implements Tool {
    private final Scene scene;
    private final FillSettings settings;

    public SeedFillTool(Scene scene, FillSettings settings) {
        this.scene = scene;
        this.settings = settings;
    }

    @Override
    public void pressed(Point p, boolean shift) {
        scene.getSeedFills().add(new SeedFillCommand(p, settings.mode, settings.pattern));
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
        return "Vyplnění semínkem: klikněte do oblasti. B = podmínka (hranice / pozadí), T = plná / vzorek.";
    }
}
