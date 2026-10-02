package kpgr1.render;

import java.util.List;
import kpgr1.clip.CohenSutherlandClipper;
import kpgr1.clip.SutherlandHodgmanClipper;
import kpgr1.fill.MatrixPattern;
import kpgr1.fill.Pattern;
import kpgr1.fill.ScanLineFiller;
import kpgr1.fill.SeedFiller;
import kpgr1.fill.SolidPattern;
import kpgr1.model.ClipRectangle;
import kpgr1.model.Line;
import kpgr1.model.Point;
import kpgr1.model.Polygon;
import kpgr1.model.Scene;
import kpgr1.model.SeedFillCommand;
import kpgr1.raster.Raster;
import kpgr1.rasterize.BresenhamLineRasterizer;
import kpgr1.rasterize.LineRasterizer;

/** Redraws the whole retained {@link Scene} into a {@link Raster}; clip results are derived on every call. */
public final class SceneRenderer {
    private static final int PATTERN_CELL = 4;

    private final Raster raster;
    private final LineRasterizer lines;

    public SceneRenderer(Raster raster) {
        this.raster = raster;
        this.lines = new BresenhamLineRasterizer(raster, Palette.BOUNDARY);
    }

    public void render(Scene scene, List<Line> preview) {
        raster.clear(Palette.BACKGROUND);

        // Scan-line fills go first so outlines and clip shapes are drawn over them.
        for (var entry : scene.getScanLineFills().entrySet()) {
            Pattern pattern = patternFor(entry.getValue());
            new ScanLineFiller(raster, entry.getKey(), pattern, lines, Palette.BOUNDARY).fill();
        }

        ClipRectangle rect = scene.getClipRectangle();
        Polygon clipPolygon = scene.getClipPolygon();
        boolean hasClipPolygon = clipPolygon != null && clipPolygon.size() >= 3;

        int lineColor = rect != null ? Palette.ORIGINAL_CLIPPED : Palette.BOUNDARY;
        for (Line line : scene.getLines()) {
            drawLine(line, lineColor);
        }
        int polygonColor = hasClipPolygon ? Palette.ORIGINAL_CLIPPED : Palette.BOUNDARY;
        for (Polygon polygon : scene.getPolygons()) {
            drawPolygon(polygon, polygonColor);
        }

        if (rect != null) {
            drawPolygon(new Polygon(rect.corners()), Palette.CLIP_SHAPE);
        }
        if (clipPolygon != null) {
            drawPolygon(clipPolygon, Palette.CLIP_SHAPE);
        }

        if (rect != null) {
            for (Line line : scene.getLines()) {
                CohenSutherlandClipper.clip(line, rect).ifPresent(l -> drawLine(l, Palette.CLIP_RESULT));
            }
        }
        if (hasClipPolygon) {
            for (Polygon polygon : scene.getPolygons()) {
                Polygon clipped = SutherlandHodgmanClipper.clip(polygon, clipPolygon);
                if (clipped.size() >= 2) {
                    drawPolygon(clipped, Palette.CLIP_RESULT);
                }
            }
        }

        // Seed fills are replayed last: they flood whatever is on the raster and stop at the
        // boundary color, so every outline must already be in place when they run.
        for (SeedFillCommand cmd : scene.getSeedFills()) {
            new SeedFiller(raster, cmd.seed(), patternFor(cmd.pattern()), cmd.mode(),
                    Palette.BOUNDARY, Palette.BACKGROUND).fill();
        }

        for (Line line : preview) {
            drawLine(line, Palette.PREVIEW);
        }
    }

    private static Pattern patternFor(boolean pattern) {
        return pattern
                ? MatrixPattern.checker(Palette.FILL, Palette.PATTERN_ALT, PATTERN_CELL)
                : new SolidPattern(Palette.FILL);
    }

    private void drawLine(Line line, int color) {
        lines.rasterize(line.a().x(), line.a().y(), line.b().x(), line.b().y(), color);
    }

    private void drawPolygon(Polygon polygon, int color) {
        int n = polygon.size();
        if (n == 1) {
            Point p = polygon.getVertex(0);
            raster.setPixel(p.x(), p.y(), color);
        }
        if (n < 2) {
            return;
        }
        for (int i = 0; i < n; i++) {
            Point a = polygon.getVertex(i);
            Point b = polygon.getVertex((i + 1) % n);
            lines.rasterize(a.x(), a.y(), b.x(), b.y(), color);
        }
    }
}
