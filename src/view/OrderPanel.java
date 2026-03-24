package view;

import dao.OrderDAO;
import dao.ProductDAO;
import model.Order;
import model.OrderItem;
import model.Product;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderPanel extends JPanel {
    private ProductDAO productDAO = new ProductDAO();
    private OrderDAO orderDAO = new OrderDAO();

    private List<Product> products;
    private Map<Integer, Integer> cart = new HashMap<>();
    private DefaultListModel<String> cartModel = new DefaultListModel<>();
    private JList<String> cartList;
    private JLabel lblTotal, lblOrderCount;
    private int currentOrderId = -1;

    public OrderPanel() {
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_COLOR);

        JPanel leftPanel = createProductGrid();
        JPanel rightPanel = createCartPanel();

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        split.setDividerLocation(600);
        split.setResizeWeight(1);
        split.setBackground(UITheme.BG_COLOR);
        add(split, BorderLayout.CENTER);
    }

    private JPanel createProductGrid() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER),
            new EmptyBorder(0, 0, 0, 0)
        ));

        JLabel title = new JLabel("Products");
        title.setFont(UITheme.FONT_SUBTITLE);
        title.setForeground(UITheme.TEXT_DARK);
        title.setBorder(new EmptyBorder(20, 20, 16, 20));

        // Category tabs
        JPanel tabs = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        tabs.setBackground(UITheme.BG_COLOR);
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

        JPanel grid = new JPanel(new GridLayout(0, 3, 12, 12));
        grid.setBorder(new EmptyBorder(0, 16, 16, 16));
        grid.setBackground(UITheme.BG_COLOR);

        loadProductsGrid(grid, "");

        JScrollPane scroll = new JScrollPane(grid);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(UITheme.BG_COLOR);

        panel.add(title, BorderLayout.NORTH);
        panel.add(tabs, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JButton createTab(String text, boolean selected) {
        JButton btn = new JButton(text);
        btn.setFont(UITheme.FONT_SMALL);
        btn.setForeground(selected ? UITheme.TEXT_DARK : UITheme.TEXT_LIGHT);
        btn.setBackground(selected ? UITheme.PRIMARY : UITheme.BG_MEDIUM);
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
        } catch (Exception e) { e.printStackTrace(); }
        grid.revalidate();
        grid.repaint();
    }

    private void filterProducts(String category) {
        // Simply reload - the grid is inside the scroll pane
        Component[] comps = getComponents();
        for (Component c : comps) {
            if (c instanceof JSplitPane) {
                JSplitPane split = (JSplitPane) c;
                Component left = split.getLeftComponent();
                if (left instanceof JPanel) {
                    JPanel panel = (JPanel) left;
                    Component[] innerComps = panel.getComponents();
                    for (Component ic : innerComps) {
                        if (ic instanceof JScrollPane) {
                            JScrollPane scroll = (JScrollPane) ic;
                            if (scroll.getViewport().getView() instanceof JPanel) {
                                JPanel gridPanel = (JPanel) scroll.getViewport().getView();
                                loadProductsGrid(gridPanel, category);
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    private JPanel createProductCard(Product p) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(UITheme.BG_MEDIUM);
        card.setBorder(RoundedBorder.create(8, UITheme.BG_MEDIUM));
        card.setPreferredSize(new Dimension(160, 120));

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBackground(UITheme.BG_MEDIUM);
        info.setBorder(new EmptyBorder(8, 8, 8, 8));

        JLabel name = new JLabel(p.getName());
        name.setFont(UITheme.FONT_BODY);
        name.setForeground(UITheme.TEXT_DARK);

        JLabel category = new JLabel(p.getCategory());
        category.setFont(UITheme.FONT_SMALL);
        category.setForeground(UITheme.TEXT_LIGHT);

        JLabel price = new JLabel(String.format("%,.0f VND", p.getPrice()));
        price.setFont(new Font("Segoe UI", Font.BOLD, 14));
        price.setForeground(UITheme.PRIMARY);

        JLabel stock = new JLabel("Stock: " + p.getStockQuantity());
        stock.setFont(UITheme.FONT_SMALL);
        stock.setForeground(p.getStockQuantity() > 0 ? UITheme.SUCCESS : UITheme.DANGER);

        info.add(name);
        info.add(category);
        info.add(Box.createVerticalStrut(4));
        info.add(price);
        info.add(stock);

        JButton addBtn = new JButton("Add");
        addBtn.setFont(UITheme.FONT_BUTTON);
        addBtn.setForeground(UITheme.TEXT_DARK);
        addBtn.setBackground(UITheme.SUCCESS);
        addBtn.setFocusPainted(false);
        addBtn.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));
        addBtn.addActionListener(e -> addToCart(p));

        card.add(info, BorderLayout.CENTER);
        card.add(addBtn, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createCartPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG_COLOR);
        panel.setBorder(RoundedBorder.createWithPadding(12, UITheme.BG_MEDIUM, 16));
        panel.setPreferredSize(new Dimension(320, 0));

        JLabel title = new JLabel("Current Order");
        title.setFont(UITheme.FONT_SUBTITLE);
        title.setForeground(UITheme.TEXT_DARK);
        title.setBorder(new EmptyBorder(16, 16, 16, 16));

        cartList = new JList<>(cartModel);
        cartList.setFont(UITheme.FONT_BODY);
        cartList.setBackground(UITheme.BG_MEDIUM);
        cartList.setForeground(UITheme.TEXT_DARK);
        cartList.setSelectionBackground(UITheme.PRIMARY);

        JScrollPane scroll = new JScrollPane(cartList);
        scroll.setBorder(new EmptyBorder(0, 16, 0, 16));
        scroll.getViewport().setBackground(UITheme.BG_MEDIUM);

        JPanel buttons = new JPanel(new GridLayout(1, 3, 8, 0));
        buttons.setBorder(new EmptyBorder(16, 16, 16, 16));
        buttons.setBackground(UITheme.BG_COLOR);

        JButton btnNew = createButton("New Order", UITheme.PRIMARY);
        JButton btnComplete = createButton("Complete", UITheme.SUCCESS);
        JButton btnCancel = createButton("Cancel", UITheme.DANGER);

        btnNew.addActionListener(e -> startNewOrder());
        btnComplete.addActionListener(e -> completeOrder());
        btnCancel.addActionListener(e -> cancelOrder());

        buttons.add(btnNew);
        buttons.add(btnComplete);
        buttons.add(btnCancel);

        JPanel totalPanel = new JPanel(new BorderLayout());
        totalPanel.setBackground(UITheme.BG_COLOR);
        totalPanel.setBorder(new EmptyBorder(0, 16, 16, 16));

        lblOrderCount = new JLabel("Items: 0");
        lblOrderCount.setFont(UITheme.FONT_BODY);
        lblOrderCount.setForeground(UITheme.TEXT_LIGHT);

        lblTotal = new JLabel("Total: 0 VND");
        lblTotal.setFont(UITheme.FONT_SUBTITLE);
        lblTotal.setForeground(UITheme.PRIMARY);

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
        btn.setFont(UITheme.FONT_BUTTON);
        btn.setForeground(UITheme.TEXT_DARK);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void addToCart(Product p) {
        if (currentOrderId < 0) startNewOrder();
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
            currentOrderId = orderDAO.createOrder(1);
            if (currentOrderId < 0) JOptionPane.showMessageDialog(this, "Failed to create order!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void completeOrder() {
        if (currentOrderId < 0 || cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty!");
            return;
        }
        try {
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
            orderDAO.updateOrderTotal(currentOrderId);
            orderDAO.completeOrder(currentOrderId);
            JOptionPane.showMessageDialog(this, "Order completed!");
            cart.clear();
            cartModel.clear();
            currentOrderId = -1;
            updateCartDisplay();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
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
