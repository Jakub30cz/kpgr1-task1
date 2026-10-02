package kpgr1.raster;

/** Pixel buffer. Colors are 0xRRGGBB (alpha ignored, getPixel masks with 0xFFFFFF). */
public interface Raster {
    int getWidth();

    int getHeight();

    /** Returns true when (x, y) lies inside the buffer. */
    boolean contains(int x, int y);

    /** Precondition: {@code contains(x, y)}. */
    int getPixel(int x, int y);

    /** Silently ignores out-of-bounds coordinates. */
    void setPixel(int x, int y, int color);

    void clear(int color);
}
