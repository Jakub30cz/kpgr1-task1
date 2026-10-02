package kpgr1.rasterize;

import kpgr1.model.Line;
import kpgr1.raster.Raster;

/** Base class of line rasterization algorithms. */
public abstract class LineRasterizer {
    protected final Raster raster;
    protected int color;

    protected LineRasterizer(Raster raster, int color) {
        this.raster = raster;
        this.color = color;
    }

    public void setColor(int color) { this.color = color; }

    /** Rasterizes the line with the current color. */
    public void rasterize(Line line) {
        rasterize(line.a().x(), line.a().y(), line.b().x(), line.b().y(), color);
    }

    public abstract void rasterize(int x1, int y1, int x2, int y2, int color);
}
