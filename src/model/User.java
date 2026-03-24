package model;

import java.sql.Timestamp;

public class User {
    private int id;
    private String username;
    private String password;
    private String fullName;
    private String role;
    private String phone;
    private String email;
    private boolean isActive;
    private Timestamp createdAt;

    public User() {}

    public User(int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public boolean isAdmin() { return "admin".equals(role); }
    public boolean isManager() { return "manager".equals(role); }
    public boolean isCashier() { return "cashier".equals(role); }
    public boolean isBarista() { return "barista".equals(role); }

    public String getRoleDisplay() {
        switch (role) {
            case "admin": return "Quản lý";
            case "manager": return "Quản lý";
            case "cashier": return "Thu ngân";
            case "barista": return "Pha chế";
            default: return role;
        }
    }
}
