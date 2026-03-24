package dao;

import model.Discount;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DiscountDAO {

    public List<Discount> getAll() throws Exception {
        List<Discount> discounts = new ArrayList<>();
        String sql = "SELECT * FROM discounts ORDER BY created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                discounts.add(mapResultSet(rs));
            }
        }
        return discounts;
    }

    public Discount getById(int id) throws Exception {
        String sql = "SELECT * FROM discounts WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        }
        return null;
    }

    public Discount getByCode(String code) throws Exception {
        String sql = "SELECT * FROM discounts WHERE code = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code.toUpperCase());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        }
        return null;
    }

    public List<Discount> getActive() throws Exception {
        List<Discount> discounts = new ArrayList<>();
        String sql = "SELECT * FROM discounts WHERE is_active = TRUE AND (max_uses IS NULL OR used_count < max_uses) AND (end_date IS NULL OR end_date >= CURDATE())";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                discounts.add(mapResultSet(rs));
            }
        }
        return discounts;
    }

    public int create(Discount discount) throws Exception {
        String sql = "INSERT INTO discounts (code, description, discount_type, discount_value, min_order_amount, max_uses, start_date, end_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, discount.getCode().toUpperCase());
            ps.setString(2, discount.getDescription());
            ps.setString(3, discount.getDiscountType());
            ps.setDouble(4, discount.getDiscountValue());
            ps.setDouble(5, discount.getMinOrderAmount());
            ps.setObject(6, discount.getMaxUses() > 0 ? discount.getMaxUses() : null);
            ps.setDate(7, discount.getStartDate());
            ps.setDate(8, discount.getEndDate());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    public boolean update(Discount discount) throws Exception {
        String sql = "UPDATE discounts SET code = ?, description = ?, discount_type = ?, discount_value = ?, min_order_amount = ?, max_uses = ?, start_date = ?, end_date = ?, is_active = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, discount.getCode().toUpperCase());
            ps.setString(2, discount.getDescription());
            ps.setString(3, discount.getDiscountType());
            ps.setDouble(4, discount.getDiscountValue());
            ps.setDouble(5, discount.getMinOrderAmount());
            ps.setObject(6, discount.getMaxUses() > 0 ? discount.getMaxUses() : null);
            ps.setDate(7, discount.getStartDate());
            ps.setDate(8, discount.getEndDate());
            ps.setBoolean(9, discount.isActive());
            ps.setInt(10, discount.getId());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean incrementUsage(int id) throws Exception {
        String sql = "UPDATE discounts SET used_count = used_count + 1 WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean toggleActive(int id) throws Exception {
        String sql = "UPDATE discounts SET is_active = NOT is_active WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws Exception {
        String sql = "DELETE FROM discounts WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);

            return ps.executeUpdate() > 0;
        }
    }

    public double applyDiscount(String code, double orderAmount) throws Exception {
        Discount discount = getByCode(code);
        if (discount == null || !discount.isValid()) {
            return 0;
        }

        double discountAmount = discount.calculateDiscount(orderAmount);
        if (discountAmount > 0) {
            incrementUsage(discount.getId());
        }

        return discountAmount;
    }

    private Discount mapResultSet(ResultSet rs) throws Exception {
        Discount d = new Discount();
        d.setId(rs.getInt("id"));
        d.setCode(rs.getString("code"));
        d.setDescription(rs.getString("description"));
        d.setDiscountType(rs.getString("discount_type"));
        d.setDiscountValue(rs.getDouble("discount_value"));
        d.setMinOrderAmount(rs.getDouble("min_order_amount"));

        Integer maxUses = (Integer) rs.getObject("max_uses");
        d.setMaxUses(maxUses != null ? maxUses : -1);

        d.setUsedCount(rs.getInt("used_count"));
        d.setStartDate(rs.getDate("start_date"));
        d.setEndDate(rs.getDate("end_date"));
        d.setActive(rs.getBoolean("is_active"));
        return d;
    }
}
