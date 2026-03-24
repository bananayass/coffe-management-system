package model;

public class Table {
    private int id;
    private String tableNumber;
    private int capacity;
    private String status;
    private int positionX;
    private int positionY;

    public Table() {}

    public Table(String tableNumber, int capacity) {
        this.tableNumber = tableNumber;
        this.capacity = capacity;
        this.status = "available";
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTableNumber() { return tableNumber; }
    public void setTableNumber(String tableNumber) { this.tableNumber = tableNumber; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getPositionX() { return positionX; }
    public void setPositionX(int positionX) { this.positionX = positionX; }

    public int getPositionY() { return positionY; }
    public void setPositionY(int positionY) { this.positionY = positionY; }

    public boolean isAvailable() { return "available".equals(status); }
    public boolean isOccupied() { return "occupied".equals(status); }
    public boolean isReserved() { return "reserved".equals(status); }
}
