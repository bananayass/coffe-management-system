package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Theme Toggle Button - Switch between Light and Dark mode
 * Styled as a toggle switch
 */
public class ThemeToggle extends JPanel {

    private static boolean isDarkMode = false;
    private static MainFrame mainFrame;
    private JButton btnToggle;

    public ThemeToggle() {
        this(null);
    }

    public ThemeToggle(MainFrame mainFrame) {
        ThemeToggle.mainFrame = mainFrame;
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        setBackground(UITheme.BG_DARK);
        setOpaque(false);

        btnToggle = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Background track
                g2.setColor(isDarkMode ? UITheme.PRIMARY : new Color(200, 200, 200));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                // Thumb
                g2.setColor(Color.WHITE);
                int thumbX = isDarkMode ? getWidth() - 24 : 2;
                g2.fillOval(thumbX, 2, 20, 20);

                g2.dispose();
            }
        };

        btnToggle.setPreferredSize(new Dimension(50, 24));
        btnToggle.setContentAreaFilled(false);
        btnToggle.setBorderPainted(false);
        btnToggle.setFocusPainted(false);
        btnToggle.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnToggle.setToolTipText(isDarkMode ? "Switch to Light Mode" : "Switch to Dark Mode");

        btnToggle.addActionListener(e -> toggleTheme());

        add(btnToggle);

        // Add label
        JLabel lbl = new JLabel(isDarkMode ? "Dark" : "Light");
        lbl.setFont(UITheme.FONT_SMALL);
        lbl.setForeground(new Color(148, 163, 184));
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
        add(lbl);
    }

    private void toggleTheme() {
        isDarkMode = !isDarkMode;

        if (isDarkMode) {
            applyDarkMode();
        } else {
            applyLightMode();
        }

        btnToggle.setToolTipText(isDarkMode ? "Switch to Light Mode" : "Switch to Dark Mode");

        // Update label
        for (Component c : getComponents()) {
            if (c instanceof JLabel) {
                ((JLabel) c).setText(isDarkMode ? "Dark" : "Light");
            }
        }

        btnToggle.repaint();

        // Refresh main frame by navigating to current page
        if (mainFrame != null) {
            mainFrame.refreshCurrentView();
        } else {
            // Fallback: refresh all windows
            for (Window w : Window.getWindows()) {
                w.repaint();
                w.revalidate();
            }
        }
    }

    public static MainFrame getMainFrame() {
        return mainFrame;
    }

    public static void setMainFrame(MainFrame frame) {
        mainFrame = frame;
    }

    public static void applyDarkMode() {
        UITheme.BG_COLOR = new Color(15, 23, 42);
        UITheme.BG_LIGHT = new Color(30, 41, 59);
        UITheme.BG_MEDIUM = new Color(51, 65, 85);
        UITheme.BG_CARD = new Color(30, 41, 59);
        UITheme.TEXT_DARK = Color.WHITE;
        UITheme.TEXT_MEDIUM = new Color(148, 163, 184);
        UITheme.TEXT_LIGHT = new Color(100, 116, 139);
        UITheme.BORDER = new Color(51, 65, 85);
    }

    public static void applyLightMode() {
        UITheme.BG_COLOR = new Color(248, 250, 252);
        UITheme.BG_LIGHT = new Color(255, 255, 255);
        UITheme.BG_MEDIUM = new Color(241, 245, 249);
        UITheme.BG_CARD = Color.WHITE;
        UITheme.TEXT_DARK = new Color(15, 23, 42);
        UITheme.TEXT_MEDIUM = new Color(71, 85, 105);
        UITheme.TEXT_LIGHT = new Color(100, 116, 139);
        UITheme.BORDER = new Color(226, 232, 240);
    }

    public static boolean isDarkMode() {
        return isDarkMode;
    }
}
