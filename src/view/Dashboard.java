package view;

import dao.DBConnection;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
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

    private static final Color PRIMARY = new Color(99, 102, 241);
    private static final Color SUCCESS = new Color(34, 197, 94);
    private static final Color WARNING = new Color(251, 146, 60);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color BG_COLOR = new Color(243, 244, 246);
    private static final Color TEXT_DARK = new Color(17, 24, 39);
    private static final Color TEXT_GRAY = new Color(107, 114, 128);
    private static final Color BORDER = new Color(229, 231, 235);

    private JLabel lblProducts, lblOrders, lblRevenue;

    public Dashboard() {
        setLayout(new BorderLayout());
        setBackground(BG_COLOR);

        JPanel content = createContent();
        add(content, BorderLayout.CENTER);

        loadData();
    }

    private JPanel createContent() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(BG_COLOR);
        content.setBorder(new EmptyBorder(24, 24, 24, 24));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_COLOR);
        header.setMaximumSize(new Dimension(800, 60));
        header.setPreferredSize(new Dimension(800, 60));

        JLabel title = new JLabel("Dashboard");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(TEXT_DARK);

        JLabel date = new JLabel(java.time.LocalDate.now().toString());
        date.setFont(new Font("Arial", Font.PLAIN, 12));
        date.setForeground(TEXT_GRAY);

        header.add(title, BorderLayout.WEST);
        header.add(date, BorderLayout.EAST);

        // Stats row
        JPanel stats = new JPanel(new GridLayout(1, 3, 16, 0));
        stats.setBackground(BG_COLOR);
        stats.setMaximumSize(new Dimension(800, 120));
        stats.setPreferredSize(new Dimension(800, 120));
        stats.setBorder(new EmptyBorder(24, 0, 24, 0));

        stats.add(createStatCard("Products", "0", SUCCESS));
        stats.add(createStatCard("Orders", "0", PRIMARY));
        stats.add(createStatCard("Revenue", "0 VND", WARNING));

        // Table section
        JPanel tableSection = new JPanel(new BorderLayout());
        tableSection.setBackground(CARD_BG);
        tableSection.setMaximumSize(new Dimension(800, 300));
        tableSection.setPreferredSize(new Dimension(800, 300));
        tableSection.setBorder(BorderFactory.createLineBorder(BORDER));

        JLabel tableTitle = new JLabel("Recent Orders");
        tableTitle.setFont(new Font("Arial", Font.BOLD, 14));
        tableTitle.setForeground(TEXT_DARK);
        tableTitle.setBorder(new EmptyBorder(16, 16, 16, 16));

        String[] cols = {"ID", "Customer", "Amount", "Status"};
        Object[][] data = {
            {"#001", "Nguyen Van A", "50.000", "Pending"},
            {"#002", "Tran Thi B", "75.000", "Completed"},
            {"#003", "Le Van C", "120.000", "Completed"}
        };

        JTable table = new JTable(data, cols);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setRowHeight(30);
        table.setGridColor(BORDER);
        table.setShowGrid(true);
        table.getTableHeader().setBackground(BG_COLOR);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        tableSection.add(tableTitle, BorderLayout.NORTH);
        tableSection.add(scroll, BorderLayout.CENTER);

        content.add(header);
        content.add(stats);
        content.add(tableSection);

        return content;
    }

    private JPanel createStatCard(String title, String value, Color accent) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            new EmptyBorder(16, 16, 16, 16)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Arial", Font.PLAIN, 12));
        lblTitle.setForeground(TEXT_GRAY);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Arial", Font.BOLD, 28));
        lblValue.setForeground(accent);

        if (title.equals("Products")) lblProducts = lblValue;
        else if (title.equals("Orders")) lblOrders = lblValue;
        else if (title.equals("Revenue")) lblRevenue = lblValue;

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);

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
