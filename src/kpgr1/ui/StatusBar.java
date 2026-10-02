package kpgr1.ui;

import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

/** Bottom bar: help of the active mode, fill/clip settings and a transient message. */
public final class StatusBar extends JPanel {
    private final JLabel help = new JLabel(" ");
    private final JLabel settings = new JLabel(" ");
    private final JLabel message = new JLabel(" ");

    public StatusBar() {
        super(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(3, 6, 3, 6));
        add(help, BorderLayout.NORTH);
        add(settings, BorderLayout.CENTER);
        add(message, BorderLayout.SOUTH);
    }

    public void setHelp(String text) { help.setText(text.isEmpty() ? " " : text); }

    public void setSettings(String text) { settings.setText(text.isEmpty() ? " " : text); }

    public void setMessage(String text) { message.setText(text.isEmpty() ? " " : text); }
}
