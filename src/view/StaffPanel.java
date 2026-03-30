package view;

import dao.UserDAO;
import model.User;
import utils.PasswordHelper;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StaffPanel extends JPanel {
    private UserDAO userDAO = new UserDAO();
    private JTable table;
    private DefaultTableModel tableModel;

    public StaffPanel() {
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

        JLabel title = new JLabel("Staff Management");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_DARK);

        panel.add(title, BorderLayout.WEST);
        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG_COLOR);
        panel.setBorder(new EmptyBorder(0, 24, 0, 24));

        String[] columns = {"ID", "Username", "Full Name", "Role", "Phone", "Email", "Status", "Created"};
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
        loadStaff();

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBackground(UITheme.BG_COLOR);
        panel.setBorder(new EmptyBorder(16, 24, 20, 24));

        JButton btnAdd = createButton("Add Staff", UITheme.SUCCESS);
        JButton btnEdit = createButton("Edit", UITheme.PRIMARY);
        JButton btnToggle = createButton("Toggle Status", UITheme.WARNING);
        JButton btnDelete = createButton("Delete", UITheme.DANGER);
        JButton btnRefresh = createButton("Refresh", UITheme.INFO);

        btnAdd.addActionListener(e -> showAddDialog());
        btnEdit.addActionListener(e -> showEditDialog());
        btnToggle.addActionListener(e -> toggleStatus());
        btnDelete.addActionListener(e -> deleteStaff());
        btnRefresh.addActionListener(e -> loadStaff());

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

    private void loadStaff() {
        tableModel.setRowCount(0);
        try {
            List<User> users = userDAO.getAll();
            for (User u : users) {
                tableModel.addRow(new Object[]{
                    u.getId(), u.getUsername(), u.getFullName(), u.getRoleDisplay(),
                    u.getPhone(), u.getEmail(),
                    u.isActive() ? "Active" : "Inactive",
                    u.getCreatedAt() != null ? u.getCreatedAt().toString().substring(0, 10) : "-"
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void showAddDialog() {
        JTextField txtUsername = new JTextField();
        JPasswordField txtPassword = new JPasswordField();
        JTextField txtFullName = new JTextField();
        JComboBox<String> cmbRole = new JComboBox<>(new String[]{"admin", "manager", "cashier", "barista"});
        JTextField txtPhone = new JTextField();
        JTextField txtEmail = new JTextField();

        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.add(new JLabel("Username:")); panel.add(txtUsername);
        panel.add(new JLabel("Password:")); panel.add(txtPassword);
        panel.add(new JLabel("Full Name:")); panel.add(txtFullName);
        panel.add(new JLabel("Role:")); panel.add(cmbRole);
        panel.add(new JLabel("Phone:")); panel.add(txtPhone);
        panel.add(new JLabel("Email:")); panel.add(txtEmail);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Staff",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                User u = new User();
                u.setUsername(txtUsername.getText());
                u.setPassword(PasswordHelper.hashPassword(new String(txtPassword.getPassword())));
                u.setFullName(txtFullName.getText());
                u.setRole((String) cmbRole.getSelectedItem());
                u.setPhone(txtPhone.getText());
                u.setEmail(txtEmail.getText());
                u.setActive(true);

                userDAO.create(u);
                loadStaff();
                JOptionPane.showMessageDialog(this, "Staff added!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }

    private void showEditDialog() {
        int selected = table.getSelectedRow();
        if (selected < 0) {
            JOptionPane.showMessageDialog(this, "Select a staff member to edit");
            return;
        }

        int userId = (int) tableModel.getValueAt(selected, 0);
        try {
            User u = userDAO.getById(userId);
            if (u == null) return;

            JTextField txtUsername = new JTextField(u.getUsername());
            JPasswordField txtPassword = new JPasswordField(u.getPassword());
            JTextField txtFullName = new JTextField(u.getFullName() != null ? u.getFullName() : "");
            JComboBox<String> cmbRole = new JComboBox<>(new String[]{"admin", "manager", "cashier", "barista"});
            cmbRole.setSelectedItem(u.getRole());
            JTextField txtPhone = new JTextField(u.getPhone() != null ? u.getPhone() : "");
            JTextField txtEmail = new JTextField(u.getEmail() != null ? u.getEmail() : "");

            JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
            panel.add(new JLabel("Username:")); panel.add(txtUsername);
            panel.add(new JLabel("Password:")); panel.add(txtPassword);
            panel.add(new JLabel("Full Name:")); panel.add(txtFullName);
            panel.add(new JLabel("Role:")); panel.add(cmbRole);
            panel.add(new JLabel("Phone:")); panel.add(txtPhone);
            panel.add(new JLabel("Email:")); panel.add(txtEmail);

            int result = JOptionPane.showConfirmDialog(this, panel, "Edit Staff",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                u.setUsername(txtUsername.getText());
                u.setPassword(PasswordHelper.hashPassword(new String(txtPassword.getPassword())));
                u.setFullName(txtFullName.getText());
                u.setRole((String) cmbRole.getSelectedItem());
                u.setPhone(txtPhone.getText());
                u.setEmail(txtEmail.getText());

                userDAO.update(u);
                loadStaff();
                JOptionPane.showMessageDialog(this, "Staff updated!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void toggleStatus() {
        int selected = table.getSelectedRow();
        if (selected < 0) {
            JOptionPane.showMessageDialog(this, "Select a staff member");
            return;
        }

        int userId = (int) tableModel.getValueAt(selected, 0);
        try {
            userDAO.toggleActive(userId);
            loadStaff();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void deleteStaff() {
        int selected = table.getSelectedRow();
        if (selected < 0) {
            JOptionPane.showMessageDialog(this, "Select a staff member to delete");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete this staff member?", "Confirm", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int userId = (int) tableModel.getValueAt(selected, 0);
            try {
                userDAO.delete(userId);
                loadStaff();
                JOptionPane.showMessageDialog(this, "Staff deleted!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
}
