package kpgr1.fill;

/** Pattern given by a repeating matrix of colors (rows[y][x]), tiled over the plane. */
public final class MatrixPattern implements Pattern {
    private final int[][] rows;

    public MatrixPattern(int[][] rows) {
        if (rows.length == 0 || rows[0].length == 0) throw new IllegalArgumentException("empty pattern");
        this.rows = rows;
    }

    /** Checkerboard of two colors with square cells of {@code cell} pixels. */
    public static MatrixPattern checker(int c1, int c2, int cell) {
        int n = cell * 2;
        int[][] m = new int[n][n];
        for (int y = 0; y < n; y++)
            for (int x = 0; x < n; x++) m[y][x] = ((x / cell + y / cell) % 2 == 0) ? c1 : c2;
        return new MatrixPattern(m);
    }

    @Override
    public int colorAt(int x, int y) {
        int h = rows.length;
        int w = rows[0].length;
        return rows[Math.floorMod(y, h)][Math.floorMod(x, w)];
    }
}
