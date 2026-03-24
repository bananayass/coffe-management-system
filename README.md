# Coffee Shop Management Application

## Structure
```
MVC/
├── lib/
│   └── mysql-connector-j-9.6.0.jar
├── src/
│   ├── Main.java
│   ├── controller/
│   │   └── AuthController.java
│   ├── dao/
│   │   ├── DBConnection.java
│   │   ├── OrderDAO.java
│   │   ├── ProductDAO.java
│   │   └── UserDAO.java
│   ├── model/
│   │   ├── Order.java
│   │   ├── OrderItem.java
│   │   ├── Product.java
│   │   └── User.java
│   └── view/
│       ├── Dashboard.java
│       ├── LoginForm.java
│       ├── MainFrame.java
│       ├── OrderPanel.java
│       ├── ProductPanel.java
│       └── RevenuePanel.java
├── databasemigration
├── build.bat
└── settings.json
```

## Setup

### 1. Database
Run `databasemigration` in MySQL to create tables and sample data:
```sql
SOURCE databasemigration;
```

### 2. MySQL Config
Edit `src/dao/DBConnection.java` if needed:
- URL: `jdbc:mysql://localhost:3306/coffee_db`
- User: `root`
- Password: `` (empty by default)

### 3. Run
Double-click `build.bat` or compile manually:
```bash
javac -cp "lib/mysql-connector-j-9.6.0.jar" -d bin src/*.java src/**/*.java
java -cp "bin;lib/mysql-connector-j-9.6.0.jar" Main
```

## Features
- Login/Register
- Dashboard with stats
- Product management (CRUD)
- Order creation with cart
- Revenue charts and statistics
