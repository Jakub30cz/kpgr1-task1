package kpgr1.tool;

import kpgr1.fill.SeedFillMode;

/** Fill options shared by the seed-fill and scan-line tools. */
public final class FillSettings {
    public SeedFillMode mode = SeedFillMode.STOP_ON_BOUNDARY;
    /** True = pattern fill, false = solid. */
    public boolean pattern;

    public void toggleMode() {
        mode = mode == SeedFillMode.STOP_ON_BOUNDARY
                ? SeedFillMode.FILL_BACKGROUND_ONLY
                : SeedFillMode.STOP_ON_BOUNDARY;
    }

    public void togglePattern() {
        pattern = !pattern;
    }
}
