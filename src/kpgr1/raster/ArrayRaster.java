package kpgr1.raster;

import java.util.Arrays;

/** Raster backed by a plain int array; used mainly in tests. */
public final class ArrayRaster implements Raster {
    private final int width;
    private final int height;
    private final int[] pixels;

    public ArrayRaster(int w, int h) {
        this.width = w;
        this.height = h;
        this.pixels = new int[w * h];
    }

    @Override public int getWidth() { return width; }

    @Override public int getHeight() { return height; }

    @Override public boolean contains(int x, int y) { return x >= 0 && y >= 0 && x < width && y < height; }

    @Override public int getPixel(int x, int y) { return pixels[y * width + x] & 0xFFFFFF; }

    @Override
    public void setPixel(int x, int y, int color) {
        if (contains(x, y)) pixels[y * width + x] = color & 0xFFFFFF;
    }

    @Override public void clear(int color) { Arrays.fill(pixels, color & 0xFFFFFF); }
}
