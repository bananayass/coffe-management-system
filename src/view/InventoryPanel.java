package view;

import dao.ProductDAO;
import dao.InventoryAlertDAO;
import dao.SettingsDAO;
import model.Product;
import model.InventoryAlert;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class InventoryPanel extends JPanel {
    private ProductDAO productDAO = new ProductDAO();
    private InventoryAlertDAO alertDAO = new InventoryAlertDAO();
    private SettingsDAO settingsDAO = new SettingsDAO();
    private JTable table;
    private DefaultTableModel tableModel;
    private int lowStockThreshold;

    public InventoryPanel() {
        try {
            lowStockThreshold = settingsDAO.getLowStockThreshold();
        } catch (Exception e) {
            lowStockThreshold = 10;
        }
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_COLOR);

        add(createHeader(), BorderLayout.NORTH);
        add(createContent(), BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG_COLOR);
        panel.setBorder(new EmptyBorder(20, 24, 20, 24));

        JLabel title = new JLabel("Inventory Management");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_DARK);

        JButton btnAlerts = new JButton("View Alerts");
        btnAlerts.setFont(UITheme.FONT_BUTTON);
        btnAlerts.setBackground(UITheme.DANGER);
        btnAlerts.setForeground(Color.WHITE);
        btnAlerts.setFocusPainted(false);
        btnAlerts.addActionListener(e -> showAlerts());

        panel.add(title, BorderLayout.WEST);
        panel.add(btnAlerts, BorderLayout.EAST);

        return panel;
    }

    private JPanel createContent() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG_COLOR);
        panel.setBorder(new EmptyBorder(0, 24, 20, 24));

        // Top stats
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 16, 16));
        statsPanel.setBackground(UITheme.BG_COLOR);
        statsPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        statsPanel.add(createStatCard("Total Products", getTotalProducts(), UITheme.PRIMARY));
        statsPanel.add(createStatCard("Low Stock", getLowStockCount(), UITheme.WARNING));
        statsPanel.add(createStatCard("Out of Stock", getOutOfStockCount(), UITheme.DANGER));
        statsPanel.add(createStatCard("Total Value", getTotalValue(), UITheme.SUCCESS));

        // Table
        String[] columns = {"ID", "Name", "Category", "Stock", "Cost Price", "Selling Price", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(tableModel);
        table.setFont(UITheme.FONT_BODY);
        table.setRowHeight(40);
        table.setBackground(UITheme.BG_CARD);
        table.setForeground(UITheme.TEXT_DARK);
        table.getTableHeader().setBackground(UITheme.PRIMARY);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(UITheme.FONT_SUBTITLE);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(RoundedBorder.create(12, UITheme.BG_CARD));
        scroll.getViewport().setBackground(UITheme.BG_CARD);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(UITheme.BG_COLOR);
        btnPanel.setBorder(new EmptyBorder(16, 0, 0, 0));

        JButton btnAdd = createButton("Add Stock", UITheme.SUCCESS);
        JButton btnAdjust = createButton("Adjust Stock", UITheme.PRIMARY);
        JButton btnRefresh = createButton("Refresh", UITheme.INFO);

        btnAdd.addActionListener(e -> adjustStock(true));
        btnAdjust.addActionListener(e -> adjustStock(false));
        btnRefresh.addActionListener(e -> loadProducts());

        btnPanel.add(btnRefresh);
        btnPanel.add(btnAdd);
        btnPanel.add(btnAdjust);

        panel.add(statsPanel, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);

        loadProducts();
        return panel;
    }

    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(UITheme.BG_CARD);
        card.setBorder(RoundedBorder.create(12, UITheme.BG_CARD));
        card.setPreferredSize(new Dimension(180, 80));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(UITheme.FONT_BODY);
        lblTitle.setForeground(UITheme.TEXT_MEDIUM);
        lblTitle.setBorder(new EmptyBorder(12, 12, 4, 12));

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblValue.setForeground(color);
        lblValue.setBorder(new EmptyBorder(0, 12, 12, 12));

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);

        return card;
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(UITheme.FONT_BUTTON);
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void loadProducts() {
        tableModel.setRowCount(0);
        try {
            List<Product> products = productDAO.getAll();
            for (Product p : products) {
                String status;
                Color statusColor;
                if (p.getStockQuantity() <= 0) {
                    status = "OUT OF STOCK";
                    statusColor = UITheme.DANGER;
                } else if (p.getStockQuantity() <= lowStockThreshold) {
                    status = "LOW STOCK";
                    statusColor = UITheme.WARNING;
                } else {
                    status = "OK";
                    statusColor = UITheme.SUCCESS;
                }

                tableModel.addRow(new Object[]{
                    p.getId(), p.getName(), p.getCategory(), p.getStockQuantity(),
                    String.format("%,.0f VND", p.getCostPrice()),
                    String.format("%,.0f VND", p.getPrice()),
                    status
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private String getTotalProducts() {
        try {
            return String.valueOf(productDAO.getAll().size());
        } catch (Exception e) { return "0"; }
    }

    private String getLowStockCount() {
        try {
            return String.valueOf(productDAO.getAll().stream()
                .filter(p -> p.getStockQuantity() > 0 && p.getStockQuantity() <= lowStockThreshold)
                .count());
        } catch (Exception e) { return "0"; }
    }

    private String getOutOfStockCount() {
        try {
            return String.valueOf(productDAO.getAll().stream()
                .filter(p -> p.getStockQuantity() <= 0)
                .count());
        } catch (Exception e) { return "0"; }
    }

    private String getTotalValue() {
        try {
            double total = productDAO.getAll().stream()
                .mapToDouble(p -> p.getStockQuantity() * p.getCostPrice())
                .sum();
            return String.format("%,.0f VND", total);
        } catch (Exception e) { return "0 VND"; }
    }

    private void adjustStock(boolean add) {
        int selected = table.getSelectedRow();
        if (selected < 0) {
            JOptionPane.showMessageDialog(this, "Select a product");
            return;
        }

        int productId = (int) tableModel.getValueAt(selected, 0);
        try {
            Product p = productDAO.getById(productId);
            if (p == null) return;

            JSpinner spnQty = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
            JTextField txtReason = new JTextField();

            JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
            panel.add(new JLabel("Product:")); panel.add(new JLabel(p.getName()));
            panel.add(new JLabel("Quantity:")); panel.add(spnQty);
            panel.add(new JLabel("Reason:")); panel.add(txtReason);

            int result = JOptionPane.showConfirmDialog(this, panel,
                add ? "Add Stock" : "Adjust Stock",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                int qty = (int) spnQty.getValue();
                int newQty = add ? p.getStockQuantity() + qty : qty;

                productDAO.updateStock(productId, newQty);

                // Check for low stock alert
                alertDAO.checkAndCreateAlert(productId, newQty, lowStockThreshold);

                loadProducts();
                JOptionPane.showMessageDialog(this, "Stock updated!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void showAlerts() {
        try {
            List<InventoryAlert> alerts = alertDAO.getUnresolved();
            if (alerts.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No alerts!");
                return;
            }

            StringBuilder sb = new StringBuilder("Low Stock Alerts:\n\n");
            for (InventoryAlert a : alerts) {
                Product p = productDAO.getById(a.getProductId());
                if (p != null) {
                    sb.append("- ").append(p.getName()).append(": ").append(p.getStockQuantity()).append(" remaining\n");
                }
            }

            JOptionPane.showMessageDialog(this, sb.toString());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}
