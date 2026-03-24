package model;

import java.sql.Timestamp;

public class Customer {
    private int id;
    private String name;
    private String phone;
    private String email;
    private int loyaltyPoints;
    private double totalSpent;
    private Timestamp createdAt;
    private boolean isActive;

    public Customer() {}

    public Customer(String name, String phone, String email) {
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getLoyaltyPoints() { return loyaltyPoints; }
    public void setLoyaltyPoints(int loyaltyPoints) { this.loyaltyPoints = loyaltyPoints; }

    public double getTotalSpent() { return totalSpent; }
    public void setTotalSpent(double totalSpent) { this.totalSpent = totalSpent; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public String getLoyaltyTier() {
        if (loyaltyPoints >= 500) return "VIP Gold";
        if (loyaltyPoints >= 200) return "VIP Silver";
        if (loyaltyPoints >= 50) return "Member";
        return "New";
    }
}
