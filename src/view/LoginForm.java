package view;

import controller.AuthController;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

public class LoginForm extends JFrame {

    private JTextField txtUser;
    private JPasswordField txtPass;
    private JButton btnLogin, btnRegister;
    private JLabel lblTitle, lblUser, lblPass, lblError;

    private final AuthController controller = new AuthController();

    // Modern color palette (SaaS style)
    private static final Color PRIMARY_COLOR = new Color(37, 99, 235);      // #2563EB
    private static final Color SECONDARY_COLOR = new Color(59, 130, 246);   // #3B82F6
    private static final Color BG_COLOR = new Color(248, 250, 252);         // #F8FAFC
    private static final Color TEXT_COLOR = new Color(30, 41, 59);         // #1E293B
    private static final Color BORDER_COLOR = new Color(226, 232, 240);    // #E2E8F0
    private static final Color ERROR_COLOR = new Color(239, 68, 68);       // #EF4444

    public LoginForm() {
        initComponents();
        setupLayout();
        setupListeners();
        setVisible(true);
    }

    private void initComponents() {
        setTitle("Đăng nhập");
        setSize(400, 350);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel with background
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(BG_COLOR);
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        setContentPane(mainPanel);

        // Title
        lblTitle = new JLabel("Chào mừng!");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(TEXT_COLOR);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);

        // Labels
        lblUser = createLabel("Tên đăng nhập");
        lblPass = createLabel("Mật khẩu");

        // Error label
        lblError = new JLabel();
        lblError.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblError.setForeground(ERROR_COLOR);
        lblError.setHorizontalAlignment(SwingConstants.CENTER);
        lblError.setVisible(false);

        // Text fields with styling
        txtUser = createTextField();
        txtPass = createPasswordField();

        // Buttons
        btnLogin = createButton("Đăng nhập", PRIMARY_COLOR, Color.WHITE);
        btnRegister = createButton("Đăng ký", Color.WHITE, PRIMARY_COLOR);
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

        // Username label
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 0, 4, 0);
        add(lblUser, gbc);

        // Username field
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 12, 0);
        add(txtUser, gbc);

        // Password label
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 4, 0);
        add(lblPass, gbc);

        // Password field
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 8, 0);
        add(txtPass, gbc);

        // Error message
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 8, 0);
        add(lblError, gbc);

        // Buttons panel
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        btnPanel.setOpaque(false);
        btnPanel.add(btnLogin);
        btnPanel.add(btnRegister);

        gbc.gridy = 6;
        gbc.insets = new Insets(16, 0, 0, 0);
        add(btnPanel, gbc);
    }

    private void setupListeners() {
        // LOGIN
        btnLogin.addActionListener(e -> handleLogin());

        // REGISTER
        btnRegister.addActionListener(e -> handleRegister());

        // Enter key for login
        txtPass.addActionListener(e -> handleLogin());
    }

    private void handleLogin() {
        String user = txtUser.getText().trim();
        String pass = new String(txtPass.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            showError("Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        if (controller.login(user, pass)) {
            JOptionPane.showMessageDialog(this, "Đăng nhập thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            new MainFrame();
            dispose();
        } else {
            showError("Sai tài khoản hoặc mật khẩu!");
            txtPass.setText("");
            txtPass.requestFocus();
        }
    }

    private void handleRegister() {
        String user = txtUser.getText().trim();
        String pass = new String(txtPass.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            showError("Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        if (controller.register(user, pass)) {
            JOptionPane.showMessageDialog(this, "Đăng ký thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        } else {
            showError("Tên đăng nhập đã tồn tại!");
            txtUser.requestFocus();
        }
    }

    private void showError(String msg) {
        lblError.setText(msg);
        lblError.setVisible(true);
    }

    // --- Component Factories ---

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(TEXT_COLOR);
        return label;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField(20);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(10, 12, 10, 12)
        ));
        field.setBackground(Color.WHITE);
        return field;
    }

    private JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField(20);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(10, 12, 10, 12)
        ));
        field.setBackground(Color.WHITE);
        return field;
    }

    private JButton createButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bg.equals(PRIMARY_COLOR) ? PRIMARY_COLOR : BORDER_COLOR, 1),
            new EmptyBorder(10, 20, 10, 20)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);

        // Hover effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(bg.equals(PRIMARY_COLOR) ? SECONDARY_COLOR : new Color(248, 250, 252));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(bg);
            }
        });

        return btn;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}
