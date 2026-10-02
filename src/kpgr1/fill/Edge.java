package kpgr1.fill;

/** Non-horizontal polygon edge oriented top-down (y1 < y2), with x = k*y + q. */
final class Edge {
    final int y1;
    final int y2;
    final double k;
    final double q;

    private Edge(int y1, int y2, double k, double q) {
        this.y1 = y1;
        this.y2 = y2;
        this.k = k;
        this.q = q;
    }

    /** Creates an oriented edge, or returns null for a horizontal one. */
    static Edge of(int xa, int ya, int xb, int yb) {
        if (ya == yb) return null;
        int x1 = xa, y1 = ya, x2 = xb, y2 = yb;
        if (y1 > y2) {
            x1 = xb; y1 = yb;
            x2 = xa; y2 = ya;
        }
        double k = (double) (x2 - x1) / (y2 - y1);
        double q = x1 - k * y1;
        return new Edge(y1, y2, k, q);
    }

    /** The edge is shortened by one pixel: the lower end is excluded. */
    boolean covers(int y) { return y >= y1 && y < y2; }

    int xAt(int y) { return (int) Math.round(k * y + q); }
}
