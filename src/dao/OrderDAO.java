package dao;

import model.Order;
import model.OrderItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
    // Create new order and return order ID
    public int createOrder(int userId) throws Exception {
        String sql = "INSERT INTO orders (user_id, status, total_amount) VALUES (?, 'pending', 0)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    // Add item to order
    public boolean addItem(OrderItem item) throws Exception {
        String sql = "INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, item.getOrderId());
            ps.setInt(2, item.getProductId());
            ps.setInt(3, item.getQuantity());
            ps.setDouble(4, item.getUnitPrice());
            return ps.executeUpdate() > 0;
        }
    }

    // Update order total
    public void updateOrderTotal(int orderId) throws Exception {
        String sql = "UPDATE orders SET total_amount = " +
            "(SELECT SUM(quantity * unit_price) FROM order_items WHERE order_id = ?) WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.setInt(2, orderId);
            ps.executeUpdate();
        }
    }

    // Complete order (update status)
    public boolean completeOrder(int orderId) throws Exception {
        String sql = "UPDATE orders SET status = 'completed' WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            return ps.executeUpdate() > 0;
        }
    }

    // Cancel order
    public boolean cancelOrder(int orderId) throws Exception {
        String sql = "UPDATE orders SET status = 'cancelled' WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            return ps.executeUpdate() > 0;
        }
    }

    // Get all orders
    public List<Order> getAll() throws Exception {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM orders ORDER BY order_date DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Order(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getTimestamp("order_date"),
                    rs.getString("status"),
                    rs.getDouble("total_amount")
                ));
            }
        }
        return list;
    }

    // Get order by ID
    public Order getById(int id) throws Exception {
        String sql = "SELECT * FROM orders WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Order(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getTimestamp("order_date"),
                        rs.getString("status"),
                        rs.getDouble("total_amount")
                    );
                }
            }
        }
        return null;
    }

    // Get items for an order
    public List<OrderItem> getItems(int orderId) throws Exception {
        List<OrderItem> list = new ArrayList<>();
        String sql = "SELECT oi.*, p.name as product_name FROM order_items oi " +
            "JOIN products p ON oi.product_id = p.id WHERE oi.order_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderItem item = new OrderItem(
                        rs.getInt("id"),
                        rs.getInt("order_id"),
                        rs.getInt("product_id"),
                        rs.getInt("quantity"),
                        rs.getDouble("unit_price")
                    );
                    item.setProductName(rs.getString("product_name"));
                    list.add(item);
                }
            }
        }
        return list;
    }

    // Revenue stats - get total revenue
    public double getTotalRevenue() throws Exception {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) as total FROM orders WHERE status = 'completed'";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble("total");
        }
        return 0;
    }

    // Revenue by date range
    public List<Object[]> getRevenueByDate(String startDate, String endDate) throws Exception {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT DATE(order_date) as date, SUM(total_amount) as revenue " +
            "FROM orders WHERE status = 'completed' AND DATE(order_date) BETWEEN ? AND ? " +
            "GROUP BY DATE(order_date) ORDER BY date";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, startDate);
            ps.setString(2, endDate);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{rs.getString("date"), rs.getDouble("revenue")});
                }
            }
        }
        return list;
    }

    // Revenue by category
    public List<Object[]> getRevenueByCategory() throws Exception {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT p.category, SUM(oi.quantity * oi.unit_price) as revenue " +
            "FROM order_items oi JOIN orders o ON oi.order_id = o.id " +
            "JOIN products p ON oi.product_id = p.id " +
            "WHERE o.status = 'completed' GROUP BY p.category";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Object[]{rs.getString("category"), rs.getDouble("revenue")});
            }
        }
        return list;
    }

    // Orders count by status
    public int getOrderCountByStatus(String status) throws Exception {
        String sql = "SELECT COUNT(*) FROM orders WHERE status = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    // Get recent orders for activity feed
    public List<Object[]> getRecentOrders(int limit) throws Exception {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT o.id, o.status, o.total_amount, o.order_date, c.name as customer_name " +
            "FROM orders o LEFT JOIN customers c ON o.customer_id = c.id " +
            "ORDER BY o.order_date DESC LIMIT ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{
                        rs.getInt("id"),
                        rs.getString("status"),
                        rs.getDouble("total_amount"),
                        rs.getTimestamp("order_date"),
                        rs.getString("customer_name")
                    });
                }
            }
        }
        return list;
    }
}