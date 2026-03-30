package view;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class MainFrame extends JFrame {
    private JPanel contentPanel;
    private JLabel lblDashboard, lblOrders, lblProducts, lblCustomers, lblTables, lblStaff, lblReports, lblRevenue;
    private String currentPage = "Dashboard";
    private ThemeToggle themeToggle;

    public MainFrame() {
        setTitle("Coffee Shop Management");
        setSize(1400, 900);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main container
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(UITheme.BG_COLOR);

        // Left sidebar - Dark for contrast
        JPanel sidebar = createSidebar();
        container.add(sidebar, BorderLayout.WEST);

        // Main content area
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(UITheme.BG_COLOR);
        showDashboard();

        container.add(contentPanel, BorderLayout.CENTER);

        setContentPane(container);
        setVisible(true);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(UITheme.BG_DARK);
        sidebar.setPreferredSize(new Dimension(UITheme.SIDEBAR_WIDTH, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(24, 0, 24, 0));

        // Top section with logo and theme toggle
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(UITheme.BG_DARK);
        topPanel.setBorder(new EmptyBorder(0, 24, 20, 24));

        // Logo section
        JPanel logoPanel = new JPanel();
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
        logoPanel.setBackground(UITheme.BG_DARK);

        JLabel logo = new JLabel("Coffee Shop");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        logo.setForeground(UITheme.PRIMARY);

        JLabel sub = new JLabel("Shop Manager");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(new Color(148, 163, 184));

        logoPanel.add(logo);
        logoPanel.add(Box.createVerticalStrut(4));
        logoPanel.add(sub);

        // Theme toggle
        themeToggle = new ThemeToggle();
        themeToggle.setAlignmentX(Component.LEFT_ALIGNMENT);

        topPanel.add(logoPanel);
        topPanel.add(Box.createVerticalStrut(20));
        topPanel.add(themeToggle);

        // Menu items
        JPanel menu = new JPanel();
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setBackground(UITheme.BG_DARK);

        // Main menu
        lblDashboard = createMenuItem("Dashboard", true);
        lblOrders = createMenuItem("Orders", false);
        lblProducts = createMenuItem("Products", false);

        lblDashboard.addMouseListener(createNavClick(lblDashboard, "Dashboard"));
        lblOrders.addMouseListener(createNavClick(lblOrders, "Orders"));
        lblProducts.addMouseListener(createNavClick(lblProducts, "Products"));

        menu.add(lblDashboard);
        menu.add(lblOrders);
        menu.add(lblProducts);

        // Section label
        JLabel lblManagement = createSectionLabel("MANAGEMENT");
        menu.add(Box.createVerticalStrut(16));
        menu.add(lblManagement);

        lblCustomers = createMenuItem("Customers", false);
        lblTables = createMenuItem("Tables", false);
        lblStaff = createMenuItem("Staff", false);

        lblCustomers.addMouseListener(createNavClick(lblCustomers, "Customers"));
        lblTables.addMouseListener(createNavClick(lblTables, "Tables"));
        lblStaff.addMouseListener(createNavClick(lblStaff, "Staff"));

        menu.add(lblCustomers);
        menu.add(lblTables);
        menu.add(lblStaff);

        // Section label
        JLabel lblAnalytics = createSectionLabel("ANALYTICS");
        menu.add(Box.createVerticalStrut(16));
        menu.add(lblAnalytics);

        lblReports = createMenuItem("Reports", false);
        lblRevenue = createMenuItem("Revenue", false);

        lblReports.addMouseListener(createNavClick(lblReports, "Reports"));
        lblRevenue.addMouseListener(createNavClick(lblRevenue, "Revenue"));

        menu.add(lblReports);
        menu.add(lblRevenue);

        // Spacer
        menu.add(Box.createVerticalGlue());

        // Logout
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

        sidebar.add(topPanel);
        sidebar.add(menu);

        return sidebar;
    }

    private JLabel createSectionLabel(String text) {
        JLabel label = new JLabel("  " + text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        label.setForeground(new Color(100, 116, 139));
        label.setMaximumSize(new Dimension(UITheme.SIDEBAR_WIDTH, 24));
        return label;
    }

    private JLabel createMenuItem(String text, boolean active) {
        JLabel item = new JLabel("  " + text);
        item.setFont(UITheme.FONT_BODY);
        item.setForeground(active ? Color.WHITE : new Color(148, 163, 184));
        item.setOpaque(true);
        item.setBackground(active ? UITheme.PRIMARY : UITheme.BG_DARK);
        item.setMaximumSize(new Dimension(UITheme.SIDEBAR_WIDTH, 44));
        item.setPreferredSize(new Dimension(UITheme.SIDEBAR_WIDTH, 44));
        item.setBorder(new EmptyBorder(0, 24, 0, 24));
        item.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        item.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!item.getBackground().equals(UITheme.PRIMARY)) {
                    item.setBackground(UITheme.BG_MEDIUM_DARK);
                    item.setForeground(Color.WHITE);
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (!item.getBackground().equals(UITheme.PRIMARY)) {
                    item.setBackground(UITheme.BG_DARK);
                    item.setForeground(new Color(148, 163, 184));
                }
            }
        });

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
                if (!label.getBackground().equals(UITheme.PRIMARY)) {
                    label.setBackground(UITheme.BG_MEDIUM_DARK);
                    label.setForeground(Color.WHITE);
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (!label.getBackground().equals(UITheme.PRIMARY)) {
                    label.setBackground(UITheme.BG_DARK);
                    label.setForeground(new Color(148, 163, 184));
                }
            }
        };
    }

    private void navigateTo(String page) {
        currentPage = page;

        // Reset all menu items
        resetMenuItem(lblDashboard);
        resetMenuItem(lblOrders);
        resetMenuItem(lblProducts);
        resetMenuItem(lblCustomers);
        resetMenuItem(lblTables);
        resetMenuItem(lblStaff);
        resetMenuItem(lblReports);
        resetMenuItem(lblRevenue);

        // Highlight active and show panel
        switch (page) {
            case "Dashboard":
                setActive(lblDashboard);
                showDashboard();
                break;
            case "Orders":
                setActive(lblOrders);
                showOrders();
                break;
            case "Products":
                setActive(lblProducts);
                showProducts();
                break;
            case "Customers":
                setActive(lblCustomers);
                showCustomers();
                break;
            case "Tables":
                setActive(lblTables);
                showTables();
                break;
            case "Staff":
                setActive(lblStaff);
                showStaff();
                break;
            case "Reports":
                setActive(lblReports);
                showReports();
                break;
            case "Revenue":
                setActive(lblRevenue);
                showRevenue();
                break;
        }
    }

    private void resetMenuItem(JLabel label) {
        label.setBackground(UITheme.BG_DARK);
        label.setForeground(new Color(148, 163, 184));
    }

    private void setActive(JLabel label) {
        label.setBackground(UITheme.PRIMARY);
        label.setForeground(Color.WHITE);
    }

    private void showDashboard() {
        contentPanel.removeAll();
        contentPanel.add(new Dashboard(), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showOrders() {
        contentPanel.removeAll();
        contentPanel.add(new OrderPanel(), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showProducts() {
        contentPanel.removeAll();
        contentPanel.add(new ProductPanel(), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showCustomers() {
        contentPanel.removeAll();
        contentPanel.add(new CustomerPanel(), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showTables() {
        contentPanel.removeAll();
        contentPanel.add(new TablePanel(), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showStaff() {
        contentPanel.removeAll();
        contentPanel.add(new StaffPanel(), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showReports() {
        contentPanel.removeAll();
        contentPanel.add(new ReportsPanel(), BorderLayout.CENTER);
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
