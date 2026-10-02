package kpgr1.raster;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ArrayRasterTest {
    @Test
    void outOfBoundsSetPixelIsIgnored() {
        ArrayRaster r = new ArrayRaster(3, 2);
        r.setPixel(-1, 0, 0xFFFFFF);
        r.setPixel(0, -1, 0xFFFFFF);
        r.setPixel(3, 0, 0xFFFFFF);
        r.setPixel(0, 2, 0xFFFFFF);
        for (int y = 0; y < 2; y++)
            for (int x = 0; x < 3; x++) assertEquals(0, r.getPixel(x, y));
    }

    @Test
    void containsChecksBounds() {
        ArrayRaster r = new ArrayRaster(3, 2);
        assertTrue(r.contains(2, 1));
        assertFalse(r.contains(3, 1));
        assertFalse(r.contains(2, 2));
        assertFalse(r.contains(-1, 0));
    }

    @Test
    void getPixelMasksAlpha() {
        ArrayRaster r = new ArrayRaster(2, 2);
        r.setPixel(1, 1, 0xFF123456);
        assertEquals(0x123456, r.getPixel(1, 1));
    }

    @Test
    void clearFillsEverything() {
        ArrayRaster r = new ArrayRaster(4, 3);
        r.clear(0xABCDEF);
        for (int y = 0; y < 3; y++)
            for (int x = 0; x < 4; x++) assertEquals(0xABCDEF, r.getPixel(x, y));
        assertEquals(4, r.getWidth());
        assertEquals(3, r.getHeight());
    }

    @Test
    void imageRasterUsesRgbImage() {
        ImageRaster r = new ImageRaster(5, 4);
        r.setPixel(2, 2, 0x00FF00);
        r.setPixel(9, 9, 0x00FF00);
        assertEquals(0x00FF00, r.getPixel(2, 2));
        assertEquals(java.awt.image.BufferedImage.TYPE_INT_RGB, r.getImage().getType());
    }
}
