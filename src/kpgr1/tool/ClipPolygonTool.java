package kpgr1.tool;

import kpgr1.clip.PolygonGeometry;
import kpgr1.model.Polygon;
import kpgr1.model.Scene;

/**
 * Clip-polygon mode: edits {@code scene.clipPolygon}. The default pentagon is created only explicitly, by
 * {@link #activate()} (key K) or by the first click while no clip polygon exists; never as a side effect of reset.
 */
public final class ClipPolygonTool extends PolygonTool {
    private static final int DEFAULT_WIDTH = 800;
    private static final int DEFAULT_HEIGHT = 600;

    private final Scene scene;
    private final int width;
    private final int height;

    public ClipPolygonTool(Scene scene) { this(scene, DEFAULT_WIDTH, DEFAULT_HEIGHT); }

    public ClipPolygonTool(Scene scene, int width, int height) {
        super(scene);
        this.scene = scene;
        this.width = width;
        this.height = height;
    }

    /** Creates the default pentagon when the scene has no clip polygon yet. */
    public void activate() {
        if (scene.getClipPolygon() == null) scene.setClipPolygon(Scene.createDefaultClipPolygon(width, height));
    }

    @Override
    Polygon target() { return scene.getClipPolygon(); }

    @Override
    Polygon open() {
        activate();
        return scene.getClipPolygon();
    }

    @Override
    public void finish() {
        // The clip polygon is never closed off; clicks always extend it.
    }

    @Override
    public void reset() {
        // Nothing to drop: the clip polygon lives in the scene, which is cleared by Scene.clear().
    }

    /** Reverses the vertex order of the clip polygon (CW <-> CCW). */
    public void toggleOrientation() {
        if (target() != null) target().reverse();
    }

    public String orientationLabel() {
        Polygon clip = target();
        if (clip == null) return "-";
        return PolygonGeometry.isClockwise(clip) ? "CW" : "CCW";
    }

    @Override
    public String help() {
        return "Ořezávací polygon (K): klik přidá vrchol; O přepne orientaci CW/CCW.";
    }
}
