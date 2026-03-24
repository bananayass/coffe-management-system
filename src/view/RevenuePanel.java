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

public class RevenuePanel extends JPanel {
    private OrderDAO orderDAO = new OrderDAO();
    private ProductDAO productDAO = new ProductDAO();

    private static final Color[] CHART_COLORS = {
        UITheme.PRIMARY, UITheme.SUCCESS, UITheme.WARNING, UITheme.DANGER,
        new Color(168, 85, 247), new Color(20, 184, 166), new Color(244, 114, 192)
    };

    public RevenuePanel() {
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_COLOR);
        setBorder(new EmptyBorder(32, 32, 32, 32));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.BG_COLOR);
        header.setMaximumSize(new Dimension(900, 60));

        JLabel title = new JLabel("Revenue Statistics");
        title.setFont(UITheme.FONT_HEADER);
        title.setForeground(UITheme.TEXT_DARK);

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.setFont(UITheme.FONT_BODY);
        btnRefresh.setBackground(UITheme.PRIMARY);
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFocusPainted(false);
        btnRefresh.addActionListener(e -> repaint());

        header.add(title, BorderLayout.WEST);
        header.add(btnRefresh, BorderLayout.EAST);

        JPanel stats = new JPanel(new GridLayout(1, 4, 16, 0));
        stats.setBackground(UITheme.BG_COLOR);
        stats.setMaximumSize(new Dimension(900, 100));
        stats.setBorder(new EmptyBorder(0, 0, 32, 0));

        stats.add(createStatCard("Total Revenue", getTotalRevenue(), UITheme.SUCCESS));
        stats.add(createStatCard("Completed", getOrderCount("completed"), UITheme.PRIMARY));
        stats.add(createStatCard("Pending", getOrderCount("pending"), UITheme.WARNING));
        stats.add(createStatCard("Products", getProductCount(), UITheme.PRIMARY));

        JSplitPane charts = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        charts.setDividerLocation(450);
        charts.setResizeWeight(1);
        charts.setBackground(UITheme.BG_COLOR);

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
        card.setBackground(UITheme.BG_CARD);
        card.setBorder(RoundedBorder.create(12, UITheme.BG_LIGHT));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(UITheme.FONT_BODY);
        lblTitle.setForeground(UITheme.TEXT_LIGHT);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblValue.setForeground(accent);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);

        return card;
    }

    private String getTotalRevenue() {
        try { return String.format("%,.0f VND", orderDAO.getTotalRevenue()); }
        catch (Exception e) { return "0 VND"; }
    }

    private String getOrderCount(String status) {
        try { return String.valueOf(orderDAO.getOrderCountByStatus(status)); }
        catch (Exception e) { return "0"; }
    }

    private String getProductCount() {
        try { return String.valueOf(productDAO.getAll().size()); }
        catch (Exception e) { return "0"; }
    }

    private JPanel createBarChartPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG_CARD);
        panel.setBorder(RoundedBorder.create(12, UITheme.BG_LIGHT));

        JLabel title = new JLabel("Revenue by Category");
        title.setFont(UITheme.FONT_SUBTITLE);
        title.setForeground(UITheme.TEXT_DARK);
        title.setBorder(new EmptyBorder(16, 16, 16, 16));

        JPanel chartPanel = new JPanel(new GridLayout(1, 1)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBarChart(g);
            }
        };
        chartPanel.setBackground(UITheme.BG_CARD);

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
                g2.setColor(UITheme.TEXT_LIGHT);
                g2.setFont(UITheme.FONT_BODY);
                g2.drawString("No data available", 150, 150);
                return;
            }

            int chartWidth = getWidth() - 80;
            int chartHeight = getHeight() - 60;
            int startX = 60;
            int startY = 30;

            double maxValue = 0;
            for (Object[] row : data) maxValue = Math.max(maxValue, (Double) row[1]);
            if (maxValue == 0) maxValue = 1;

            int barCount = data.size();
            int barWidth = Math.min(60, (chartWidth - 20) / barCount - 10);
            int gap = (chartWidth - barCount * barWidth) / (barCount + 1);

            for (int i = 0; i < data.size(); i++) {
                Object[] row = data.get(i);
                double value = (Double) row[1];
                int barHeight = (int) ((value / maxValue) * chartHeight);

                int x = startX + gap + i * (barWidth + gap);
                int y = startY + chartHeight - barHeight;

                g2.setColor(CHART_COLORS[i % CHART_COLORS.length]);
                g2.fillRect(x, y, barWidth, barHeight);

                g2.setColor(UITheme.TEXT_DARK);
                g2.setFont(UITheme.FONT_SMALL);
                g2.drawString(String.format("%,.0f", value), x, y - 5);

                String label = ((String) row[0]).length() > 10 ? ((String) row[0]).substring(0, 8) + "..." : (String) row[0];
                g2.setColor(UITheme.TEXT_LIGHT);
                g2.drawString(label, x, startY + chartHeight + 15);
            }
        } catch (Exception e) {
            g2.setColor(UITheme.TEXT_LIGHT);
            g2.drawString("Error loading data", 150, 150);
        }
    }

    private JPanel createPieChartPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG_CARD);
        panel.setBorder(RoundedBorder.create(12, UITheme.BG_LIGHT));

        JLabel title = new JLabel("Order Status");
        title.setFont(UITheme.FONT_SUBTITLE);
        title.setForeground(UITheme.TEXT_DARK);
        title.setBorder(new EmptyBorder(16, 16, 16, 16));

        JPanel chartPanel = new JPanel(new GridLayout(1, 1)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawPieChart(g);
            }
        };
        chartPanel.setBackground(UITheme.BG_CARD);

        JPanel legend = new JPanel(new GridLayout(0, 1, 4, 4));
        legend.setBackground(UITheme.BG_CARD);
        legend.setBorder(new EmptyBorder(8, 8, 8, 8));

        try {
            int completed = orderDAO.getOrderCountByStatus("completed");
            int pending = orderDAO.getOrderCountByStatus("pending");
            int cancelled = orderDAO.getOrderCountByStatus("cancelled");
            int total = completed + pending + cancelled;

            if (total > 0) {
                legend.add(createLegendItem("Completed", completed, UITheme.PRIMARY));
                legend.add(createLegendItem("Pending", pending, UITheme.WARNING));
                legend.add(createLegendItem("Cancelled", cancelled, UITheme.DANGER));
            }
        } catch (Exception e) { }

        panel.add(title, BorderLayout.NORTH);
        panel.add(chartPanel, BorderLayout.CENTER);
        panel.add(legend, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createLegendItem(String label, int count, Color color) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setBackground(UITheme.BG_CARD);

        JPanel colorBox = new JPanel();
        colorBox.setBackground(color);
        colorBox.setPreferredSize(new Dimension(16, 16));

        JLabel text = new JLabel(label + ": " + count);
        text.setFont(UITheme.FONT_BODY);
        text.setForeground(UITheme.TEXT_DARK);

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
                g2.setColor(UITheme.TEXT_LIGHT);
                g2.drawString("No orders yet", 130, 150);
                return;
            }

            int size = Math.min(getWidth(), getHeight()) - 40;
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;

            double[] values = {completed, pending, cancelled};
            Color[] colors = {UITheme.PRIMARY, UITheme.WARNING, UITheme.DANGER};
            double startAngle = 0;

            for (int i = 0; i < values.length; i++) {
                if (values[i] > 0) {
                    double angle = (values[i] / total) * 360;
                    g2.setColor(colors[i]);
                    g2.fill(new Arc2D.Double(x, y, size, size, startAngle, angle, Arc2D.PIE));
                    startAngle += angle;
                }
            }

            g2.setColor(UITheme.BG_CARD);
            g2.fill(new Ellipse2D.Double(x + size * 0.3, y + size * 0.3, size * 0.4, size * 0.4));

            g2.setColor(UITheme.TEXT_DARK);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
            String totalText = String.valueOf(total);
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(totalText);
            g2.drawString(totalText, x + size / 2 - textWidth / 2, y + size / 2 + 6);

        } catch (Exception e) {
            g2.setColor(UITheme.TEXT_LIGHT);
            g2.drawString("Error loading data", 130, 150);
        }
    }
}
