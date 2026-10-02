package kpgr1.model;

import kpgr1.fill.SeedFillMode;

/** A recorded seed fill, replayed after all geometry on every redraw. */
public record SeedFillCommand(Point seed, SeedFillMode mode, boolean pattern) {}
