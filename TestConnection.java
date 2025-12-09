import java.sql.Connection;
import java.sql.DriverManager;

public class TestConnection {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://127.0.0.1:5433/audigo";
        String user = "postgres";
        String password = "cheerup25!";

        System.out.println("Testing PostgreSQL connection...");
        System.out.println("URL: " + url);
        System.out.println("User: " + user);

        try {
            Class.forName("org.postgresql.Driver");
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("SUCCESS: Connected to PostgreSQL!");
            conn.close();
        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
