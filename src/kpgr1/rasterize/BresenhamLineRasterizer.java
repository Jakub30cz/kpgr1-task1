package kpgr1.rasterize;

import kpgr1.raster.Raster;

/**
 * Bresenham's line algorithm, integer-only, valid for all 8 octants.
 *
 * <p>The line is walked along its major axis (the one with the larger |delta|), one pixel per step.
 * With {@code adx = |dx|}, {@code ady = |dy|} and the major axis x, the decision variable
 * {@code err} holds the scaled vertical distance between the ideal line and the current pixel row:
 * after each x step it grows by {@code 2*ady}; once it exceeds {@code adx} the ideal line is closer to
 * the next row, so y advances by one and {@code err} drops by {@code 2*adx}. Starting with
 * {@code err = 2*ady - adx} makes the first decision correspond to the pixel centre, so the plotted pixel is
 * always the nearest one to the ideal line. For a y-major line the roles of x and y are swapped.
 * Endpoints are both drawn, so the line has {@code max(adx, ady) + 1} pixels.
 */
public final class BresenhamLineRasterizer extends LineRasterizer {
    public BresenhamLineRasterizer(Raster raster, int color) {
        super(raster, color);
    }

    @Override
    public void rasterize(int x1, int y1, int x2, int y2, int color) {
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = Integer.compare(x2, x1);
        int sy = Integer.compare(y2, y1);
        int x = x1;
        int y = y1;
        if (dx >= dy) {
            int err = 2 * dy - dx;
            for (int i = 0; i <= dx; i++) {
                raster.setPixel(x, y, color);
                if (err > 0) {
                    y += sy;
                    err -= 2 * dx;
                }
                err += 2 * dy;
                x += sx;
            }
        } else {
            int err = 2 * dx - dy;
            for (int i = 0; i <= dy; i++) {
                raster.setPixel(x, y, color);
                if (err > 0) {
                    x += sx;
                    err -= 2 * dy;
                }
                err += 2 * dx;
                y += sy;
            }
        }
    }
}
