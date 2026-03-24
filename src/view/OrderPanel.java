package view;

import dao.OrderDAO;
import dao.ProductDAO;
import model.Order;
import model.OrderItem;
import model.Product;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderPanel extends JPanel {
    private ProductDAO productDAO = new ProductDAO();
    private OrderDAO orderDAO = new OrderDAO();

    private List<Product> products;
    private Map<Integer, Integer> cart = new HashMap<>(); // productId -> quantity
    private DefaultListModel<String> cartModel = new DefaultListModel<>();
    private JList<String> cartList;
    private JLabel lblTotal, lblOrderCount;
    private int currentOrderId = -1;

    // Colors
    private static final Color PRIMARY = new Color(99, 102, 241);
    private static final Color SUCCESS = new Color(34, 197, 94);
    private static final Color WARNING = new Color(251, 146, 60);
    private static final Color DANGER = new Color(239, 68, 68);
    private static final Color SIDEBAR_DARK = new Color(17, 24, 39);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color BG_COLOR = new Color(243, 244, 246);
    private static final Color TEXT_DARK = new Color(17, 24, 39);
    private static final Color TEXT_GRAY = new Color(107, 114, 128);
    private static final Color BORDER = new Color(229, 231, 235);

    public OrderPanel() {
        setLayout(new BorderLayout());
        setBackground(BG_COLOR);

        // Left - Products grid
        JPanel leftPanel = createProductGrid();

        // Right - Cart & Order
        JPanel rightPanel = createCartPanel();

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        split.setDividerLocation(600);
        split.setResizeWeight(1);
        add(split, BorderLayout.CENTER);
    }

    private JPanel createProductGrid() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createLineBorder(BORDER));

        JLabel title = new JLabel("Products");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setForeground(TEXT_DARK);
        title.setBorder(new EmptyBorder(16, 16, 16, 16));

        // Category tabs
        JPanel tabs = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        tabs.setBackground(CARD_BG);
        tabs.setBorder(new EmptyBorder(0, 16, 16, 16));

        JButton btnAll = createTab("All", true);
        btnAll.addActionListener(e -> filterProducts(""));
        tabs.add(btnAll);

        try {
            List<Product> all = productDAO.getAll();
            java.util.Set<String> categories = new java.util.HashSet<>();
            for (Product p : all) categories.add(p.getCategory());

            for (String cat : categories) {
                JButton btn = createTab(cat, false);
                btn.addActionListener(e -> filterProducts(cat));
                tabs.add(btn);
            }
        } catch (Exception e) { e.printStackTrace(); }

        // Products grid
        JPanel grid = new JPanel(new GridLayout(0, 3, 12, 12));
        grid.setBorder(new EmptyBorder(0, 16, 16, 16));
        grid.setBackground(CARD_BG);

        loadProductsGrid(grid, "");

        JScrollPane scroll = new JScrollPane(grid);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(CARD_BG);

        panel.add(title, BorderLayout.NORTH);
        panel.add(tabs, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JButton createTab(String text, boolean selected) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.PLAIN, 12));
        btn.setForeground(selected ? Color.WHITE : TEXT_GRAY);
        btn.setBackground(selected ? PRIMARY : BG_COLOR);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        return btn;
    }

    private void loadProductsGrid(JPanel grid, String category) {
        grid.removeAll();
        try {
            products = category.isEmpty() ? productDAO.getAll() : productDAO.getByCategory(category);
            for (Product p : products) {
                grid.add(createProductCard(p));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        grid.revalidate();
        grid.repaint();
    }

    private void filterProducts(String category) {
        JPanel parent = (JPanel) ((JScrollPane) ((JPanel) ((JPanel) getComponent(0)).getComponent(2)).getComponent(0)).getParent();
        JPanel grid = null;
        for (Component c : ((JPanel) getComponent(0)).getComponents()) {
            if (c instanceof JSplitPane) {
                JSplitPane split = (JSplitPane) c;
                grid = (JPanel) split.getLeftComponent();
                break;
            }
        }
        if (grid != null) {
            for (Component c : grid.getComponents()) {
                if (c instanceof JScrollPane) {
                    JScrollPane sp = (JScrollPane) c;
                    if (sp.getViewport().getView() instanceof JPanel) {
                        JPanel gridPanel = (JPanel) sp.getViewport().getView();
                        loadProductsGrid(gridPanel, category);
                    }
                }
            }
        }
    }

    private JPanel createProductCard(Product p) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(BG_COLOR);
        card.setBorder(BorderFactory.createLineBorder(BORDER));
        card.setPreferredSize(new Dimension(160, 120));

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBackground(BG_COLOR);
        info.setBorder(new EmptyBorder(8, 8, 8, 8));

        JLabel name = new JLabel(p.getName());
        name.setFont(new Font("Arial", Font.BOLD, 12));
        name.setForeground(TEXT_DARK);

        JLabel category = new JLabel(p.getCategory());
        category.setFont(new Font("Arial", Font.PLAIN, 10));
        category.setForeground(TEXT_GRAY);

        JLabel price = new JLabel(String.format("%,.0f VND", p.getPrice()));
        price.setFont(new Font("Arial", Font.BOLD, 14));
        price.setForeground(PRIMARY);

        JLabel stock = new JLabel("Stock: " + p.getStockQuantity());
        stock.setFont(new Font("Arial", Font.PLAIN, 10));
        stock.setForeground(p.getStockQuantity() > 0 ? SUCCESS : DANGER);

        info.add(name);
        info.add(category);
        info.add(Box.createVerticalStrut(4));
        info.add(price);
        info.add(stock);

        JButton addBtn = new JButton("Add");
        addBtn.setFont(new Font("Arial", Font.BOLD, 11));
        addBtn.setForeground(Color.WHITE);
        addBtn.setBackground(SUCCESS);
        addBtn.setFocusPainted(false);
        addBtn.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));
        addBtn.addActionListener(e -> addToCart(p));

        card.add(info, BorderLayout.CENTER);
        card.add(addBtn, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createCartPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createLineBorder(BORDER));
        panel.setPreferredSize(new Dimension(320, 0));

        JLabel title = new JLabel("Current Order");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setForeground(TEXT_DARK);
        title.setBorder(new EmptyBorder(16, 16, 16, 16));

        // Cart list
        cartList = new JList<>(cartModel);
        cartList.setFont(new Font("Arial", Font.PLAIN, 12));
        cartList.setBackground(BG_COLOR);
        cartList.setSelectionBackground(PRIMARY.brighter());

        JScrollPane scroll = new JScrollPane(cartList);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        scroll.setBorder(new EmptyBorder(0, 16, 0, 16));

        // Buttons
        JPanel buttons = new JPanel(new GridLayout(1, 3, 8, 0));
        buttons.setBorder(new EmptyBorder(16, 16, 16, 16));
        buttons.setBackground(CARD_BG);

        JButton btnNew = createButton("New Order", PRIMARY);
        JButton btnComplete = createButton("Complete", SUCCESS);
        JButton btnCancel = createButton("Cancel", DANGER);

        btnNew.addActionListener(e -> startNewOrder());
        btnComplete.addActionListener(e -> completeOrder());
        btnCancel.addActionListener(e -> cancelOrder());

        buttons.add(btnNew);
        buttons.add(btnComplete);
        buttons.add(btnCancel);

        // Total
        JPanel totalPanel = new JPanel(new BorderLayout());
        totalPanel.setBackground(CARD_BG);
        totalPanel.setBorder(new EmptyBorder(0, 16, 16, 16));

        lblOrderCount = new JLabel("Items: 0");
        lblOrderCount.setFont(new Font("Arial", Font.PLAIN, 12));
        lblOrderCount.setForeground(TEXT_GRAY);

        lblTotal = new JLabel("Total: 0 VND");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 18));
        lblTotal.setForeground(PRIMARY);

        totalPanel.add(lblOrderCount, BorderLayout.NORTH);
        totalPanel.add(lblTotal, BorderLayout.CENTER);

        panel.add(title, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(totalPanel, BorderLayout.SOUTH);
        panel.add(buttons, BorderLayout.SOUTH);

        return panel;
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 11));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        return btn;
    }

    private void addToCart(Product p) {
        if (currentOrderId < 0) {
            startNewOrder();
        }

        int qty = cart.getOrDefault(p.getId(), 0) + 1;
        if (qty > p.getStockQuantity()) {
            JOptionPane.showMessageDialog(this, "Not enough stock!");
            return;
        }
        cart.put(p.getId(), qty);
        updateCartDisplay();
    }

    private void updateCartDisplay() {
        cartModel.clear();
        double total = 0;
        int count = 0;

        try {
            for (Map.Entry<Integer, Integer> entry : cart.entrySet()) {
                Product p = productDAO.getById(entry.getKey());
                if (p != null) {
                    int qty = entry.getValue();
                    double subtotal = qty * p.getPrice();
                    cartModel.addElement(String.format("%s x%d = %,.0f", p.getName(), qty, subtotal));
                    total += subtotal;
                    count += qty;
                }
            }
        } catch (Exception e) { e.printStackTrace(); }

        lblOrderCount.setText("Items: " + count);
        lblTotal.setText(String.format("Total: %,.0f VND", total));
    }

    private void startNewOrder() {
        cart.clear();
        cartModel.clear();
        lblOrderCount.setText("Items: 0");
        lblTotal.setText("Total: 0 VND");

        try {
            currentOrderId = orderDAO.createOrder(1); // User ID 1 for demo
            if (currentOrderId < 0) {
                JOptionPane.showMessageDialog(this, "Failed to create order!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void completeOrder() {
        if (currentOrderId < 0 || cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty!");
            return;
        }

        try {
            // Save items
            for (Map.Entry<Integer, Integer> entry : cart.entrySet()) {
                Product p = productDAO.getById(entry.getKey());
                if (p != null) {
                    OrderItem item = new OrderItem();
                    item.setOrderId(currentOrderId);
                    item.setProductId(entry.getKey());
                    item.setQuantity(entry.getValue());
                    item.setUnitPrice(p.getPrice());
                    orderDAO.addItem(item);
                }
            }

            // Update total
            orderDAO.updateOrderTotal(currentOrderId);

            // Complete
            orderDAO.completeOrder(currentOrderId);

            JOptionPane.showMessageDialog(this, "Order completed!");
            cart.clear();
            cartModel.clear();
            currentOrderId = -1;
            updateCartDisplay();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void cancelOrder() {
        if (currentOrderId < 0) return;

        int confirm = JOptionPane.showConfirmDialog(this, "Cancel this order?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                orderDAO.cancelOrder(currentOrderId);
                cart.clear();
                cartModel.clear();
                currentOrderId = -1;
                updateCartDisplay();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
}
