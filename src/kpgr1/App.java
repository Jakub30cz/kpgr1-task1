package kpgr1;

import javax.swing.SwingUtilities;
import kpgr1.ui.MainFrame;

/** Application entry point. */
public final class App {
    private App() {}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
