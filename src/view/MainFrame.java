package view;

import dao.DBConnection;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class MainFrame extends JFrame {
    private JPanel contentPanel;
    private JLabel lblDashboard, lblProducts, lblOrders, lblRevenue;
    private String currentPage = "Dashboard";

    // Clean color palette
    private static final Color PRIMARY = new Color(99, 102, 241);
    private static final Color SUCCESS = new Color(34, 197, 94);
    private static final Color WARNING = new Color(251, 146, 60);
    private static final Color SIDEBAR_DARK = new Color(17, 24, 39);
    private static final Color SIDEBAR_HOVER = new Color(31, 41, 55);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color BG_COLOR = new Color(243, 244, 246);
    private static final Color TEXT_DARK = new Color(17, 24, 39);
    private static final Color TEXT_GRAY = new Color(107, 114, 128);
    private static final Color TEXT_LIGHT = new Color(156, 163, 175);
    private static final Color BORDER = new Color(229, 231, 235);

    public MainFrame() {
        setTitle("Coffee Shop Management");
        setSize(1200, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main container
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(BG_COLOR);

        // Left sidebar
        JPanel sidebar = createSidebar();
        container.add(sidebar, BorderLayout.WEST);

        // Main content area
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BG_COLOR);
        showDashboard();

        container.add(contentPanel, BorderLayout.CENTER);

        setContentPane(container);
        setVisible(true);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(SIDEBAR_DARK);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(20, 0, 20, 0));

        // Logo
        JPanel logoPanel = new JPanel();
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
        logoPanel.setBackground(SIDEBAR_DARK);
        logoPanel.setBorder(new EmptyBorder(0, 24, 30, 24));

        JLabel logo = new JLabel("COFFEE");
        logo.setFont(new Font("Arial", Font.BOLD, 22));
        logo.setForeground(PRIMARY);

        JLabel sub = new JLabel("Shop Manager");
        sub.setFont(new Font("Arial", Font.PLAIN, 11));
        sub.setForeground(TEXT_LIGHT);

        logoPanel.add(logo);
        logoPanel.add(sub);
        logoPanel.add(Box.createVerticalStrut(40));

        // Menu items
        JPanel menu = new JPanel();
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setBackground(SIDEBAR_DARK);

        lblDashboard = createMenuItem("Dashboard", true);
        lblProducts = createMenuItem("Products", false);
        lblOrders = createMenuItem("Orders", false);
        lblRevenue = createMenuItem("Revenue", false);

        lblDashboard.addMouseListener(createNavClick(lblDashboard, "Dashboard"));
        lblProducts.addMouseListener(createNavClick(lblProducts, "Products"));
        lblOrders.addMouseListener(createNavClick(lblOrders, "Orders"));
        lblRevenue.addMouseListener(createNavClick(lblRevenue, "Revenue"));

        menu.add(lblDashboard);
        menu.add(lblProducts);
        menu.add(lblOrders);
        menu.add(lblRevenue);

        // Logout at bottom
        menu.add(Box.createVerticalGlue());

        JLabel lblLogout = createMenuItem("Logout", false);
        lblLogout.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int confirm = JOptionPane.showConfirmDialog(MainFrame.this,
                    "Logout?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });
        menu.add(lblLogout);

        sidebar.add(logoPanel);
        sidebar.add(menu);

        return sidebar;
    }

    private JLabel createMenuItem(String text, boolean active) {
        JLabel item = new JLabel("  " + text);
        item.setFont(new Font("Arial", Font.PLAIN, 14));
        item.setForeground(active ? Color.WHITE : TEXT_LIGHT);
        item.setOpaque(true);
        item.setBackground(active ? PRIMARY : SIDEBAR_DARK);
        item.setMaximumSize(new Dimension(220, 44));
        item.setPreferredSize(new Dimension(220, 44));
        item.setBorder(new EmptyBorder(0, 24, 0, 24));
        item.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return item;
    }

    private MouseAdapter createNavClick(JLabel label, String page) {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                navigateTo(page);
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!label.getBackground().equals(PRIMARY)) {
                    label.setBackground(SIDEBAR_HOVER);
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (!label.getBackground().equals(PRIMARY)) {
                    label.setBackground(SIDEBAR_DARK);
                }
            }
        };
    }

    private void navigateTo(String page) {
        currentPage = page;

        // Reset all menu items
        lblDashboard.setBackground(SIDEBAR_DARK);
        lblDashboard.setForeground(TEXT_LIGHT);
        lblProducts.setBackground(SIDEBAR_DARK);
        lblProducts.setForeground(TEXT_LIGHT);
        lblOrders.setBackground(SIDEBAR_DARK);
        lblOrders.setForeground(TEXT_LIGHT);
        lblRevenue.setBackground(SIDEBAR_DARK);
        lblRevenue.setForeground(TEXT_LIGHT);

        // Highlight active
        switch (page) {
            case "Dashboard":
                lblDashboard.setBackground(PRIMARY);
                lblDashboard.setForeground(Color.WHITE);
                showDashboard();
                break;
            case "Products":
                lblProducts.setBackground(PRIMARY);
                lblProducts.setForeground(Color.WHITE);
                showProducts();
                break;
            case "Orders":
                lblOrders.setBackground(PRIMARY);
                lblOrders.setForeground(Color.WHITE);
                showOrders();
                break;
            case "Revenue":
                lblRevenue.setBackground(PRIMARY);
                lblRevenue.setForeground(Color.WHITE);
                showRevenue();
                break;
        }
    }

    private void showDashboard() {
        contentPanel.removeAll();
        contentPanel.add(new Dashboard(), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showProducts() {
        contentPanel.removeAll();
        contentPanel.add(new ProductPanel(), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showOrders() {
        contentPanel.removeAll();
        contentPanel.add(new OrderPanel(), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showRevenue() {
        contentPanel.removeAll();
        contentPanel.add(new RevenuePanel(), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame());
    }
}
