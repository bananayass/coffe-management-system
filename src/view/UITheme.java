package view;

import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Font;

/**
 * Modern UI Theme Constants - Light Glassmorphism Style
 * Colors can be changed dynamically for theme switching
 */
public class UITheme {

    // ============= COLORS =============
    // Primary
    public static Color PRIMARY = new Color(99, 102, 241);       // Indigo
    public static Color PRIMARY_LIGHT = new Color(129, 140, 248);
    public static Color PRIMARY_DARK = new Color(67, 56, 202);

    // Secondary
    public static Color SECONDARY = new Color(139, 92, 246);    // Purple

    // Accent
    public static Color ACCENT = new Color(6, 182, 212);      // Cyan

    // Semantic
    public static Color SUCCESS = new Color(16, 185, 129);     // Green
    public static Color WARNING = new Color(245, 158, 11);     // Orange
    public static Color DANGER = new Color(239, 68, 68);        // Red
    public static Color INFO = new Color(59, 130, 246);         // Blue

    // Light Theme (Modern SaaS)
    public static Color BG_COLOR = new Color(248, 250, 252);   // #F8FAFC
    public static Color BG_LIGHT = new Color(255, 255, 255);    // White
    public static Color BG_MEDIUM = new Color(241, 245, 249);   // #F1F5F9
    public static Color BG_CARD = new Color(255, 255, 255);      // Pure white

    // Dark accents (for sidebar)
    public static Color BG_DARK = new Color(15, 23, 42);        // #0F172A
    public static Color BG_MEDIUM_DARK = new Color(30, 41, 59);  // #1E293B

    // Text
    public static Color TEXT_DARK = new Color(15, 23, 42);       // #0F172A
    public static Color TEXT_MEDIUM = new Color(71, 85, 105);    // #475569
    public static Color TEXT_LIGHT = new Color(100, 116, 139);   // #64748B
    public static Color TEXT_WHITE = Color.WHITE;

    // Border
    public static Color BORDER = new Color(226, 232, 240);      // #E2E8F0
    public static Color BORDER_LIGHT = new Color(241, 245, 249);

    // Glass Effect
    public static Color GLASS_BG = new Color(255, 255, 255, 240);
    public static Color GLASS_BORDER = new Color(255, 255, 255, 100);

    // ============= FONTS =============
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 14);

    // ============= BORDERS =============
    public static final Border BORDER_EMPTY = new EmptyBorder(0, 0, 0, 0);
    public static final Border BORDER_PADDING_SMALL = new EmptyBorder(8, 8, 8, 8);
    public static final Border BORDER_PADDING_MEDIUM = new EmptyBorder(16, 16, 16, 16);
    public static final Border BORDER_PADDING_LARGE = new EmptyBorder(24, 24, 24, 24);

    // ============= DIMENSIONS =============
    public static final int SIDEBAR_WIDTH = 260;
    public static final int CARD_RADIUS = 16;
    public static final int BUTTON_RADIUS = 10;

    // ============= ANIMATIONS =============
    public static final int FADE_DURATION = 300;
    public static final int HOVER_SCALE = 105;
    public static final int PRESS_SCALE = 95;

    // ========== SHADOW ==========
    public static Color SHADOW = new Color(0, 0, 0, 30);
}
