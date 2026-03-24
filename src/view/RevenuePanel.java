package view;

import dao.OrderDAO;
import dao.ProductDAO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Arc2D;
import java.util.List;
import java.util.Map;

public class RevenuePanel extends JPanel {
    private OrderDAO orderDAO = new OrderDAO();
    private ProductDAO productDAO = new ProductDAO();

    // Colors
    private static final Color PRIMARY = new Color(99, 102, 241);
    private static final Color SUCCESS = new Color(34, 197, 94);
    private static final Color WARNING = new Color(251, 146, 60);
    private static final Color DANGER = new Color(239, 68, 68);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color BG_COLOR = new Color(243, 244, 246);
    private static final Color TEXT_DARK = new Color(17, 24, 39);
    private static final Color TEXT_GRAY = new Color(107, 114, 128);
    private static final Color BORDER = new Color(229, 231, 235);
    private static final Color[] CHART_COLORS = {
        new Color(99, 102, 241),
        new Color(34, 197, 94),
        new Color(251, 146, 60),
        new Color(239, 68, 68),
        new Color(168, 85, 247),
        new Color(20, 184, 166),
        new Color(244, 114, 192)
    };

    public RevenuePanel() {
        setLayout(new BorderLayout());
        setBackground(BG_COLOR);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_COLOR);
        header.setMaximumSize(new Dimension(800, 60));

        JLabel title = new JLabel("Revenue Statistics");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(TEXT_DARK);

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.setFont(new Font("Arial", Font.PLAIN, 12));
        btnRefresh.addActionListener(e -> repaint());
        btnRefresh.setFocusPainted(false);

        header.add(title, BorderLayout.WEST);
        header.add(btnRefresh, BorderLayout.EAST);

        // Stats row
        JPanel stats = new JPanel(new GridLayout(1, 4, 16, 0));
        stats.setBackground(BG_COLOR);
        stats.setMaximumSize(new Dimension(800, 100));
        stats.setBorder(new EmptyBorder(0, 0, 24, 0));

        stats.add(createStatCard("Total Revenue", getTotalRevenue(), SUCCESS));
        stats.add(createStatCard("Completed Orders", getOrderCount("completed"), PRIMARY));
        stats.add(createStatCard("Pending Orders", getOrderCount("pending"), WARNING));
        stats.add(createStatCard("Products", getProductCount(), PRIMARY));

        // Charts
        JSplitPane charts = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        charts.setDividerLocation(450);
        charts.setResizeWeight(1);

        JPanel leftChart = createBarChartPanel();
        JPanel rightChart = createPieChartPanel();

        charts.setLeftComponent(leftChart);
        charts.setRightComponent(rightChart);

        add(header, BorderLayout.NORTH);
        add(stats, BorderLayout.CENTER);
        add(charts, BorderLayout.SOUTH);
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
        lblValue.setFont(new Font("Arial", Font.BOLD, 24));
        lblValue.setForeground(accent);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);

        return card;
    }

    private String getTotalRevenue() {
        try {
            double total = orderDAO.getTotalRevenue();
            return String.format("%,.0f VND", total);
        } catch (Exception e) { return "0 VND"; }
    }

    private String getOrderCount(String status) {
        try {
            return String.valueOf(orderDAO.getOrderCountByStatus(status));
        } catch (Exception e) { return "0"; }
    }

    private String getProductCount() {
        try {
            return String.valueOf(productDAO.getAll().size());
        } catch (Exception e) { return "0"; }
    }

    private JPanel createBarChartPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            new EmptyBorder(16, 16, 16, 16)
        ));

        JLabel title = new JLabel("Revenue by Category");
        title.setFont(new Font("Arial", Font.BOLD, 14));
        title.setForeground(TEXT_DARK);

        JPanel chartPanel = new JPanel(new GridLayout(1, 1)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBarChart(g);
            }
        };
        chartPanel.setBackground(CARD_BG);

        panel.add(title, BorderLayout.NORTH);
        panel.add(chartPanel, BorderLayout.CENTER);

        return panel;
    }

    private void drawBarChart(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        try {
            List<Object[]> data = orderDAO.getRevenueByCategory();
            if (data.isEmpty()) {
                g2.setColor(TEXT_GRAY);
                g2.setFont(new Font("Arial", Font.PLAIN, 14));
                g2.drawString("No data available", 150, 150);
                return;
            }

            int chartWidth = getWidth() - 80;
            int chartHeight = getHeight() - 60;
            int startX = 60;
            int startY = 30;

            double maxValue = 0;
            for (Object[] row : data) {
                maxValue = Math.max(maxValue, (Double) row[1]);
            }
            if (maxValue == 0) maxValue = 1;

            int barCount = data.size();
            int barWidth = Math.min(60, (chartWidth - 20) / barCount - 10);
            int gap = (chartWidth - barCount * barWidth) / (barCount + 1);

            g2.setColor(TEXT_GRAY);
            g2.setFont(new Font("Arial", Font.PLAIN, 10));

            for (int i = 0; i < data.size(); i++) {
                Object[] row = data.get(i);
                String label = (String) row[0];
                double value = (Double) row[1];
                int barHeight = (int) ((value / maxValue) * chartHeight);

                int x = startX + gap + i * (barWidth + gap);
                int y = startY + chartHeight - barHeight;

                // Bar
                g2.setColor(CHART_COLORS[i % CHART_COLORS.length]);
                g2.fillRect(x, y, barWidth, barHeight);

                // Value
                g2.setColor(TEXT_DARK);
                g2.setFont(new Font("Arial", Font.BOLD, 10));
                g2.drawString(String.format("%,.0f", value), x, y - 5);

                // Label
                g2.setColor(TEXT_GRAY);
                g2.setFont(new Font("Arial", Font.PLAIN, 9));
                String shortLabel = label.length() > 10 ? label.substring(0, 8) + "..." : label;
                g2.drawString(shortLabel, x, startY + chartHeight + 15);
            }
        } catch (Exception e) {
            g2.setColor(TEXT_GRAY);
            g2.setFont(new Font("Arial", Font.PLAIN, 14));
            g2.drawString("Error loading data", 150, 150);
        }
    }

    private JPanel createPieChartPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            new EmptyBorder(16, 16, 16, 16)
        ));

        JLabel title = new JLabel("Order Status");
        title.setFont(new Font("Arial", Font.BOLD, 14));
        title.setForeground(TEXT_DARK);

        JPanel chartPanel = new JPanel(new GridLayout(1, 1)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawPieChart(g);
            }
        };
        chartPanel.setBackground(CARD_BG);

        // Legend
        JPanel legend = new JPanel(new GridLayout(0, 1, 4, 4));
        legend.setBackground(CARD_BG);
        legend.setBorder(new EmptyBorder(8, 8, 8, 8));

        try {
            int completed = orderDAO.getOrderCountByStatus("completed");
            int pending = orderDAO.getOrderCountByStatus("pending");
            int cancelled = orderDAO.getOrderCountByStatus("cancelled");
            int total = completed + pending + cancelled;

            if (total > 0) {
                legend.add(createLegendItem("Completed", completed, PRIMARY));
                legend.add(createLegendItem("Pending", pending, WARNING));
                legend.add(createLegendItem("Cancelled", cancelled, DANGER));
            }
        } catch (Exception e) { }

        panel.add(title, BorderLayout.NORTH);
        panel.add(chartPanel, BorderLayout.CENTER);
        panel.add(legend, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createLegendItem(String label, int count, Color color) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setBackground(CARD_BG);

        JPanel colorBox = new JPanel();
        colorBox.setBackground(color);
        colorBox.setPreferredSize(new Dimension(16, 16));

        JLabel text = new JLabel(label + ": " + count);
        text.setFont(new Font("Arial", Font.PLAIN, 12));
        text.setForeground(TEXT_DARK);

        row.add(colorBox, BorderLayout.WEST);
        row.add(text, BorderLayout.CENTER);

        return row;
    }

    private void drawPieChart(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        try {
            int completed = orderDAO.getOrderCountByStatus("completed");
            int pending = orderDAO.getOrderCountByStatus("pending");
            int cancelled = orderDAO.getOrderCountByStatus("cancelled");
            int total = completed + pending + cancelled;

            if (total == 0) {
                g2.setColor(TEXT_GRAY);
                g2.setFont(new Font("Arial", Font.PLAIN, 14));
                g2.drawString("No orders yet", 130, 150);
                return;
            }

            int size = Math.min(getWidth(), getHeight()) - 40;
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;

            double[] values = {completed, pending, cancelled};
            Color[] colors = {PRIMARY, WARNING, DANGER};
            double startAngle = 0;

            for (int i = 0; i < values.length; i++) {
                if (values[i] > 0) {
                    double angle = (values[i] / total) * 360;
                    g2.setColor(colors[i]);
                    g2.fill(new Arc2D.Double(x, y, size, size, startAngle, angle, Arc2D.PIE));
                    startAngle += angle;
                }
            }

            // Draw donut hole
            g2.setColor(CARD_BG);
            g2.fill(new Ellipse2D.Double(x + size * 0.3, y + size * 0.3, size * 0.4, size * 0.4));

            // Center text
            g2.setColor(TEXT_DARK);
            g2.setFont(new Font("Arial", Font.BOLD, 16));
            String totalText = String.valueOf(total);
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(totalText);
            g2.drawString(totalText, x + size / 2 - textWidth / 2, y + size / 2 + 6);

        } catch (Exception e) {
            g2.setColor(TEXT_GRAY);
            g2.setFont(new Font("Arial", Font.PLAIN, 14));
            g2.drawString("Error loading data", 130, 150);
        }
    }
}
