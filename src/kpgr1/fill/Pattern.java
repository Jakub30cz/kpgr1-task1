package kpgr1.fill;

/** Fill color source; implementations use pattern(x % w, y % h). */
public interface Pattern {
    int colorAt(int x, int y);
}
