package kpgr1.tool;

import java.util.List;
import kpgr1.model.Line;
import kpgr1.model.Point;

/** One interaction mode. Points are canvas pixel coordinates. */
public interface Tool {
    void pressed(Point p, boolean shift);

    void dragged(Point p, boolean shift);

    void released(Point p, boolean shift);

    /** Enter key. */
    default void finish() {}

    /** Rubber-band lines, empty when idle. */
    List<Line> preview();

    /** Key C: drops any in-progress state. */
    void reset();

    /** Czech status-bar text. */
    String help();
}
