package view;

import dao.DiscountDAO;
import model.Discount;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.util.List;

public class DiscountPanel extends JPanel {
    private DiscountDAO discountDAO = new DiscountDAO();
    private JTable table;
    private DefaultTableModel tableModel;

    public DiscountPanel() {
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

        JLabel title = new JLabel("Discount & Promotion Management");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_DARK);

        panel.add(title, BorderLayout.WEST);
        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG_COLOR);
        panel.setBorder(new EmptyBorder(0, 24, 0, 24));

        String[] columns = {"ID", "Code", "Description", "Type", "Value", "Min Order", "Max Uses", "Used", "Start", "End", "Status"};
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
        loadDiscounts();

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBackground(UITheme.BG_COLOR);
        panel.setBorder(new EmptyBorder(16, 24, 20, 24));

        JButton btnAdd = createButton("Add Discount", UITheme.SUCCESS);
        JButton btnEdit = createButton("Edit", UITheme.PRIMARY);
        JButton btnToggle = createButton("Toggle Active", UITheme.WARNING);
        JButton btnDelete = createButton("Delete", UITheme.DANGER);
        JButton btnRefresh = createButton("Refresh", UITheme.INFO);

        btnAdd.addActionListener(e -> showAddDialog());
        btnEdit.addActionListener(e -> showEditDialog());
        btnToggle.addActionListener(e -> toggleActive());
        btnDelete.addActionListener(e -> deleteDiscount());
        btnRefresh.addActionListener(e -> loadDiscounts());

        panel.add(btnRefresh);
        panel.add(btnAdd);
        panel.add(btnEdit);
        panel.add(btnToggle);
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

    private void loadDiscounts() {
        tableModel.setRowCount(0);
        try {
            List<Discount> discounts = discountDAO.getAll();
            for (Discount d : discounts) {
                tableModel.addRow(new Object[]{
                    d.getId(), d.getCode(), d.getDescription(), d.getDiscountType(),
                    d.getDisplayValue(), String.format("%,.0f VND", d.getMinOrderAmount()),
                    d.getMaxUses() > 0 ? d.getMaxUses() : "Unlimited",
                    d.getUsedCount(),
                    d.getStartDate(), d.getEndDate(),
                    d.isActive() ? "Active" : "Inactive"
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void showAddDialog() {
        JTextField txtCode = new JTextField();
        JTextField txtDescription = new JTextField();
        JComboBox<String> cmbType = new JComboBox<>(new String[]{"percentage", "fixed"});
        JSpinner spnValue = new JSpinner(new SpinnerNumberModel(10, 1, 100, 1));
        JSpinner spnMinOrder = new JSpinner(new SpinnerNumberModel(0, 0, 1000000, 10000));
        JSpinner spnMaxUses = new JSpinner(new SpinnerNumberModel(100, 1, 10000, 1));

        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.add(new JLabel("Code:")); panel.add(txtCode);
        panel.add(new JLabel("Description:")); panel.add(txtDescription);
        panel.add(new JLabel("Type:")); panel.add(cmbType);
        panel.add(new JLabel("Value (%/VND):")); panel.add(spnValue);
        panel.add(new JLabel("Min Order:")); panel.add(spnMinOrder);
        panel.add(new JLabel("Max Uses:")); panel.add(spnMaxUses);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Discount",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                Discount d = new Discount();
                d.setCode(txtCode.getText().toUpperCase());
                d.setDescription(txtDescription.getText());
                d.setDiscountType((String) cmbType.getSelectedItem());
                d.setDiscountValue((int) spnValue.getValue());
                d.setMinOrderAmount((int) spnMinOrder.getValue());
                d.setMaxUses((int) spnMaxUses.getValue());
                d.setStartDate(new Date(System.currentTimeMillis()));
                d.setEndDate(null);
                d.setActive(true);

                discountDAO.create(d);
                loadDiscounts();
                JOptionPane.showMessageDialog(this, "Discount added!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }

    private void showEditDialog() {
        int selected = table.getSelectedRow();
        if (selected < 0) {
            JOptionPane.showMessageDialog(this, "Select a discount to edit");
            return;
        }

        int id = (int) tableModel.getValueAt(selected, 0);
        try {
            Discount d = discountDAO.getById(id);
            if (d == null) return;

            JTextField txtCode = new JTextField(d.getCode());
            JTextField txtDescription = new JTextField(d.getDescription());
            JComboBox<String> cmbType = new JComboBox<>(new String[]{"percentage", "fixed"});
            cmbType.setSelectedItem(d.getDiscountType());
            JSpinner spnValue = new JSpinner(new SpinnerNumberModel((int)d.getDiscountValue(), 1, 100, 1));
            JSpinner spnMinOrder = new JSpinner(new SpinnerNumberModel((int)d.getMinOrderAmount(), 0, 1000000, 10000));
            JSpinner spnMaxUses = new JSpinner(new SpinnerNumberModel(d.getMaxUses() > 0 ? d.getMaxUses() : 100, 1, 10000, 1));

            JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
            panel.add(new JLabel("Code:")); panel.add(txtCode);
            panel.add(new JLabel("Description:")); panel.add(txtDescription);
            panel.add(new JLabel("Type:")); panel.add(cmbType);
            panel.add(new JLabel("Value:")); panel.add(spnValue);
            panel.add(new JLabel("Min Order:")); panel.add(spnMinOrder);
            panel.add(new JLabel("Max Uses:")); panel.add(spnMaxUses);

            int result = JOptionPane.showConfirmDialog(this, panel, "Edit Discount",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                d.setCode(txtCode.getText().toUpperCase());
                d.setDescription(txtDescription.getText());
                d.setDiscountType((String) cmbType.getSelectedItem());
                d.setDiscountValue((int) spnValue.getValue());
                d.setMinOrderAmount((int) spnMinOrder.getValue());
                d.setMaxUses((int) spnMaxUses.getValue());

                discountDAO.update(d);
                loadDiscounts();
                JOptionPane.showMessageDialog(this, "Discount updated!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void toggleActive() {
        int selected = table.getSelectedRow();
        if (selected < 0) {
            JOptionPane.showMessageDialog(this, "Select a discount");
            return;
        }

        int id = (int) tableModel.getValueAt(selected, 0);
        try {
            discountDAO.toggleActive(id);
            loadDiscounts();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void deleteDiscount() {
        int selected = table.getSelectedRow();
        if (selected < 0) {
            JOptionPane.showMessageDialog(this, "Select a discount to delete");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete this discount?", "Confirm", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int id = (int) tableModel.getValueAt(selected, 0);
            try {
                discountDAO.delete(id);
                loadDiscounts();
                JOptionPane.showMessageDialog(this, "Discount deleted!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
}
