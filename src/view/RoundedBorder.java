package view;

import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;

/**
 * Rounded Border Utility - Creates modern rounded corners
 */
public class RoundedBorder extends LineBorder {

    private final int radius;
    private final Color fillColor;

    public RoundedBorder(int radius, Color color) {
        super(color, 1, true);
        this.radius = radius;
        this.fillColor = null;
    }

    public RoundedBorder(int radius, Color color, Color fillColor) {
        super(color, 1, true);
        this.radius = radius;
        this.fillColor = fillColor;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);

        Shape shape = new RoundRectangle2D.Double(x, y, width - 1, height - 1, radius, radius);

        if (fillColor != null) {
            g2.setColor(fillColor);
            g2.fill(shape);
        }

        if (lineColor != null) {
            g2.setColor(lineColor);
            g2.draw(shape);
        }

        g2.dispose();
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(radius / 2 + 2, radius / 2 + 2, radius / 2 + 2, radius / 2 + 2);
    }

    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        insets.set(radius / 2 + 2, radius / 2 + 2, radius / 2 + 2, radius / 2 + 2);
        return insets;
    }

    /**
     * Create a rounded border with given radius and color
     */
    public static Border create(int radius, Color color) {
        return new RoundedBorder(radius, color);
    }

    /**
     * Create a rounded border with fill color
     */
    public static Border create(int radius, Color color, Color fillColor) {
        return new RoundedBorder(radius, color, fillColor);
    }

    /**
     * Create rounded border with padding
     */
    public static Border createWithPadding(int radius, Color color, int padding) {
        return new CompoundBorder(
            new RoundedBorder(radius, color),
            new EmptyBorder(padding, padding, padding, padding)
        );
    }
}
