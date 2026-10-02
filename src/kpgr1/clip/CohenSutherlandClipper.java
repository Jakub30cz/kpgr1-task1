package kpgr1.clip;

import java.util.Optional;
import kpgr1.model.ClipRectangle;
import kpgr1.model.Line;
import kpgr1.model.Point;

/** Cohen-Sutherland line clipping against a {@link ClipRectangle} (screen coordinates, y grows downwards). */
public final class CohenSutherlandClipper {
    public static final int LEFT = 1;
    public static final int RIGHT = 2;
    public static final int BOTTOM = 4;
    public static final int TOP = 8;

    private CohenSutherlandClipper() {}

    /** Region code of {@code p}: LEFT/RIGHT by x, TOP (y &lt; minY) / BOTTOM (y &gt; maxY) by y. */
    public static int outcode(Point p, ClipRectangle r) {
        int code = 0;
        if (p.x() < r.minX()) code |= LEFT;
        else if (p.x() > r.maxX()) code |= RIGHT;
        if (p.y() < r.minY()) code |= TOP;
        else if (p.y() > r.maxY()) code |= BOTTOM;
        return code;
    }

    /** Clips {@code line} to {@code r}; empty when no part of it lies inside. */
    public static Optional<Line> clip(Line line, ClipRectangle r) {
        double x0 = line.a().x();
        double y0 = line.a().y();
        double x1 = line.b().x();
        double y1 = line.b().y();
        int c0 = outcode(line.a(), r);
        int c1 = outcode(line.b(), r);
        while (true) {
            if ((c0 | c1) == 0) {
                return Optional.of(new Line(
                        new Point((int) Math.round(x0), (int) Math.round(y0)),
                        new Point((int) Math.round(x1), (int) Math.round(y1))));
            }
            if ((c0 & c1) != 0) {
                return Optional.empty();
            }
            int out = c0 != 0 ? c0 : c1;
            double x;
            double y;
            if ((out & TOP) != 0) {
                y = r.minY();
                x = x0 + (x1 - x0) * (y - y0) / (y1 - y0);
            } else if ((out & BOTTOM) != 0) {
                y = r.maxY();
                x = x0 + (x1 - x0) * (y - y0) / (y1 - y0);
            } else if ((out & RIGHT) != 0) {
                x = r.maxX();
                y = y0 + (y1 - y0) * (x - x0) / (x1 - x0);
            } else {
                x = r.minX();
                y = y0 + (y1 - y0) * (x - x0) / (x1 - x0);
            }
            Point p = new Point((int) Math.round(x), (int) Math.round(y));
            if (out == c0) {
                x0 = x;
                y0 = y;
                c0 = outcode(p, r);
            } else {
                x1 = x;
                y1 = y;
                c1 = outcode(p, r);
            }
        }
    }
}
