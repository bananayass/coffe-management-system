package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Theme Toggle Button - Switch between Light and Dark mode
 */
public class ThemeToggle extends JButton {

    private static boolean isDarkMode = false;

    public ThemeToggle() {
        setText(isDarkMode ? "Light Mode" : "Dark Mode");
        setFont(UITheme.FONT_SMALL);
        setForeground(UITheme.TEXT_DARK);
        setBackground(UITheme.BG_MEDIUM);
        setFocusPainted(false);
        setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        addActionListener(e -> toggleTheme());
    }

    private void toggleTheme() {
        isDarkMode = !isDarkMode;

        // Update UITheme colors
        if (isDarkMode) {
            applyDarkMode();
        } else {
            applyLightMode();
        }

        setText(isDarkMode ? "Light Mode" : "Dark Mode");

        // Notify all windows to refresh
        for (Window w : Window.getWindows()) {
            w.repaint();
            w.revalidate();
        }
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
