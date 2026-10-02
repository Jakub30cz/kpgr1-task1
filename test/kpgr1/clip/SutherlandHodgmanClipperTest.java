package kpgr1.clip;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import kpgr1.model.Point;
import kpgr1.model.Polygon;
import org.junit.jupiter.api.Test;

class SutherlandHodgmanClipperTest {
    private static Polygon poly(int... xy) {
        Polygon p = new Polygon();
        for (int i = 0; i < xy.length; i += 2) p.addVertex(new Point(xy[i], xy[i + 1]));
        return p;
    }

    private static Polygon pentagon() {
        return poly(50, 0, 100, 40, 80, 100, 20, 100, 0, 40); // clockwise on screen
    }

    private static double absArea(Polygon p) { return Math.abs(PolygonGeometry.signedArea(p)); }

    @Test
    void cwAndCcwClipPolygonGiveSameResult() {
        Polygon subject = poly(10, 10, 90, 10, 90, 90, 10, 90);
        Polygon cw = pentagon();
        Polygon ccw = pentagon();
        ccw.reverse();
        Polygon r1 = SutherlandHodgmanClipper.clip(subject, cw);
        Polygon r2 = SutherlandHodgmanClipper.clip(subject, ccw);
        assertTrue(r1.size() >= 3);
        assertEquals(new HashSet<>(r1.getVertices()), new HashSet<>(r2.getVertices()));
        assertEquals(absArea(r1), absArea(r2), 1e-9);
    }

    @Test
    void subjectFullyInsideIsUnchanged() {
        Polygon subject = poly(40, 40, 60, 40, 60, 60, 40, 60);
        Polygon r = SutherlandHodgmanClipper.clip(subject, pentagon());
        assertEquals(subject.getVertices(), r.getVertices());
    }

    @Test
    void subjectFullyOutsideGivesEmpty() {
        Polygon subject = poly(200, 200, 300, 200, 300, 300, 200, 300);
        assertEquals(0, SutherlandHodgmanClipper.clip(subject, pentagon()).size());
    }

    @Test
    void concaveSubjectAreaIsCorrect() {
        Polygon lShape = poly(0, 0, 40, 0, 40, 20, 20, 20, 20, 40, 0, 40); // area 1200
        Polygon clip = poly(-10, -10, 100, -10, 100, 100, -10, 100);
        Polygon r = SutherlandHodgmanClipper.clip(lShape, clip);
        assertEquals(1200.0, absArea(r), 1e-9);
        // clip window cutting the L at x<=20: remaining area 20*40 = 800
        Polygon half = poly(-10, -10, 20, -10, 20, 100, -10, 100);
        assertEquals(800.0, absArea(SutherlandHodgmanClipper.clip(lShape, half)), 1e-9);
    }

    @Test
    void bowtieVerticesLieInsideClipPolygon() {
        Polygon bowtie = poly(0, 0, 100, 100, 100, 0, 0, 100);
        Polygon clip = poly(20, 20, 80, 20, 80, 80, 20, 80);
        Polygon r = SutherlandHodgmanClipper.clip(bowtie, clip);
        assertTrue(r.size() > 0);
        for (Point p : r.getVertices()) {
            assertTrue(p.x() >= 20 && p.x() <= 80 && p.y() >= 20 && p.y() <= 80, p.toString());
        }
    }
}
