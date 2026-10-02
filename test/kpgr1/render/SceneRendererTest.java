package kpgr1.render;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import kpgr1.fill.SeedFillMode;
import kpgr1.model.ClipRectangle;
import kpgr1.model.Line;
import kpgr1.model.Point;
import kpgr1.model.Polygon;
import kpgr1.model.Scene;
import kpgr1.model.SeedFillCommand;
import kpgr1.raster.ArrayRaster;
import org.junit.jupiter.api.Test;

class SceneRendererTest {
    private final ArrayRaster raster = new ArrayRaster(100, 100);
    private final SceneRenderer renderer = new SceneRenderer(raster);

    @Test
    void lineRendersBoundaryPixels() {
        Scene scene = new Scene();
        scene.getLines().add(new Line(new Point(10, 10), new Point(30, 10)));
        renderer.render(scene, List.of());
        assertEquals(Palette.BOUNDARY, raster.getPixel(10, 10));
        assertEquals(Palette.BOUNDARY, raster.getPixel(20, 10));
        assertEquals(Palette.BOUNDARY, raster.getPixel(30, 10));
        assertEquals(Palette.BACKGROUND, raster.getPixel(20, 11));
    }

    @Test
    void clipRectangleSplitsLineIntoOriginalAndResult() {
        Scene scene = new Scene();
        scene.getLines().add(new Line(new Point(10, 50), new Point(90, 50)));
        scene.setClipRectangle(new ClipRectangle(new Point(40, 30), new Point(60, 70)));
        renderer.render(scene, List.of());
        assertEquals(Palette.ORIGINAL_CLIPPED, raster.getPixel(20, 50));
        assertEquals(Palette.ORIGINAL_CLIPPED, raster.getPixel(80, 50));
        assertEquals(Palette.CLIP_RESULT, raster.getPixel(50, 50));
        assertEquals(Palette.CLIP_SHAPE, raster.getPixel(40, 40));
    }

    @Test
    void seedFillSurvivesSecondRender() {
        Scene scene = new Scene();
        scene.getPolygons().add(new Polygon(List.of(
                new Point(20, 20), new Point(60, 20), new Point(60, 60), new Point(20, 60))));
        scene.getSeedFills().add(new SeedFillCommand(new Point(40, 40), SeedFillMode.STOP_ON_BOUNDARY, false));
        renderer.render(scene, List.of());
        assertEquals(Palette.FILL, raster.getPixel(40, 40));
        assertEquals(Palette.BACKGROUND, raster.getPixel(80, 80));
        renderer.render(scene, List.of());
        assertEquals(Palette.FILL, raster.getPixel(40, 40));
        assertEquals(Palette.BOUNDARY, raster.getPixel(20, 40));
    }

    @Test
    void previewAppearsAndDisappears() {
        Scene scene = new Scene();
        renderer.render(scene, List.of(new Line(new Point(5, 5), new Point(25, 5))));
        assertEquals(Palette.PREVIEW, raster.getPixel(15, 5));
        renderer.render(scene, List.of());
        assertEquals(Palette.BACKGROUND, raster.getPixel(15, 5));
    }
}
