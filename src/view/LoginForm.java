package view;

import controller.AuthController;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class LoginForm extends JFrame {

    private JTextField txtUser;
    private JPasswordField txtPass;
    private JPanel txtPassPanel;
    private JButton btnLogin, btnRegister;
    private JButton btnTogglePass;
    private JLabel lblTitle, lblUser, lblPass, lblError;

    private final AuthController controller = new AuthController();

    public LoginForm() {
        initComponents();
        setupLayout();
        setupListeners();
        setVisible(true);
    }

    private void initComponents() {
        setTitle("Coffee Shop - Login");
        setSize(420, 480);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel with gradient
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Light gradient background
                GradientPaint gp = new GradientPaint(0, 0, new Color(248, 250, 252), 0, getHeight(), new Color(241, 245, 249));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setBorder(new EmptyBorder(40, 50, 40, 50));
        setContentPane(mainPanel);

        // Welcome section
        lblTitle = new JLabel("☕ Welcome!");
        lblTitle.setFont(UITheme.FONT_HEADER);
        lblTitle.setForeground(UITheme.PRIMARY);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);

        // Labels
        lblUser = createLabel("Username");
        lblPass = createLabel("Password");

        // Error
        lblError = new JLabel();
        lblError.setFont(UITheme.FONT_SMALL);
        lblError.setForeground(UITheme.DANGER);
        lblError.setHorizontalAlignment(SwingConstants.CENTER);
        lblError.setVisible(false);

        // Fields
        txtUser = createTextField();
        txtPassPanel = createPasswordField();

        // Buttons
        btnLogin = createButton("Login", UITheme.PRIMARY, Color.WHITE);
        btnRegister = createButton("Register", Color.WHITE, UITheme.PRIMARY);
    }

    private void setupLayout() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(lblTitle, gbc);

        // Username
        gbc.gridy = 1;
        gbc.insets = new Insets(24, 0, 4, 0);
        add(lblUser, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 12, 0);
        add(txtUser, gbc);

        // Password
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 4, 0);
        add(lblPass, gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 8, 0);
        add(txtPassPanel, gbc);

        // Error
        gbc.gridy = 5;
        add(lblError, gbc);

        // Buttons
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 12, 0));
        btnPanel.setOpaque(false);
        btnPanel.add(btnLogin);
        btnPanel.add(btnRegister);

        gbc.gridy = 6;
        gbc.insets = new Insets(20, 0, 0, 0);
        add(btnPanel, gbc);
    }

    private void setupListeners() {
        btnLogin.addActionListener(e -> handleLogin());
        btnRegister.addActionListener(e -> handleRegister());
        txtPass.addActionListener(e -> handleLogin());

        // Hover effects
        AnimationHelper.addHoverEffect(btnLogin);
        AnimationHelper.addHoverEffect(btnRegister);
    }

    private void handleLogin() {
        String user = txtUser.getText().trim();
        String pass = new String(txtPass.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            showError("Please fill all fields!");
            return;
        }

        if (controller.login(user, pass)) {
            JOptionPane.showMessageDialog(this, "Login successful! 🎉", "Success", JOptionPane.INFORMATION_MESSAGE);
            new MainFrame();
            dispose();
        } else {
            showError("Invalid username or password!");
            txtPass.setText("");
            txtPass.requestFocus();
        }
    }

    private void handleRegister() {
        String user = txtUser.getText().trim();
        String pass = new String(txtPass.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            showError("Please fill all fields!");
            return;
        }

        if (controller.register(user, pass)) {
            JOptionPane.showMessageDialog(this, "Registration successful! 🎉", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            showError("Username already exists!");
            txtUser.requestFocus();
        }
    }

    private void showError(String msg) {
        lblError.setText(msg);
        lblError.setVisible(true);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UITheme.FONT_BODY);
        label.setForeground(UITheme.TEXT_MEDIUM);
        return label;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField(20);
        field.setFont(UITheme.FONT_BODY);
        field.setBackground(UITheme.BG_LIGHT);
        field.setForeground(UITheme.TEXT_DARK);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER, 1),
            new EmptyBorder(12, 14, 12, 14)
        ));
        return field;
    }

    private JPanel createPasswordField() {
        txtPass = new JPasswordField(20);
        txtPass.setFont(UITheme.FONT_BODY);
        txtPass.setBackground(UITheme.BG_LIGHT);
        txtPass.setForeground(UITheme.TEXT_DARK);
        txtPass.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER, 1),
            new EmptyBorder(12, 14, 12, 14)
        ));

        // Create a wrapper panel with show/hide button
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(UITheme.BG_LIGHT);

        // Show/Hide button - using text labels
        btnTogglePass = new JButton("Show");
        btnTogglePass.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnTogglePass.setBackground(UITheme.BG_MEDIUM);
        btnTogglePass.setForeground(UITheme.TEXT_MEDIUM);
        btnTogglePass.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        btnTogglePass.setFocusPainted(false);
        btnTogglePass.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTogglePass.setToolTipText("Show password");

        btnTogglePass.addActionListener(e -> {
            if (txtPass.getEchoChar() != 0) {
                txtPass.setEchoChar((char) 0);
                btnTogglePass.setText("Hide");
                btnTogglePass.setToolTipText("Hide password");
            } else {
                txtPass.setEchoChar('\u2022');
                btnTogglePass.setText("Show");
                btnTogglePass.setToolTipText("Show password");
            }
        });

        wrapper.add(txtPass, BorderLayout.CENTER);
        wrapper.add(btnTogglePass, BorderLayout.EAST);

        return wrapper;
    }

    private JButton createButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(UITheme.FONT_BUTTON);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bg.equals(UITheme.PRIMARY) ? UITheme.PRIMARY : UITheme.BORDER, 1),
            new EmptyBorder(12, 24, 12, 24)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        return btn;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}
