package dao;

import model.Table;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TableDAO {

    public List<Table> getAll() throws Exception {
        List<Table> tables = new ArrayList<>();
        String sql = "SELECT * FROM tables ORDER BY table_number";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                tables.add(mapResultSet(rs));
            }
        }
        return tables;
    }

    public Table getById(int id) throws Exception {
        String sql = "SELECT * FROM tables WHERE id = ?";

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

    public Table getByNumber(String tableNumber) throws Exception {
        String sql = "SELECT * FROM tables WHERE table_number = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tableNumber);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        }
        return null;
    }

    public List<Table> getAvailable() throws Exception {
        List<Table> tables = new ArrayList<>();
        String sql = "SELECT * FROM tables WHERE status = 'available' ORDER BY capacity";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                tables.add(mapResultSet(rs));
            }
        }
        return tables;
    }

    public int create(Table table) throws Exception {
        String sql = "INSERT INTO tables (table_number, capacity, status, position_x, position_y) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, table.getTableNumber());
            ps.setInt(2, table.getCapacity());
            ps.setString(3, table.getStatus());
            ps.setInt(4, table.getPositionX());
            ps.setInt(5, table.getPositionY());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    public boolean update(Table table) throws Exception {
        String sql = "UPDATE tables SET table_number = ?, capacity = ?, status = ?, position_x = ?, position_y = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, table.getTableNumber());
            ps.setInt(2, table.getCapacity());
            ps.setString(3, table.getStatus());
            ps.setInt(4, table.getPositionX());
            ps.setInt(5, table.getPositionY());
            ps.setInt(6, table.getId());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateStatus(int id, String status) throws Exception {
        String sql = "UPDATE tables SET status = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, id);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean occupy(int id) throws Exception {
        return updateStatus(id, "occupied");
    }

    public boolean release(int id) throws Exception {
        return updateStatus(id, "available");
    }

    public boolean reserve(int id) throws Exception {
        return updateStatus(id, "reserved");
    }

    public boolean delete(int id) throws Exception {
        String sql = "DELETE FROM tables WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);

            return ps.executeUpdate() > 0;
        }
    }

    private Table mapResultSet(ResultSet rs) throws Exception {
        Table t = new Table();
        t.setId(rs.getInt("id"));
        t.setTableNumber(rs.getString("table_number"));
        t.setCapacity(rs.getInt("capacity"));
        t.setStatus(rs.getString("status"));
        t.setPositionX(rs.getInt("position_x"));
        t.setPositionY(rs.getInt("position_y"));
        return t;
    }
}
