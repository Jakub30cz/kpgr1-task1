package kpgr1.ui;

import java.awt.BorderLayout;
import javax.swing.JFrame;

/** Main window: canvas in the center, status bar at the bottom. */
public final class MainFrame extends JFrame {
    public MainFrame() {
        super("KPGR1 - Úloha 1: rasterizace, vyplňování, ořezávání");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        StatusBar status = new StatusBar();
        CanvasPanel canvas = new CanvasPanel(status);
        add(canvas, BorderLayout.CENTER);
        add(status, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(null);
        canvas.requestFocusInWindow();
    }
}
