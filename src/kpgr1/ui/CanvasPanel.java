package kpgr1.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import kpgr1.fill.SeedFillMode;
import kpgr1.model.Point;
import kpgr1.model.Scene;
import kpgr1.raster.ImageRaster;
import kpgr1.render.SceneRenderer;
import kpgr1.tool.ClipPolygonTool;
import kpgr1.tool.ClipRectangleTool;
import kpgr1.tool.FillSettings;
import kpgr1.tool.LineTool;
import kpgr1.tool.PolygonTool;
import kpgr1.tool.ScanLineTool;
import kpgr1.tool.SeedFillTool;
import kpgr1.tool.Tool;
import kpgr1.tool.VertexEditor;

/** Drawing canvas: owns the scene, the tools and the raster; every event re-renders the whole scene. */
public final class CanvasPanel extends JPanel {
    private static final int DEFAULT_WIDTH = 900;
    private static final int DEFAULT_HEIGHT = 650;

    private final StatusBar status;
    private final Scene scene = new Scene();
    private final FillSettings fillSettings = new FillSettings();
    private final VertexEditor editor = new VertexEditor(scene);
    private final Tool lineTool = new LineTool(scene);
    private final Tool polygonTool = new PolygonTool(scene);
    private final Tool seedFillTool = new SeedFillTool(scene, fillSettings);
    private final Tool scanLineTool = new ScanLineTool(scene, fillSettings);
    private final Tool clipRectangleTool = new ClipRectangleTool(scene);
    private ClipPolygonTool clipPolygonTool;

    private ImageRaster raster;
    private SceneRenderer renderer;
    private Tool tool = lineTool;
    private boolean editing;
    private Point lastMouse = new Point(0, 0);

    public CanvasPanel(StatusBar status) {
        this.status = status;
        setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        setFocusable(true);
        recreateRaster(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        installMouse();
        installKeys();
        refresh("");
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        boolean resized = getWidth() != raster.getWidth() || getHeight() != raster.getHeight();
        if (resized && getWidth() > 0 && getHeight() > 0) {
            // The scene is kept; only the pixel buffer is recreated and redrawn.
            recreateRaster(getWidth(), getHeight());
            renderer.render(scene, tool.preview());
        }
        g.drawImage(raster.getImage(), 0, 0, null);
    }

    private void recreateRaster(int w, int h) {
        raster = new ImageRaster(w, h);
        renderer = new SceneRenderer(raster);
    }

    /** Re-renders the scene with the active tool's preview, updates the status bar and repaints. */
    private void refresh(String message) {
        renderer.render(scene, tool.preview());
        status.setHelp(tool.help());
        status.setSettings("Seed fill: "
                + (fillSettings.mode == SeedFillMode.STOP_ON_BOUNDARY ? "zastavit na hranici" : "jen barva pozadí")
                + " | Výplň: " + (fillSettings.pattern ? "vzor" : "plná")
                + " | Orientace ořezávacího polygonu: " + clipOrientation());
        status.setMessage(message);
        repaint();
    }

    private String clipOrientation() {
        return scene.getClipPolygon() == null ? "-" : clipPolygonTool().orientationLabel();
    }

    private ClipPolygonTool clipPolygonTool() {
        if (clipPolygonTool == null) {
            clipPolygonTool = new ClipPolygonTool(scene, raster.getWidth(), raster.getHeight());
        }
        return clipPolygonTool;
    }

    private static Point toPoint(MouseEvent e) { return new Point(e.getX(), e.getY()); }

    private void installMouse() {
        MouseAdapter adapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
                Point p = toPoint(e);
                lastMouse = p;
                String message = "";
                if (SwingUtilities.isRightMouseButton(e)) {
                    editing = editor.grab(p);
                } else if (SwingUtilities.isLeftMouseButton(e)) {
                    if (e.isControlDown()) {
                        if (!editor.insertOnNearestEdge(p)) message = "V okolí není žádná hrana.";
                    } else {
                        tool.pressed(p, e.isShiftDown());
                    }
                }
                refresh(message);
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                Point p = toPoint(e);
                lastMouse = p;
                if (SwingUtilities.isRightMouseButton(e)) {
                    if (editing) editor.drag(p);
                } else if (SwingUtilities.isLeftMouseButton(e) && !e.isControlDown()) {
                    tool.dragged(p, e.isShiftDown());
                }
                refresh("");
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                Point p = toPoint(e);
                lastMouse = p;
                if (SwingUtilities.isRightMouseButton(e)) {
                    editor.release();
                    editing = false;
                } else if (SwingUtilities.isLeftMouseButton(e) && !e.isControlDown()) {
                    tool.released(p, e.isShiftDown());
                }
                refresh("");
            }

            @Override
            public void mouseMoved(MouseEvent e) { lastMouse = toPoint(e); }
        };
        addMouseListener(adapter);
        addMouseMotionListener(adapter);
    }

    private void bind(int keyCode, String name, Runnable action) {
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(keyCode, 0), name);
        getActionMap().put(name, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) { action.run(); }
        });
    }

    private void setTool(Tool next) {
        tool.reset();
        tool = next;
        refresh("");
    }

    private void installKeys() {
        bind(KeyEvent.VK_L, "line", () -> setTool(lineTool));
        bind(KeyEvent.VK_P, "polygon", () -> setTool(polygonTool));
        bind(KeyEvent.VK_F, "seed", () -> setTool(seedFillTool));
        bind(KeyEvent.VK_S, "scanline", () -> setTool(scanLineTool));
        bind(KeyEvent.VK_R, "clipRect", () -> setTool(clipRectangleTool));
        bind(KeyEvent.VK_K, "clipPoly", () -> {
            tool.reset();
            // Created with the real canvas size so the default pentagon fits the window.
            clipPolygonTool = new ClipPolygonTool(scene, raster.getWidth(), raster.getHeight());
            tool = clipPolygonTool;
            refresh("");
        });
        bind(KeyEvent.VK_B, "seedCondition", () -> {
            fillSettings.toggleMode();
            refresh("");
        });
        bind(KeyEvent.VK_T, "fillStyle", () -> {
            fillSettings.togglePattern();
            refresh("");
        });
        bind(KeyEvent.VK_O, "orientation", () -> {
            if (scene.getClipPolygon() == null) {
                refresh("Ořezávací polygon neexistuje (klávesa K).");
                return;
            }
            clipPolygonTool().toggleOrientation();
            refresh("");
        });
        bind(KeyEvent.VK_ENTER, "finish", () -> {
            tool.finish();
            refresh("");
        });
        bind(KeyEvent.VK_DELETE, "deleteVertex", () -> refresh(editor.deleteNearestVertex(lastMouse)));
        bind(KeyEvent.VK_X, "deletePolygon",
                () -> refresh(editor.deleteNearestPolygon(lastMouse) ? "" : "V okolí není žádný polygon."));
        bind(KeyEvent.VK_C, "clear", this::clearAll);
    }

    private void clearAll() {
        scene.clear();
        lineTool.reset();
        polygonTool.reset();
        seedFillTool.reset();
        scanLineTool.reset();
        clipRectangleTool.reset();
        if (clipPolygonTool != null) clipPolygonTool.reset();
        editor.release();
        editing = false;
        refresh("");
    }
}
