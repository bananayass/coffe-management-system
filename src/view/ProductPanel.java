package view;

import dao.ProductDAO;
import model.Product;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ProductPanel extends JPanel {
    private ProductDAO productDAO = new ProductDAO();
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtName, txtCategory, txtPrice, txtStock;
    private JButton btnAdd, btnUpdate, btnDelete;
    private int selectedId = -1;

    public ProductPanel() {
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_COLOR);

        // Left form + Right table
        JPanel leftPanel = createFormPanel();
        JPanel rightPanel = createTablePanel();

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        split.setDividerLocation(340);
        split.setResizeWeight(0);
        split.setBackground(UITheme.BG_COLOR);
        add(split, BorderLayout.CENTER);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(UITheme.BG_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER),
            new EmptyBorder(0, 0, 0, 0)
        ));
        panel.setPreferredSize(new Dimension(320, 0));

        JLabel title = new JLabel("Product Details");
        title.setFont(UITheme.FONT_SUBTITLE);
        title.setForeground(UITheme.TEXT_DARK);
        title.setBorder(new EmptyBorder(20, 20, 20, 20));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel form = new JPanel(new GridLayout(5, 2, 12, 12));
        form.setBorder(new EmptyBorder(0, 20, 20, 20));
        form.setBackground(UITheme.BG_CARD);

        txtName = createTextField();
        txtCategory = createTextField();
        txtPrice = createTextField();
        txtStock = createTextField();

        form.add(createLabel("Name:"));
        form.add(txtName);
        form.add(createLabel("Category:"));
        form.add(txtCategory);
        form.add(createLabel("Price:"));
        form.add(txtPrice);
        form.add(createLabel("Stock:"));
        form.add(txtStock);

        // Buttons
        JPanel buttons = new JPanel(new GridLayout(1, 3, 12, 0));
        buttons.setBorder(new EmptyBorder(0, 20, 20, 20));
        buttons.setBackground(UITheme.BG_CARD);

        btnAdd = createButton("Add", UITheme.SUCCESS);
        btnUpdate = createButton("Update", UITheme.PRIMARY);
        btnDelete = createButton("Delete", UITheme.DANGER);

        btnAdd.addActionListener(e -> addProduct());
        btnUpdate.addActionListener(e -> updateProduct());
        btnDelete.addActionListener(e -> deleteProduct());

        buttons.add(btnAdd);
        buttons.add(btnUpdate);
        buttons.add(btnDelete);

        panel.add(title);
        panel.add(form);
        panel.add(buttons);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER),
            new EmptyBorder(0, 0, 0, 0)
        ));

        JLabel title = new JLabel("Products");
        title.setFont(UITheme.FONT_SUBTITLE);
        title.setForeground(UITheme.TEXT_DARK);
        title.setBorder(new EmptyBorder(20, 20, 20, 20));

        String[] cols = {"ID", "Name", "Category", "Price", "Stock"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        table = new JTable(tableModel);
        table.setFont(UITheme.FONT_BODY);
        table.setBackground(UITheme.BG_CARD);
        table.setForeground(UITheme.TEXT_DARK);
        table.setRowHeight(36);
        table.setGridColor(UITheme.BORDER);
        table.setSelectionBackground(UITheme.PRIMARY_LIGHT);
        table.getTableHeader().setBackground(UITheme.BG_MEDIUM);
        table.getTableHeader().setForeground(UITheme.TEXT_DARK);
        table.getTableHeader().setFont(UITheme.FONT_BODY);

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                selectedId = (int) tableModel.getValueAt(row, 0);
                txtName.setText(tableModel.getValueAt(row, 1).toString());
                txtCategory.setText(tableModel.getValueAt(row, 2).toString());
                txtPrice.setText(tableModel.getValueAt(row, 3).toString());
                txtStock.setText(tableModel.getValueAt(row, 4).toString());
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(UITheme.BG_CARD);

        // Search
        JPanel searchPanel = new JPanel(new BorderLayout(12, 0));
        searchPanel.setBorder(new EmptyBorder(0, 20, 20, 20));
        searchPanel.setBackground(UITheme.BG_CARD);

        JTextField txtSearch = createTextField();
        txtSearch.setToolTipText("Search products...");
        JButton btnSearch = createButton("Search", UITheme.PRIMARY);

        btnSearch.addActionListener(e -> searchProducts(txtSearch.getText()));

        searchPanel.add(txtSearch, BorderLayout.CENTER);
        searchPanel.add(btnSearch, BorderLayout.EAST);

        panel.add(title, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(searchPanel, BorderLayout.SOUTH);

        loadProducts();
        return panel;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UITheme.FONT_BODY);
        label.setForeground(UITheme.TEXT_MEDIUM);
        return label;
    }

    private JTextField createTextField() {
        JTextField tf = new JTextField();
        tf.setFont(UITheme.FONT_BODY);
        tf.setBackground(UITheme.BG_LIGHT);
        tf.setForeground(UITheme.TEXT_DARK);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER, 1),
            new EmptyBorder(10, 12, 10, 12)
        ));
        return tf;
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(UITheme.FONT_BUTTON);
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bg, 1),
            new EmptyBorder(10, 20, 10, 20)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void loadProducts() {
        tableModel.setRowCount(0);
        try {
            List<Product> products = productDAO.getAll();
            for (Product p : products) {
                tableModel.addRow(new Object[]{
                    p.getId(), p.getName(), p.getCategory(), p.getPrice(), p.getStockQuantity()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void searchProducts(String keyword) {
        tableModel.setRowCount(0);
        try {
            List<Product> products = keyword.isEmpty() ?
                productDAO.getAll() : productDAO.searchByName(keyword);
            for (Product p : products) {
                tableModel.addRow(new Object[]{
                    p.getId(), p.getName(), p.getCategory(), p.getPrice(), p.getStockQuantity()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void addProduct() {
        try {
            if (txtName.getText().isEmpty() || txtCategory.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Fill all fields!");
                return;
            }
            Product p = new Product();
            p.setName(txtName.getText());
            p.setCategory(txtCategory.getText());
            p.setPrice(Double.parseDouble(txtPrice.getText()));
            p.setStockQuantity(Integer.parseInt(txtStock.getText()));

            if (productDAO.insert(p)) {
                JOptionPane.showMessageDialog(this, "Product added!");
                clearFields();
                loadProducts();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void updateProduct() {
        if (selectedId < 0) {
            JOptionPane.showMessageDialog(this, "Select a product!");
            return;
        }
        try {
            Product p = new Product();
            p.setId(selectedId);
            p.setName(txtName.getText());
            p.setCategory(txtCategory.getText());
            p.setPrice(Double.parseDouble(txtPrice.getText()));
            p.setStockQuantity(Integer.parseInt(txtStock.getText()));

            if (productDAO.update(p)) {
                JOptionPane.showMessageDialog(this, "Product updated!");
                clearFields();
                loadProducts();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void deleteProduct() {
        if (selectedId < 0) {
            JOptionPane.showMessageDialog(this, "Select a product!");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (productDAO.delete(selectedId)) {
                    JOptionPane.showMessageDialog(this, "Deleted!");
                    clearFields();
                    loadProducts();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }

    private void clearFields() {
        txtName.setText("");
        txtCategory.setText("");
        txtPrice.setText("");
        txtStock.setText("");
        selectedId = -1;
        table.clearSelection();
    }
}
