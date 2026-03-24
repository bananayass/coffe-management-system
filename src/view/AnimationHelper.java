package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Animation Utilities for Swing
 */
public class AnimationHelper {

    /**
     * Add simple hover effect (color change only)
     */
    public static void addHoverEffect(JButton button) {
        Color normalColor = button.getBackground();
        Color hoverColor = normalColor.brighter();

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(normalColor);
            }
        });
    }

    /**
     * Add click press effect
     */
    public static void addClickEffect(JButton button) {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                button.setBackground(button.getBackground().darker());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                button.setBackground(button.getBackground().brighter());
            }
        });
    }

    /**
     * Show loading spinner overlay
     */
    public static JPanel createLoadingOverlay() {
        JPanel overlay = new JPanel(new GridBagLayout());
        overlay.setBackground(new Color(0, 0, 0, 100));

        JSpinner spinner = new JSpinner();
        spinner.setPreferredSize(new Dimension(40, 40));
        spinner.setFont(UITheme.FONT_BODY);

        JLabel label = new JLabel("Loading...");
        label.setForeground(Color.WHITE);
        label.setFont(UITheme.FONT_BODY);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        overlay.add(spinner, gbc);
        gbc.gridy = 1;
        overlay.add(label, gbc);

        return overlay;
    }

    /**
     * Create toast notification
     */
    public static JPanel createToast(String message, Color bgColor) {
        JPanel toast = new JPanel(new BorderLayout());
        toast.setBackground(bgColor);
        toast.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

        JLabel label = new JLabel(message);
        label.setForeground(Color.WHITE);
        label.setFont(UITheme.FONT_BODY);
        label.setBorder(new EmptyBorder(8, 16, 8, 16));

        toast.add(label, BorderLayout.CENTER);

        return toast;
    }
}
