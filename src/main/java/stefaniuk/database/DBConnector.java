package stefaniuk.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * class providing one single connection to the database
 */
public class DBConnector {

    private Connection connection;
    private final String url = System.getenv("DB_URL");
    private final String user = System.getenv("DB_USER");
    private final String password = System.getenv("DB_PASSWORD");

    public DBConnector(){
        establishConnection();
    }


    private void establishConnection() {
        try {
            this.connection = DriverManager.getConnection(url,user,password);
        } catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    public void closeConnection(){
        try {
            this.connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection(){
        return this.connection;
    }
}
