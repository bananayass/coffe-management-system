import dao.DBConnection;
import java.sql.Connection;

public class TestDB {
    public static void main(String[] args) {
        try {
            Connection conn = DBConnection.getConnection();
            System.out.println("KẾT NỐI MYSQL THÀNH CÔNG ✅");
        } catch (Exception e) {
            System.out.println("KẾT NỐI THẤT BẠI ❌");
            e.printStackTrace();
        }
    }
}