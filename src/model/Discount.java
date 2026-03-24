package model;

import java.sql.Date;

public class Discount {
    private int id;
    private String code;
    private String description;
    private String discountType;
    private double discountValue;
    private double minOrderAmount;
    private int maxUses;
    private int usedCount;
    private Date startDate;
    private Date endDate;
    private boolean isActive;

    public Discount() {}

    public Discount(String code, String description, String discountType, double discountValue) {
        this.code = code;
        this.description = description;
        this.discountType = discountType;
        this.discountValue = discountValue;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDiscountType() { return discountType; }
    public void setDiscountType(String discountType) { this.discountType = discountType; }

    public double getDiscountValue() { return discountValue; }
    public void setDiscountValue(double discountValue) { this.discountValue = discountValue; }

    public double getMinOrderAmount() { return minOrderAmount; }
    public void setMinOrderAmount(double minOrderAmount) { this.minOrderAmount = minOrderAmount; }

    public int getMaxUses() { return maxUses; }
    public void setMaxUses(int maxUses) { this.maxUses = maxUses; }

    public int getUsedCount() { return usedCount; }
    public void setUsedCount(int usedCount) { this.usedCount = usedCount; }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public boolean isValid() {
        return isActive && usedCount < maxUses;
    }

    public double calculateDiscount(double orderAmount) {
        if (!isValid() || orderAmount < minOrderAmount) return 0;

        if ("percentage".equals(discountType)) {
            return orderAmount * discountValue / 100;
        } else if ("fixed".equals(discountType)) {
            return Math.min(discountValue, orderAmount);
        }
        return 0;
    }

    public String getDisplayValue() {
        if ("percentage".equals(discountType)) {
            return (int) discountValue + "%";
        }
        return String.format("%.0f VND", discountValue);
    }
}
