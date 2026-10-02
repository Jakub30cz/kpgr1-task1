package kpgr1.model;

/** Nearest polygon edge; a new vertex belongs at {@code insertIndex} (edge i to i+1 gives i+1). */
public record EdgeHit(Polygon polygon, int insertIndex, double distance) {}
