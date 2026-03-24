package view;

import dao.CustomerDAO;
import model.Customer;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CustomerPanel extends JPanel {
    private CustomerDAO customerDAO = new CustomerDAO();
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;

    public CustomerPanel() {
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_COLOR);

        add(createHeader(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG_COLOR);
        panel.setBorder(new EmptyBorder(20, 24, 20, 24));

        JLabel title = new JLabel("Customer Management");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_DARK);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(UITheme.BG_COLOR);

        txtSearch = new JTextField(20);
        txtSearch.setFont(UITheme.FONT_BODY);
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER),
            new EmptyBorder(8, 12, 8, 12)
        ));
        txtSearch.addActionListener(e -> searchCustomers());

        JButton btnSearch = createButton("Search", UITheme.PRIMARY);
        btnSearch.addActionListener(e -> searchCustomers());

        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);

        panel.add(title, BorderLayout.WEST);
        panel.add(searchPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG_COLOR);
        panel.setBorder(new EmptyBorder(0, 24, 0, 24));

        String[] columns = {"ID", "Name", "Phone", "Email", "Points", "Total Spent", "Tier"};
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
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(UITheme.BG_CARD);

        panel.add(scroll, BorderLayout.CENTER);
        loadCustomers();

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBackground(UITheme.BG_COLOR);
        panel.setBorder(new EmptyBorder(16, 24, 20, 24));

        JButton btnAdd = createButton("Add Customer", UITheme.SUCCESS);
        JButton btnEdit = createButton("Edit", UITheme.PRIMARY);
        JButton btnDelete = createButton("Delete", UITheme.DANGER);
        JButton btnRefresh = createButton("Refresh", UITheme.INFO);

        btnAdd.addActionListener(e -> showAddDialog());
        btnEdit.addActionListener(e -> showEditDialog());
        btnDelete.addActionListener(e -> deleteCustomer());
        btnRefresh.addActionListener(e -> loadCustomers());

        panel.add(btnRefresh);
        panel.add(btnAdd);
        panel.add(btnEdit);
        panel.add(btnDelete);

        return panel;
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

    private void loadCustomers() {
        tableModel.setRowCount(0);
        try {
            List<Customer> customers = customerDAO.getAll();
            for (Customer c : customers) {
                tableModel.addRow(new Object[]{
                    c.getId(),
                    c.getName(),
                    c.getPhone(),
                    c.getEmail(),
                    c.getLoyaltyPoints(),
                    String.format("%,.0f VND", c.getTotalSpent()),
                    c.getLoyaltyTier()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading customers: " + e.getMessage());
        }
    }

    private void searchCustomers() {
        String keyword = txtSearch.getText().trim();
        tableModel.setRowCount(0);
        try {
            List<Customer> customers = keyword.isEmpty() ?
                customerDAO.getAll() : customerDAO.search(keyword);
            for (Customer c : customers) {
                tableModel.addRow(new Object[]{
                    c.getId(), c.getName(), c.getPhone(), c.getEmail(),
                    c.getLoyaltyPoints(), String.format("%,.0f VND", c.getTotalSpent()),
                    c.getLoyaltyTier()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void showAddDialog() {
        JTextField txtName = new JTextField();
        JTextField txtPhone = new JTextField();
        JTextField txtEmail = new JTextField();

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.add(new JLabel("Name:")); panel.add(txtName);
        panel.add(new JLabel("Phone:")); panel.add(txtPhone);
        panel.add(new JLabel("Email:")); panel.add(txtEmail);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Customer",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                Customer c = new Customer(txtName.getText(), txtPhone.getText(), txtEmail.getText());
                customerDAO.create(c);
                loadCustomers();
                JOptionPane.showMessageDialog(this, "Customer added successfully!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }

    private void showEditDialog() {
        int selected = table.getSelectedRow();
        if (selected < 0) {
            JOptionPane.showMessageDialog(this, "Select a customer to edit");
            return;
        }

        int customerId = (int) tableModel.getValueAt(selected, 0);
        try {
            Customer c = customerDAO.getById(customerId);
            if (c == null) return;

            JTextField txtName = new JTextField(c.getName());
            JTextField txtPhone = new JTextField(c.getPhone());
            JTextField txtEmail = new JTextField(c.getEmail());

            JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
            panel.add(new JLabel("Name:")); panel.add(txtName);
            panel.add(new JLabel("Phone:")); panel.add(txtPhone);
            panel.add(new JLabel("Email:")); panel.add(txtEmail);

            int result = JOptionPane.showConfirmDialog(this, panel, "Edit Customer",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                c.setName(txtName.getText());
                c.setPhone(txtPhone.getText());
                c.setEmail(txtEmail.getText());
                customerDAO.update(c);
                loadCustomers();
                JOptionPane.showMessageDialog(this, "Customer updated!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void deleteCustomer() {
        int selected = table.getSelectedRow();
        if (selected < 0) {
            JOptionPane.showMessageDialog(this, "Select a customer to delete");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this customer?",
            "Confirm", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int customerId = (int) tableModel.getValueAt(selected, 0);
            try {
                customerDAO.delete(customerId);
                loadCustomers();
                JOptionPane.showMessageDialog(this, "Customer deleted!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
}
