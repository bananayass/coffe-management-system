package model;

import java.sql.Timestamp;

public class InventoryAlert {
    private int id;
    private int productId;
    private String alertType;
    private int thresholdQuantity;
    private boolean isResolved;
    private Timestamp createdAt;
    private Timestamp resolvedAt;

    public InventoryAlert() {}

    public InventoryAlert(int productId, String alertType, int thresholdQuantity) {
        this.productId = productId;
        this.alertType = alertType;
        this.thresholdQuantity = thresholdQuantity;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getAlertType() { return alertType; }
    public void setAlertType(String alertType) { this.alertType = alertType; }

    public int getThresholdQuantity() { return thresholdQuantity; }
    public void setThresholdQuantity(int thresholdQuantity) { this.thresholdQuantity = thresholdQuantity; }

    public boolean isResolved() { return isResolved; }
    public void setResolved(boolean resolved) { isResolved = resolved; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(Timestamp resolvedAt) { this.resolvedAt = resolvedAt; }
}
