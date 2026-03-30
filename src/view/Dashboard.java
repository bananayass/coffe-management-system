package view;

import dao.DBConnection;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class Dashboard extends JPanel {

    private JLabel lblProducts, lblOrders, lblRevenue, lblCustomers;

    public Dashboard() {
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

        stats.add(createStatCard("Total Products", "0", UITheme.PRIMARY, UITheme.PRIMARY_LIGHT));
        stats.add(createStatCard("Total Orders", "0", UITheme.SUCCESS, new Color(34, 197, 94)));
        stats.add(createStatCard("Revenue", "0 VND", UITheme.WARNING, new Color(245, 158, 11)));
        stats.add(createStatCard("Customers", "0", UITheme.INFO, new Color(59, 130, 246)));

        // Two column layout for bottom section
        JPanel bottomRow = new JPanel(new GridLayout(1, 2, 20, 0));
        bottomRow.setBackground(UITheme.BG_COLOR);
        bottomRow.setMaximumSize(new Dimension(1100, 300));

        bottomRow.add(createActivityPanel());
        bottomRow.add(createQuickActionsPanel());

        content.add(header);
        content.add(stats);
        content.add(bottomRow);

        return content;
    }

    private JPanel createStatCard(String title, String value, Color accent, Color lightAccent) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(UITheme.BG_CARD);

        // Subtle shadow effect using compound border
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER),
            new EmptyBorder(20, 24, 20, 24)
        ));

        // Top section: Icon circle + title
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(UITheme.BG_CARD);

        // Colored circle icon
        JPanel iconCircle = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(lightAccent);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        iconCircle.setPreferredSize(new Dimension(36, 36));
        iconCircle.setOpaque(false);

        // Icon label centered in circle
        JLabel lblIcon = new JLabel(getIconEmoji(title));
        lblIcon.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblIcon.setHorizontalAlignment(SwingConstants.CENTER);
        iconCircle.setLayout(new BorderLayout());
        iconCircle.add(lblIcon, BorderLayout.CENTER);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblTitle.setForeground(UITheme.TEXT_LIGHT);

        topPanel.add(iconCircle, BorderLayout.WEST);
        topPanel.add(lblTitle, BorderLayout.CENTER);

        // Value
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblValue.setForeground(UITheme.TEXT_DARK);

        card.add(topPanel, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);

        if (title.equals("Total Products")) lblProducts = lblValue;
        else if (title.equals("Total Orders")) lblOrders = lblValue;
        else if (title.equals("Revenue")) lblRevenue = lblValue;
        else if (title.equals("Customers")) lblCustomers = lblValue;

        return card;
    }

    private String getIconEmoji(String title) {
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
            new EmptyBorder(0, 0, 0, 0)
        ));

        JLabel title = new JLabel("Recent Activity");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(UITheme.TEXT_DARK);
        title.setBorder(new EmptyBorder(20, 20, 16, 20));

        // Activity list
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(UITheme.BG_CARD);
        listPanel.setBorder(new EmptyBorder(0, 20, 16, 20));

        listPanel.add(createActivityItem("New order #001 placed", "2 min ago", UITheme.SUCCESS));
        listPanel.add(createActivityItem("Product 'Coffee' updated", "15 min ago", UITheme.INFO));
        listPanel.add(createActivityItem("New customer registered", "1 hour ago", UITheme.PRIMARY));
        listPanel.add(createActivityItem("Order #002 completed", "2 hours ago", UITheme.SUCCESS));
        listPanel.add(createActivityItem("Inventory low for 'Milk'", "3 hours ago", UITheme.WARNING));

        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setBackground(UITheme.BG_CARD);
        scroll.getViewport().setBackground(UITheme.BG_CARD);

        card.add(title, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);

        return card;
    }

    private JPanel createActivityItem(String title, String time, Color dotColor) {
        JPanel item = new JPanel(new BorderLayout(12, 0));
        item.setBackground(UITheme.BG_CARD);
        item.setMaximumSize(new Dimension(500, 36));

        // Colored dot
        JPanel dot = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(dotColor);
                g2.fillOval(0, 0, 8, 8);
                g2.dispose();
            }
        };
        dot.setPreferredSize(new Dimension(8, 8));
        dot.setOpaque(false);

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
            new EmptyBorder(0, 0, 0, 0)
        ));

        JLabel title = new JLabel("Quick Actions");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(UITheme.TEXT_DARK);
        title.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel actionsPanel = new JPanel();
        actionsPanel.setLayout(new BoxLayout(actionsPanel, BoxLayout.Y_AXIS));
        actionsPanel.setBackground(UITheme.BG_CARD);
        actionsPanel.setBorder(new EmptyBorder(0, 20, 20, 20));

        actionsPanel.add(createActionButton("Create New Order", UITheme.SUCCESS));
        actionsPanel.add(Box.createVerticalStrut(12));
        actionsPanel.add(createActionButton("Add New Product", UITheme.PRIMARY));
        actionsPanel.add(Box.createVerticalStrut(12));
        actionsPanel.add(createActionButton("Add New Customer", UITheme.INFO));
        actionsPanel.add(Box.createVerticalStrut(12));
        actionsPanel.add(createActionButton("Manage Tables", new Color(139, 92, 246)));

        card.add(title, BorderLayout.NORTH);
        card.add(actionsPanel, BorderLayout.CENTER);

        return card;
    }

    private JButton createActionButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(color);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(300, 48));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setHorizontalAlignment(SwingConstants.LEFT);

        // Subtle hover effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            private final Color original = color;
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(color.darker());
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(original);
            }
        });

        return btn;
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
