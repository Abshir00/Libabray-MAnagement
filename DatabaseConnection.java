import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    public static Connection getConnection() {
        Connection connection = null;
        try {
            // Load the UCanAccess driver
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            
            // Connect to the database
            String dbURL = "jdbc:ucanaccess://C:/Users/MWESIGWA/Documents/NetBeansProjects/LibrarySystem/src/LibraryDB.accdb";
            connection = DriverManager.getConnection(dbURL);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }
}
