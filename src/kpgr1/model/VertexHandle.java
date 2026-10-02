package kpgr1.model;

/** Reference to one editable vertex of the scene (line endpoint, polygon vertex, clip corner...). */
public interface VertexHandle {
    Point get();

    void set(Point p);
}
