package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import dao.CustomerDAO;
import dao.DBConnection;
import dao.OrderDAO;

public class Dashboard extends JPanel {

    private JLabel lblProducts, lblOrders, lblRevenue, lblCustomers;
    private MainFrame mainFrame; // Reference to navigate

    public Dashboard() {
        this(null);
    }

    public Dashboard(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_COLOR);

        JPanel content = createContent();
        add(content, BorderLayout.CENTER);

        loadData();
    }

    private JPanel createContent() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(UITheme.BG_COLOR);
        content.setBorder(new EmptyBorder(32, 40, 32, 40));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.BG_COLOR);
        header.setMaximumSize(new Dimension(1100, 60));

        JLabel title = new JLabel("Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(UITheme.TEXT_DARK);

        JLabel date = new JLabel(java.time.LocalDate.now().toString());
        date.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        date.setForeground(UITheme.TEXT_LIGHT);

        header.add(title, BorderLayout.WEST);
        header.add(date, BorderLayout.EAST);

        // Stats cards - 4 columns
        JPanel stats = new JPanel(new GridLayout(1, 4, 20, 0));
        stats.setBackground(UITheme.BG_COLOR);
        stats.setMaximumSize(new Dimension(1100, 110));
        stats.setBorder(new EmptyBorder(28, 0, 28, 0));

        stats.add(createStatCard("Total Products", "0", UITheme.PRIMARY));
        stats.add(createStatCard("Total Orders", "0", UITheme.SUCCESS));
        stats.add(createStatCard("Revenue", "0 VND", UITheme.WARNING));
        stats.add(createStatCard("Customers", "0", UITheme.INFO));

        // Two column layout for bottom section
        JPanel bottomRow = new JPanel(new GridLayout(1, 2, 20, 0));
        bottomRow.setBackground(UITheme.BG_COLOR);
        bottomRow.setMaximumSize(new Dimension(1100, 320));

        bottomRow.add(createActivityPanel());
        bottomRow.add(createQuickActionsPanel());

        content.add(header);
        content.add(stats);
        content.add(bottomRow);

        return content;
    }

    private JPanel createStatCard(String title, String value, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(12, 0));
        card.setBackground(UITheme.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER),
            BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));

        // Icon with solid background
        JPanel iconPanel = new JPanel();
        iconPanel.setBackground(accentColor);
        iconPanel.setPreferredSize(new Dimension(40, 40));
        iconPanel.setLayout(new BorderLayout());

        JLabel lblIcon = new JLabel(getIconText(title));
        lblIcon.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblIcon.setForeground(Color.WHITE);
        lblIcon.setHorizontalAlignment(SwingConstants.CENTER);
        iconPanel.add(lblIcon, BorderLayout.CENTER);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTitle.setForeground(UITheme.TEXT_LIGHT);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblValue.setForeground(UITheme.TEXT_DARK);

        JPanel topPanel = new JPanel(new BorderLayout(10, 0));
        topPanel.setBackground(UITheme.BG_CARD);
        topPanel.add(iconPanel, BorderLayout.WEST);
        topPanel.add(lblTitle, BorderLayout.CENTER);

        card.add(topPanel, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);

        if (title.equals("Total Products")) lblProducts = lblValue;
        else if (title.equals("Total Orders")) lblOrders = lblValue;
        else if (title.equals("Revenue")) lblRevenue = lblValue;
        else if (title.equals("Customers")) lblCustomers = lblValue;

        return card;
    }

    private String getIconText(String title) {
        switch (title) {
            case "Total Products": return "P";
            case "Total Orders": return "O";
            case "Revenue": return "R";
            case "Customers": return "C";
            default: return "*";
        }
    }

    private JPanel createActivityPanel() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(UITheme.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(UITheme.BG_CARD);
        titlePanel.setBorder(new EmptyBorder(20, 20, 16, 20));

        JLabel title = new JLabel("Recent Activity");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(UITheme.TEXT_DARK);

        JLabel viewAll = new JLabel("View All >");
        viewAll.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        viewAll.setForeground(UITheme.PRIMARY);
        viewAll.setCursor(new Cursor(Cursor.HAND_CURSOR));

        titlePanel.add(title, BorderLayout.WEST);
        titlePanel.add(viewAll, BorderLayout.EAST);

        // Activity list
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(UITheme.BG_CARD);
        listPanel.setBorder(new EmptyBorder(0, 20, 16, 20));

        // Load real data
        loadActivityData(listPanel);

        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setBackground(UITheme.BG_CARD);
        scroll.getViewport().setBackground(UITheme.BG_CARD);

        card.add(titlePanel, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);

        return card;
    }

    private void loadActivityData(JPanel listPanel) {
        try {
            OrderDAO orderDAO = new OrderDAO();
            CustomerDAO customerDAO = new CustomerDAO();

            // Get recent orders
            List<Object[]> recentOrders = orderDAO.getRecentOrders(3);
            for (Object[] order : recentOrders) {
                int orderId = (int) order[0];
                String status = (String) order[1];
                double amount = (double) order[2];
                Timestamp timestamp = (Timestamp) order[3];
                String customerName = (String) order[4];

                String desc = String.format("Order #%d - %s (%.0f VND)",
                    orderId, customerName != null ? customerName : "Guest", amount);
                Color dotColor = status.equals("completed") ? UITheme.SUCCESS :
                                  status.equals("pending") ? UITheme.WARNING : UITheme.DANGER;

                listPanel.add(createActivityItem(desc, formatTimeAgo(timestamp), dotColor));
            }

            // Get recent customers
            List<Object[]> recentCustomers = customerDAO.getRecentCustomers(2);
            for (Object[] customer : recentCustomers) {
                int id = (int) customer[0];
                String name = (String) customer[1];
                Timestamp timestamp = (Timestamp) customer[3];

                listPanel.add(createActivityItem("New customer: " + name, formatTimeAgo(timestamp), UITheme.INFO));
            }

        } catch (Exception e) {
            e.printStackTrace();
            // Fallback to mock data if error
            listPanel.add(createActivityItem("Unable to load activity data", "Just now", UITheme.TEXT_LIGHT));
        }
    }

    private String formatTimeAgo(Timestamp timestamp) {
        if (timestamp == null) return "Just now";

        long diff = System.currentTimeMillis() - timestamp.getTime();
        long minutes = diff / 60000;
        long hours = minutes / 60;
        long days = hours / 24;

        if (minutes < 1) return "Just now";
        if (minutes < 60) return minutes + " min ago";
        if (hours < 24) return hours + " hour" + (hours > 1 ? "s" : "") + " ago";
        return days + " day" + (days > 1 ? "s" : "") + " ago";
    }

    private JPanel createActivityItem(String title, String time, Color dotColor) {
        JPanel item = new JPanel(new BorderLayout(12, 0));
        item.setBackground(UITheme.BG_CARD);
        item.setMaximumSize(new Dimension(500, 40));

        // Colored dot
        JPanel dot = new JPanel();
        dot.setBackground(dotColor);
        dot.setPreferredSize(new Dimension(8, 8));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblTitle.setForeground(UITheme.TEXT_DARK);

        JLabel lblTime = new JLabel(time);
        lblTime.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTime.setForeground(UITheme.TEXT_LIGHT);

        item.add(dot, BorderLayout.WEST);
        item.add(lblTitle, BorderLayout.CENTER);
        item.add(lblTime, BorderLayout.EAST);

        return item;
    }

    private JPanel createQuickActionsPanel() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(UITheme.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(UITheme.BG_CARD);
        titlePanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Quick Actions");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(UITheme.TEXT_DARK);
        titlePanel.add(title, BorderLayout.WEST);

        JPanel actionsPanel = new JPanel();
        actionsPanel.setLayout(new BoxLayout(actionsPanel, BoxLayout.Y_AXIS));
        actionsPanel.setBackground(UITheme.BG_CARD);
        actionsPanel.setBorder(new EmptyBorder(0, 20, 20, 20));

        // Action buttons
        actionsPanel.add(createActionButton("Create New Order", UITheme.SUCCESS));
        actionsPanel.add(Box.createVerticalStrut(10));
        actionsPanel.add(createActionButton("Add New Product", UITheme.PRIMARY));
        actionsPanel.add(Box.createVerticalStrut(10));
        actionsPanel.add(createActionButton("Add New Customer", UITheme.INFO));
        actionsPanel.add(Box.createVerticalStrut(10));
        actionsPanel.add(createActionButton("Manage Tables", new Color(139, 92, 246)));

        card.add(titlePanel, BorderLayout.NORTH);
        card.add(actionsPanel, BorderLayout.CENTER);

        return card;
    }

    private JButton createActionButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(color);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(400, 44));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);

        btn.addActionListener(e -> handleAction(text));

        return btn;
    }

    private void handleAction(String action) {
        String page = "";
        if (action.contains("Order")) page = "Orders";
        else if (action.contains("Product")) page = "Products";
        else if (action.contains("Customer")) page = "Customers";
        else if (action.contains("Table")) page = "Tables";

        if (mainFrame != null && !page.isEmpty()) {
            mainFrame.navigateTo(page);
        } else {
            JOptionPane.showMessageDialog(this,
                "Navigating to: " + page,
                "Quick Action",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void loadData() {
        try {
            Connection conn = DBConnection.getConnection();
            Statement st = conn.createStatement();

            ResultSet rs1 = st.executeQuery("SELECT COUNT(*) FROM products");
            if (rs1.next() && lblProducts != null) {
                lblProducts.setText(rs1.getString(1));
            }

            ResultSet rs2 = st.executeQuery("SELECT COUNT(*) FROM orders");
            if (rs2.next() && lblOrders != null) {
                lblOrders.setText(rs2.getString(1));
            }

            ResultSet rs3 = st.executeQuery("SELECT COALESCE(SUM(total_amount), 0) FROM orders");
            if (rs3.next() && lblRevenue != null) {
                lblRevenue.setText(formatCurrency(rs3.getLong(1)));
            }

            ResultSet rs4 = st.executeQuery("SELECT COUNT(*) FROM customers");
            if (rs4.next() && lblCustomers != null) {
                lblCustomers.setText(rs4.getString(1));
            }

            rs1.close();
            rs2.close();
            rs3.close();
            rs4.close();
            st.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String formatCurrency(long amount) {
        if (amount >= 1000000) {
            return String.format("%.1fM VND", amount / 1000000.0);
        } else if (amount >= 1000) {
            return String.format("%.1fK VND", amount / 1000.0);
        }
        return amount + " VND";
    }
}
