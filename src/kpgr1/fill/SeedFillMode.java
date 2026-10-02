package kpgr1.fill;

/** Seed fill stop condition. */
public enum SeedFillMode {
    /** Fill until a pixel of the boundary color is reached. */
    STOP_ON_BOUNDARY,
    /** Fill only pixels of the background color. */
    FILL_BACKGROUND_ONLY
}
