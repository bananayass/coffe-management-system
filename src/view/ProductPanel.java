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
    private JButton btnAdd, btnUpdate, btnDelete, btnClear;
    private int selectedId = -1;

    // Colors
    private static final Color PRIMARY = new Color(99, 102, 241);
    private static final Color SUCCESS = new Color(34, 197, 94);
    private static final Color DANGER = new Color(239, 68, 68);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color BG_COLOR = new Color(243, 244, 246);
    private static final Color TEXT_DARK = new Color(17, 24, 39);
    private static final Color TEXT_GRAY = new Color(107, 114, 128);
    private static final Color BORDER = new Color(229, 231, 235);

    public ProductPanel() {
        setLayout(new BorderLayout());
        setBackground(BG_COLOR);

        // Left panel - Form
        JPanel leftPanel = createFormPanel();

        // Right panel - Table
        JPanel rightPanel = createTablePanel();

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        split.setDividerLocation(320);
        split.setResizeWeight(0);
        add(split, BorderLayout.CENTER);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createLineBorder(BORDER));
        panel.setPreferredSize(new Dimension(300, 0));

        JLabel title = new JLabel("Product Details");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setForeground(TEXT_DARK);
        title.setBorder(new EmptyBorder(16, 16, 16, 16));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel form = new JPanel(new GridLayout(5, 2, 8, 8));
        form.setBorder(new EmptyBorder(0, 16, 16, 16));
        form.setBackground(CARD_BG);

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
        JPanel buttons = new JPanel(new GridLayout(1, 3, 8, 0));
        buttons.setBorder(new EmptyBorder(0, 16, 16, 16));
        buttons.setBackground(CARD_BG);

        btnAdd = createButton("Add", SUCCESS);
        btnUpdate = createButton("Update", PRIMARY);
        btnDelete = createButton("Delete", DANGER);

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
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createLineBorder(BORDER));

        JLabel title = new JLabel("Products");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setForeground(TEXT_DARK);
        title.setBorder(new EmptyBorder(16, 16, 16, 16));

        String[] cols = {"ID", "Name", "Category", "Price", "Stock"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setRowHeight(32);
        table.setGridColor(BORDER);
        table.setSelectionBackground(PRIMARY.brighter());
        table.getTableHeader().setBackground(BG_COLOR);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

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
        scroll.getViewport().setBackground(CARD_BG);

        // Search bar
        JPanel searchPanel = new JPanel(new BorderLayout(8, 0));
        searchPanel.setBorder(new EmptyBorder(0, 16, 16, 16));
        searchPanel.setBackground(CARD_BG);

        JTextField txtSearch = createTextField();
        txtSearch.setToolTipText("Search by name...");
        JButton btnSearch = createButton("Search", PRIMARY);

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
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        label.setForeground(TEXT_GRAY);
        return label;
    }

    private JTextField createTextField() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Arial", Font.PLAIN, 12));
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            new EmptyBorder(8, 8, 8, 8)
        ));
        return tf;
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
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
            JOptionPane.showMessageDialog(this, "Error loading products: " + e.getMessage());
            e.printStackTrace();
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
            JOptionPane.showMessageDialog(this, "Error searching: " + e.getMessage());
        }
    }

    private void addProduct() {
        try {
            if (txtName.getText().isEmpty() || txtCategory.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields!");
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
            JOptionPane.showMessageDialog(this, "Select a product first!");
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
            JOptionPane.showMessageDialog(this, "Select a product first!");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this product?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (productDAO.delete(selectedId)) {
                    JOptionPane.showMessageDialog(this, "Product deleted!");
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
