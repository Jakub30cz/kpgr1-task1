package kpgr1.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Polygon stored as an ordered list of vertices; edges are implicit (i, i+1 mod n). */
public final class Polygon {
    private final List<Point> vertices;

    public Polygon() { this.vertices = new ArrayList<>(); }

    public Polygon(List<Point> vertices) { this.vertices = new ArrayList<>(vertices); }

    public int size() { return vertices.size(); }

    public Point getVertex(int i) { return vertices.get(i); }

    /** Returns an unmodifiable copy of the vertices. */
    public List<Point> getVertices() { return List.copyOf(vertices); }

    public void addVertex(Point p) { vertices.add(p); }

    public void insertVertex(int index, Point p) { vertices.add(index, p); }

    public void setVertex(int i, Point p) { vertices.set(i, p); }

    public void removeVertex(int i) { vertices.remove(i); }

    /** Reverses the vertex order (flips CW and CCW). */
    public void reverse() { Collections.reverse(vertices); }
}
