package view;

import dao.DBConnection;
import dao.OrderDAO;
import dao.ProductDAO;
import dao.CustomerDAO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;
import java.util.*;

public class ReportsPanel extends JPanel {
    private OrderDAO orderDAO = new OrderDAO();
    private ProductDAO productDAO = new ProductDAO();
    private CustomerDAO customerDAO = new CustomerDAO();

    public ReportsPanel() {
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_COLOR);

        add(createHeader(), BorderLayout.NORTH);
        add(createContent(), BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG_COLOR);
        panel.setBorder(new EmptyBorder(20, 24, 20, 24));

        JLabel title = new JLabel("Reports & Analytics");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_DARK);

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.setFont(UITheme.FONT_BUTTON);
        btnRefresh.setBackground(UITheme.PRIMARY);
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFocusPainted(false);
        btnRefresh.addActionListener(e -> refreshData());

        panel.add(title, BorderLayout.WEST);
        panel.add(btnRefresh, BorderLayout.EAST);

        return panel;
    }

    private JPanel createContent() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG_COLOR);
        panel.setBorder(new EmptyBorder(0, 24, 20, 24));

        // Top cards
        JPanel cardsPanel = new JPanel(new GridLayout(1, 4, 16, 16));
        cardsPanel.setBackground(UITheme.BG_COLOR);
        cardsPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        cardsPanel.add(createStatCard("Today's Revenue", getTodayRevenue(), UITheme.SUCCESS));
        cardsPanel.add(createStatCard("Total Orders", getTodayOrders(), UITheme.PRIMARY));
        cardsPanel.add(createStatCard("Products Sold", getTodayProductsSold(), UITheme.INFO));
        cardsPanel.add(createStatCard("Customers", getTotalCustomers(), UITheme.WARNING));

        // Bottom panels
        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 16, 16));
        bottomPanel.setBackground(UITheme.BG_COLOR);

        bottomPanel.add(createBestSellersPanel());
        bottomPanel.add(createCategoryPanel());

        panel.add(cardsPanel, BorderLayout.NORTH);
        panel.add(bottomPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(UITheme.BG_CARD);
        card.setBorder(RoundedBorder.create(12, UITheme.BG_CARD));
        card.setPreferredSize(new Dimension(200, 100));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(UITheme.FONT_BODY);
        lblTitle.setForeground(UITheme.TEXT_MEDIUM);
        lblTitle.setBorder(new EmptyBorder(16, 16, 8, 16));

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblValue.setForeground(color);
        lblValue.setBorder(new EmptyBorder(0, 16, 16, 16));

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);

        return card;
    }

    private JPanel createBestSellersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG_CARD);
        panel.setBorder(RoundedBorder.create(12, UITheme.BG_CARD));

        JLabel title = new JLabel("Top Selling Products");
        title.setFont(UITheme.FONT_SUBTITLE);
        title.setForeground(UITheme.TEXT_DARK);
        title.setBorder(new EmptyBorder(16, 16, 16, 16));

        String[] columns = {"Product", "Quantity", "Revenue"};
        String[][] data = getBestSellers();

        JTable table = new JTable(data, columns);
        table.setFont(UITheme.FONT_BODY);
        table.setRowHeight(35);
        table.setBackground(UITheme.BG_CARD);
        table.setForeground(UITheme.TEXT_DARK);
        table.getTableHeader().setBackground(UITheme.PRIMARY);
        table.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(UITheme.BG_CARD);

        panel.add(title, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createCategoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG_CARD);
        panel.setBorder(RoundedBorder.create(12, UITheme.BG_CARD));

        JLabel title = new JLabel("Sales by Category");
        title.setFont(UITheme.FONT_SUBTITLE);
        title.setForeground(UITheme.TEXT_DARK);
        title.setBorder(new EmptyBorder(16, 16, 16, 16));

        String[] columns = {"Category", "Orders", "Revenue"};
        String[][] data = getCategorySales();

        JTable table = new JTable(data, columns);
        table.setFont(UITheme.FONT_BODY);
        table.setRowHeight(35);
        table.setBackground(UITheme.BG_CARD);
        table.setForeground(UITheme.TEXT_DARK);
        table.getTableHeader().setBackground(UITheme.SECONDARY);
        table.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(UITheme.BG_CARD);

        panel.add(title, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private String getTodayRevenue() {
        try {
            String sql = "SELECT COALESCE(SUM(total_amount), 0) FROM orders WHERE DATE(order_date) = CURDATE() AND status = 'completed'";
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return String.format("%,.0f VND", rs.getDouble(1));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return "0 VND";
    }

    private String getTodayOrders() {
        try {
            String sql = "SELECT COUNT(*) FROM orders WHERE DATE(order_date) = CURDATE()";
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return String.valueOf(rs.getInt(1));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return "0";
    }

    private String getTodayProductsSold() {
        try {
            String sql = "SELECT COALESCE(SUM(oi.quantity), 0) FROM order_items oi JOIN orders o ON oi.order_id = o.id WHERE DATE(o.order_date) = CURDATE() AND o.status = 'completed'";
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return String.valueOf(rs.getInt(1));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return "0";
    }

    private String getTotalCustomers() {
        try {
            return String.valueOf(customerDAO.getAll().size());
        } catch (Exception e) { return "0"; }
    }

    private String[][] getBestSellers() {
        try {
            String sql = "SELECT p.name, SUM(oi.quantity) as qty, SUM(oi.total_price) as revenue " +
                "FROM order_items oi JOIN products p ON oi.product_id = p.id " +
                "JOIN orders o ON oi.order_id = o.id " +
                "WHERE o.status = 'completed' AND o.order_date >= DATE_SUB(CURDATE(), INTERVAL 30 DAY) " +
                "GROUP BY p.id ORDER BY qty DESC LIMIT 5";

            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            java.util.List<String[]> list = new ArrayList<>();
            while (rs.next()) {
                list.add(new String[]{rs.getString(1), rs.getString(2), String.format("%,.0f VND", rs.getDouble(3))});
            }
            return list.toArray(new String[0][0]);
        } catch (Exception e) { e.printStackTrace(); }
        return new String[0][0];
    }

    private String[][] getCategorySales() {
        try {
            String sql = "SELECT p.category, COUNT(*), COALESCE(SUM(oi.total_price), 0) " +
                "FROM order_items oi JOIN products p ON oi.product_id = p.id " +
                "JOIN orders o ON oi.order_id = o.id " +
                "WHERE o.status = 'completed' AND o.order_date >= DATE_SUB(CURDATE(), INTERVAL 30 DAY) " +
                "GROUP BY p.category ORDER BY revenue DESC";

            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            java.util.List<String[]> list = new ArrayList<>();
            while (rs.next()) {
                list.add(new String[]{rs.getString(1), rs.getString(2), String.format("%,.0f VND", rs.getDouble(3))});
            }
            return list.toArray(new String[0][0]);
        } catch (Exception e) { e.printStackTrace(); }
        return new String[0][0];
    }

    private void refreshData() {
        removeAll();
        add(createHeader(), BorderLayout.NORTH);
        add(createContent(), BorderLayout.CENTER);
        revalidate();
        repaint();
    }
}
