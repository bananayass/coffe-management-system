package view;

import dao.DBConnection;
import java.awt.BorderLayout;
import java.awt.Color;
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
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;

public class Dashboard extends JPanel {

    private JLabel lblProducts, lblOrders, lblRevenue;

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
        content.setBorder(new EmptyBorder(32, 32, 32, 32));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.BG_COLOR);
        header.setMaximumSize(new Dimension(900, 60));

        JLabel title = new JLabel("Dashboard");
        title.setFont(UITheme.FONT_HEADER);
        title.setForeground(UITheme.TEXT_DARK);

        JLabel date = new JLabel(java.time.LocalDate.now().toString());
        date.setFont(UITheme.FONT_SMALL);
        date.setForeground(UITheme.TEXT_LIGHT);

        header.add(title, BorderLayout.WEST);
        header.add(date, BorderLayout.EAST);

        // Stats cards
        JPanel stats = new JPanel(new GridLayout(1, 3, 24, 0));
        stats.setBackground(UITheme.BG_COLOR);
        stats.setMaximumSize(new Dimension(900, 140));
        stats.setBorder(new EmptyBorder(32, 0, 32, 0));

        stats.add(createStatCard("Products", "0", UITheme.PRIMARY, "📦"));
        stats.add(createStatCard("Orders", "0", UITheme.SUCCESS, "🛒"));
        stats.add(createStatCard("Revenue", "0 VND", UITheme.WARNING, "💰"));

        // Table section - White card with shadow effect
        JPanel tableSection = new JPanel(new BorderLayout());
        tableSection.setBackground(UITheme.BG_CARD);
        tableSection.setMaximumSize(new Dimension(900, 350));
        tableSection.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER),
            new EmptyBorder(0, 0, 0, 0)
        ));

        JLabel tableTitle = new JLabel("Recent Orders");
        tableTitle.setFont(UITheme.FONT_SUBTITLE);
        tableTitle.setForeground(UITheme.TEXT_DARK);
        tableTitle.setBorder(new EmptyBorder(20, 20, 20, 20));

        String[] cols = {"ID", "Customer", "Amount", "Status"};
        Object[][] data = {
            {"#001", "Nguyen Van A", "50.000", "⏳ Pending"},
            {"#002", "Tran Thi B", "75.000", "✅ Completed"},
            {"#003", "Le Van C", "120.000", "✅ Completed"}
        };

        JTable table = new JTable(data, cols);
        table.setFont(UITheme.FONT_BODY);
        table.setBackground(UITheme.BG_CARD);
        table.setForeground(UITheme.TEXT_DARK);
        table.setRowHeight(40);
        table.setGridColor(UITheme.BORDER);
        table.setShowGrid(true);
        table.getTableHeader().setBackground(UITheme.BG_MEDIUM);
        table.getTableHeader().setForeground(UITheme.TEXT_DARK);
        table.getTableHeader().setFont(UITheme.FONT_BODY);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(UITheme.BG_CARD);

        tableSection.add(tableTitle, BorderLayout.NORTH);
        tableSection.add(scroll, BorderLayout.CENTER);

        content.add(header);
        content.add(stats);
        content.add(tableSection);

        return content;
    }

    private JPanel createStatCard(String title, String value, Color accent, String icon) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(UITheme.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER),
            new EmptyBorder(20, 20, 20, 20)
        ));

        // Icon
        JLabel lblIcon = new JLabel(icon);
        lblIcon.setFont(new Font("Segoe UI", Font.PLAIN, 32));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(UITheme.BG_CARD);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(UITheme.FONT_BODY);
        lblTitle.setForeground(UITheme.TEXT_LIGHT);

        topPanel.add(lblTitle, BorderLayout.NORTH);
        topPanel.add(lblIcon, BorderLayout.EAST);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblValue.setForeground(accent);

        card.add(topPanel, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);

        if (title.equals("Products")) lblProducts = lblValue;
        else if (title.equals("Orders")) lblOrders = lblValue;
        else if (title.equals("Revenue")) lblRevenue = lblValue;

        return card;
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
                lblRevenue.setText(rs3.getString(1) + " VND");
            }

            rs1.close();
            rs2.close();
            rs3.close();
            st.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
