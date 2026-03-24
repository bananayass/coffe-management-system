package dao;

import model.InventoryAlert;
import model.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventoryAlertDAO {

    public List<InventoryAlert> getAll() throws Exception {
        List<InventoryAlert> alerts = new ArrayList<>();
        String sql = "SELECT * FROM inventory_alerts ORDER BY created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                alerts.add(mapResultSet(rs));
            }
        }
        return alerts;
    }

    public List<InventoryAlert> getUnresolved() throws Exception {
        List<InventoryAlert> alerts = new ArrayList<>();
        String sql = "SELECT * FROM inventory_alerts WHERE is_resolved = FALSE ORDER BY created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                alerts.add(mapResultSet(rs));
            }
        }
        return alerts;
    }

    public int create(InventoryAlert alert) throws Exception {
        String sql = "INSERT INTO inventory_alerts (product_id, alert_type, threshold_quantity) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, alert.getProductId());
            ps.setString(2, alert.getAlertType());
            ps.setInt(3, alert.getThresholdQuantity());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    public boolean resolve(int id) throws Exception {
        String sql = "UPDATE inventory_alerts SET is_resolved = TRUE, resolved_at = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean checkAndCreateAlert(int productId, int currentQuantity, int threshold) throws Exception {
        if (currentQuantity <= threshold) {
            try {
                // Check if alert already exists
                String checkSql = "SELECT id FROM inventory_alerts WHERE product_id = ? AND is_resolved = FALSE";
                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement ps = conn.prepareStatement(checkSql)) {
                    ps.setInt(1, productId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            InventoryAlert alert = new InventoryAlert(productId, "low_stock", threshold);
                            create(alert);
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public int getUnresolvedCount() throws Exception {
        String sql = "SELECT COUNT(*) FROM inventory_alerts WHERE is_resolved = FALSE";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    private InventoryAlert mapResultSet(ResultSet rs) throws Exception {
        InventoryAlert a = new InventoryAlert();
        a.setId(rs.getInt("id"));
        a.setProductId(rs.getInt("product_id"));
        a.setAlertType(rs.getString("alert_type"));
        a.setThresholdQuantity(rs.getInt("threshold_quantity"));
        a.setResolved(rs.getBoolean("is_resolved"));
        a.setCreatedAt(rs.getTimestamp("created_at"));
        a.setResolvedAt(rs.getTimestamp("resolved_at"));
        return a;
    }
}
