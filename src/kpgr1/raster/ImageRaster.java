package kpgr1.raster;

import java.awt.image.BufferedImage;

/** Raster backed by a {@link BufferedImage} of type TYPE_INT_RGB, ready to be painted on screen. */
public final class ImageRaster implements Raster {
    private final BufferedImage image;

    public ImageRaster(int w, int h) {
        this.image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
    }

    public BufferedImage getImage() { return image; }

    @Override public int getWidth() { return image.getWidth(); }

    @Override public int getHeight() { return image.getHeight(); }

    @Override public boolean contains(int x, int y) { return x >= 0 && y >= 0 && x < getWidth() && y < getHeight(); }

    @Override public int getPixel(int x, int y) { return image.getRGB(x, y) & 0xFFFFFF; }

    @Override
    public void setPixel(int x, int y, int color) {
        if (contains(x, y)) image.setRGB(x, y, color & 0xFFFFFF);
    }

    @Override
    public void clear(int color) {
        int c = color & 0xFFFFFF;
        for (int y = 0; y < getHeight(); y++)
            for (int x = 0; x < getWidth(); x++) image.setRGB(x, y, c);
    }
}
