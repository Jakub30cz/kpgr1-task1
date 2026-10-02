package kpgr1.fill;

/** Pattern with a single color. */
public final class SolidPattern implements Pattern {
    private final int color;

    public SolidPattern(int color) { this.color = color; }

    @Override public int colorAt(int x, int y) { return color; }
}
