package stefaniuk.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * class providing one single connection to the database
 */
public class DBConnector {

    private Connection connection;
    private final String url = "jdbc:postgresql://localhost:5432/uhtam_reporting_app";
    private final String user = "postgres";
    private final String password = "Stefaniuk_07";

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
