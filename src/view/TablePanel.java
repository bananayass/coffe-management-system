package view;

import dao.TableDAO;
import model.Table;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class TablePanel extends JPanel {
    private TableDAO tableDAO = new TableDAO();
    private JPanel tableGridPanel;

    public TablePanel() {
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_COLOR);

        add(createHeader(), BorderLayout.NORTH);
        add(createTableGrid(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG_COLOR);
        panel.setBorder(new EmptyBorder(20, 24, 20, 24));

        JLabel title = new JLabel("Table Management");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_DARK);

        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        statsPanel.setBackground(UITheme.BG_COLOR);

        JLabel stats = new JLabel();
        stats.setFont(UITheme.FONT_BODY);
        stats.setForeground(UITheme.TEXT_MEDIUM);
        updateStats(stats);

        statsPanel.add(stats);

        panel.add(title, BorderLayout.WEST);
        panel.add(statsPanel, BorderLayout.EAST);

        return panel;
    }

    private void updateStats(JLabel label) {
        try {
            List<Table> all = tableDAO.getAll();
            long available = all.stream().filter(Table::isAvailable).count();
            long occupied = all.stream().filter(Table::isOccupied).count();
            long reserved = all.stream().filter(Table::isReserved).count();
            label.setText(String.format("Available: %d | Occupied: %d | Reserved: %d",
                available, occupied, reserved));
        } catch (Exception e) {
            label.setText("");
        }
    }

    private JPanel createTableGrid() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(UITheme.BG_COLOR);
        panel.setBorder(new EmptyBorder(20, 24, 20, 24));

        tableGridPanel = new JPanel(new GridLayout(0, 4, 16, 16));
        tableGridPanel.setBackground(UITheme.BG_COLOR);

        panel.add(tableGridPanel);
        loadTables();

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBackground(UITheme.BG_COLOR);
        panel.setBorder(new EmptyBorder(16, 24, 20, 24));

        JButton btnAdd = createButton("Add Table", UITheme.SUCCESS);
        JButton btnRelease = createButton("Release Table", UITheme.INFO);
        JButton btnRefresh = createButton("Refresh", UITheme.PRIMARY);

        btnAdd.addActionListener(e -> showAddDialog());
        btnRelease.addActionListener(e -> releaseSelectedTable());
        btnRefresh.addActionListener(e -> loadTables());

        panel.add(btnRefresh);
        panel.add(btnRelease);
        panel.add(btnAdd);

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

    private void loadTables() {
        tableGridPanel.removeAll();
        try {
            List<Table> tables = tableDAO.getAll();
            for (Table t : tables) {
                tableGridPanel.add(createTableCard(t));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
        tableGridPanel.revalidate();
        tableGridPanel.repaint();
    }

    private JPanel createTableCard(Table t) {
        JPanel card = new JPanel(new BorderLayout());
        card.setPreferredSize(new Dimension(180, 120));
        card.setBorder(RoundedBorder.create(12, getStatusColor(t.getStatus())));

        Color bgColor = getStatusBgColor(t.getStatus());
        card.setBackground(bgColor);

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBackground(bgColor);
        info.setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel number = new JLabel(t.getTableNumber());
        number.setFont(new Font("Segoe UI", Font.BOLD, 20));
        number.setForeground(UITheme.TEXT_DARK);
        number.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel capacity = new JLabel(t.getCapacity() + " seats");
        capacity.setFont(UITheme.FONT_SMALL);
        capacity.setForeground(UITheme.TEXT_MEDIUM);
        capacity.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel status = new JLabel(t.getStatus().toUpperCase());
        status.setFont(new Font("Segoe UI", Font.BOLD, 12));
        status.setForeground(getStatusColor(t.getStatus()));
        status.setAlignmentX(Component.CENTER_ALIGNMENT);

        info.add(number);
        info.add(Box.createVerticalStrut(4));
        info.add(capacity);
        info.add(Box.createVerticalStrut(8));
        info.add(status);

        card.add(info, BorderLayout.CENTER);

        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                showTableOptions(t);
            }
        });

        return card;
    }

    private Color getStatusColor(String status) {
        switch (status) {
            case "available": return UITheme.SUCCESS;
            case "occupied": return UITheme.DANGER;
            case "reserved": return UITheme.WARNING;
            default: return UITheme.TEXT_MEDIUM;
        }
    }

    private Color getStatusBgColor(String status) {
        switch (status) {
            case "available": return new Color(16, 185, 129, 30);
            case "occupied": return new Color(239, 68, 68, 30);
            case "reserved": return new Color(245, 158, 11, 30);
            default: return UITheme.BG_MEDIUM;
        }
    }

    private void showTableOptions(Table t) {
        String[] options = t.isAvailable() ? new String[]{"Occupy", "Reserve", "Edit", "Delete"} :
                          t.isOccupied() ? new String[]{"Release", "Edit", "Delete"} :
                          new String[]{"Release", "Occupy", "Edit", "Delete"};

        int choice = JOptionPane.showOptionDialog(this,
            "Table " + t.getTableNumber() + " (" + t.getStatus() + ")",
            "Table Options", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
            null, options, options[0]);

        if (choice == 0) {
            if (t.isAvailable()) {
                occupyTable(t);
            } else {
                releaseTable(t);
            }
        } else if (choice == 1 && !t.isAvailable()) {
            occupyTable(t);
        } else if (choice == 2) {
            showEditDialog(t);
        } else if ((choice == 3) || (choice == 2 && t.isAvailable())) {
            deleteTable(t);
        }
    }

    private void occupyTable(Table t) {
        try {
            tableDAO.occupy(t.getId());
            loadTables();
            JOptionPane.showMessageDialog(this, "Table occupied!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void releaseTable(Table t) {
        try {
            tableDAO.release(t.getId());
            loadTables();
            JOptionPane.showMessageDialog(this, "Table released!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void releaseSelectedTable() {
        JOptionPane.showMessageDialog(this, "Click on a table to release it");
    }

    private void showAddDialog() {
        JTextField txtNumber = new JTextField();
        JSpinner spnCapacity = new JSpinner(new SpinnerNumberModel(4, 1, 20, 1));

        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.add(new JLabel("Table Number:")); panel.add(txtNumber);
        panel.add(new JLabel("Capacity:")); panel.add(spnCapacity);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Table",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                Table t = new Table(txtNumber.getText(), (int) spnCapacity.getValue());
                tableDAO.create(t);
                loadTables();
                JOptionPane.showMessageDialog(this, "Table added!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }

    private void showEditDialog(Table t) {
        JTextField txtNumber = new JTextField(t.getTableNumber());
        JSpinner spnCapacity = new JSpinner(new SpinnerNumberModel(t.getCapacity(), 1, 20, 1));

        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.add(new JLabel("Table Number:")); panel.add(txtNumber);
        panel.add(new JLabel("Capacity:")); panel.add(spnCapacity);

        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Table",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                t.setTableNumber(txtNumber.getText());
                t.setCapacity((int) spnCapacity.getValue());
                tableDAO.update(t);
                loadTables();
                JOptionPane.showMessageDialog(this, "Table updated!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }

    private void deleteTable(Table t) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete table " + t.getTableNumber() + "?", "Confirm",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                tableDAO.delete(t.getId());
                loadTables();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
}
