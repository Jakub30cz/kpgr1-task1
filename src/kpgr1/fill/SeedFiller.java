package kpgr1.fill;

import java.util.ArrayDeque;
import kpgr1.model.Point;
import kpgr1.raster.Raster;

/**
 * 4-connected seed fill using an explicit queue (no recursion, so large areas cannot overflow the stack).
 *
 * <p>A pixel is fillable depending on the mode:
 * <ul>
 *   <li>{@link SeedFillMode#STOP_ON_BOUNDARY}: any pixel whose color differs from the boundary color;</li>
 *   <li>{@link SeedFillMode#FILL_BACKGROUND_ONLY}: only pixels equal to the background color.</li>
 * </ul>
 * Fillability is decided on the original raster content and each pixel is visited at most once (tracked in a
 * visited map), so a pattern that contains the boundary or background color neither loops nor re-visits pixels.
 *
 * <p>No-op conditions: the seed lies outside the raster, or the seed pixel is not fillable (it has the boundary
 * color, or in background mode is not the background color).
 */
public final class SeedFiller implements Filler {
    private final Raster raster;
    private final Point seed;
    private final Pattern pattern;
    private final SeedFillMode mode;
    private final int boundaryColor;
    private final int backgroundColor;

    public SeedFiller(Raster raster, Point seed, Pattern pattern, SeedFillMode mode,
                      int boundaryColor, int backgroundColor) {
        this.raster = raster;
        this.seed = seed;
        this.pattern = pattern;
        this.mode = mode;
        this.boundaryColor = boundaryColor & 0xFFFFFF;
        this.backgroundColor = backgroundColor & 0xFFFFFF;
    }

    private boolean fillable(int x, int y) {
        int c = raster.getPixel(x, y);
        return mode == SeedFillMode.STOP_ON_BOUNDARY ? c != boundaryColor : c == backgroundColor;
    }

    @Override
    public void fill() {
        if (!raster.contains(seed.x(), seed.y()) || !fillable(seed.x(), seed.y())) return;
        int w = raster.getWidth();
        boolean[] visited = new boolean[w * raster.getHeight()];
        ArrayDeque<int[]> queue = new ArrayDeque<>();
        visited[seed.y() * w + seed.x()] = true;
        queue.add(new int[] {seed.x(), seed.y()});
        int[] dx = {1, -1, 0, 0};
        int[] dy = {0, 0, 1, -1};
        while (!queue.isEmpty()) {
            int[] p = queue.poll();
            raster.setPixel(p[0], p[1], pattern.colorAt(p[0], p[1]));
            for (int i = 0; i < 4; i++) {
                int nx = p[0] + dx[i];
                int ny = p[1] + dy[i];
                if (!raster.contains(nx, ny) || visited[ny * w + nx]) continue;
                // Visited neighbours were already decided; unvisited ones still hold their original color.
                if (!fillable(nx, ny)) continue;
                visited[ny * w + nx] = true;
                queue.add(new int[] {nx, ny});
            }
        }
    }
}
