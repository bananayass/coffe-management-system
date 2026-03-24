package dao;

import model.Setting;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class SettingsDAO {

    public String getValue(String key) throws Exception {
        String sql = "SELECT setting_value FROM settings WHERE setting_key = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, key);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("setting_value");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean setValue(String key, String value) throws Exception {
        String sql = "INSERT INTO settings (setting_key, setting_value) VALUES (?, ?) ON DUPLICATE KEY UPDATE setting_value = ?, updated_at = CURRENT_TIMESTAMP";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, key);
            ps.setString(2, value);
            ps.setString(3, value);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Map<String, String> getAll() throws Exception {
        Map<String, String> settings = new HashMap<>();
        String sql = "SELECT * FROM settings";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                settings.put(rs.getString("setting_key"), rs.getString("setting_value"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return settings;
    }

    public int getInt(String key, int defaultValue) throws Exception {
        String value = getValue(key);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                // ignore
            }
        }
        return defaultValue;
    }

    public double getDouble(String key, double defaultValue) throws Exception {
        String value = getValue(key);
        if (value != null) {
            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException e) {
                // ignore
            }
        }
        return defaultValue;
    }

    // Common settings getters
    public String getShopName() throws Exception { return getValue("shop_name"); }
    public String getShopAddress() throws Exception { return getValue("shop_address"); }
    public String getShopPhone() throws Exception { return getValue("shop_phone"); }
    public double getTaxRate() throws Exception { return getDouble("tax_rate", 8); }
    public String getCurrency() throws Exception { return getValue("currency"); }
    public int getLowStockThreshold() throws Exception { return getInt("low_stock_threshold", 10); }
}
