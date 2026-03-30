package view;

import dao.SettingsDAO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SettingsPanel extends JPanel {
    private SettingsDAO settingsDAO = new SettingsDAO();

    private JTextField txtShopName, txtShopAddress, txtShopPhone;

    public SettingsPanel() {
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_COLOR);

        add(createHeader(), BorderLayout.NORTH);
        add(createContent(), BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG_COLOR);
        panel.setBorder(new EmptyBorder(20, 24, 20, 24));

        JLabel title = new JLabel("Settings");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_DARK);

        panel.add(title, BorderLayout.WEST);
        return panel;
    }

    private JPanel createContent() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG_COLOR);
        panel.setBorder(new EmptyBorder(0, 24, 20, 24));

        JPanel formPanel = new JPanel(new GridLayout(0, 1, 16, 16));
        formPanel.setBackground(UITheme.BG_CARD);
        formPanel.setBorder(RoundedBorder.create(16, UITheme.BG_CARD));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            RoundedBorder.create(16, UITheme.BG_CARD),
            new EmptyBorder(24, 24, 24, 24)
        ));

        // Shop Info
        JPanel shopInfo = createSection("Shop Information");
        String shopName = "Coffee Shop", shopAddr = "", shopPhone = "";
        try {
            shopName = settingsDAO.getShopName() != null ? settingsDAO.getShopName() : "Coffee Shop";
            shopAddr = settingsDAO.getShopAddress() != null ? settingsDAO.getShopAddress() : "";
            shopPhone = settingsDAO.getShopPhone() != null ? settingsDAO.getShopPhone() : "";
        } catch (Exception e) { e.printStackTrace(); }

        txtShopName = new JTextField(shopName);
        txtShopAddress = new JTextField(shopAddr);
        txtShopPhone = new JTextField(shopPhone);

        shopInfo.add(createFormRow("Shop Name:", txtShopName));
        shopInfo.add(createFormRow("Address:", txtShopAddress));
        shopInfo.add(createFormRow("Phone:", txtShopPhone));

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(UITheme.BG_CARD);

        JButton btnSave = createButton("Save Settings", UITheme.SUCCESS);
        JButton btnReset = createButton("Reset to Default", UITheme.WARNING);

        btnSave.addActionListener(e -> saveSettings());
        btnReset.addActionListener(e -> resetSettings());

        btnPanel.add(btnReset);
        btnPanel.add(btnSave);

        // Add all to main panel
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(UITheme.BG_CARD);
        wrapper.setBorder(new EmptyBorder(0, 0, 0, 0));

        JPanel allForms = new JPanel();
        allForms.setLayout(new BoxLayout(allForms, BoxLayout.Y_AXIS));
        allForms.setBackground(UITheme.BG_CARD);
        allForms.add(shopInfo);
        allForms.add(Box.createVerticalStrut(24));
        allForms.add(btnPanel);

        panel.add(allForms, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSection(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(UITheme.BG_CARD);

        JLabel lbl = new JLabel(title);
        lbl.setFont(UITheme.FONT_SUBTITLE);
        lbl.setForeground(UITheme.TEXT_DARK);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        lbl.setBorder(new EmptyBorder(0, 0, 16, 0));

        panel.add(lbl);
        return panel;
    }

    private JPanel createFormRow(String label, JComponent field) {
        JPanel row = new JPanel(new BorderLayout(20, 0));
        row.setBackground(UITheme.BG_CARD);

        JLabel lbl = new JLabel(label);
        lbl.setFont(UITheme.FONT_BODY);
        lbl.setForeground(UITheme.TEXT_MEDIUM);
        lbl.setPreferredSize(new Dimension(180, 0));

        field.setFont(UITheme.FONT_BODY);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER),
            new EmptyBorder(8, 12, 8, 12)
        ));

        row.add(lbl, BorderLayout.WEST);
        row.add(field, BorderLayout.CENTER);

        return row;
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(UITheme.FONT_BUTTON);
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void saveSettings() {
        try {
            settingsDAO.setValue("shop_name", txtShopName.getText());
            settingsDAO.setValue("shop_address", txtShopAddress.getText());
            settingsDAO.setValue("shop_phone", txtShopPhone.getText());

            JOptionPane.showMessageDialog(this, "Settings saved successfully!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving settings: " + e.getMessage());
        }
    }

    private void resetSettings() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Reset all settings to default?", "Confirm",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            txtShopName.setText("Coffee Shop");
            txtShopAddress.setText("123 Street, City");
            txtShopPhone.setText("0901234567");
            saveSettings();
        }
    }
}
