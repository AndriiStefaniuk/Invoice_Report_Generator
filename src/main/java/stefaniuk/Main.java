package stefaniuk;

import stefaniuk.database.DBConnector;
import stefaniuk.database.ReportEntryTableManager;
import stefaniuk.database.TokenTableManager;
import stefaniuk.database.UserTableManager;

/**
 * entry point class to start the server
 */
public class Main {

    private DBConnector dbConnector;
    private UserTableManager userTableManager;
    private ReportEntryTableManager reportEntryTableManager;
    private TokenTableManager tokenTableManager;


    public static void main(String[] args) {
        Main main = new Main();


        main.setOnShutDown();
    }


    private void initializeDatabases(){
        this.dbConnector = new DBConnector();
        this.userTableManager = new UserTableManager(dbConnector.getConnection());
        this.reportEntryTableManager = new ReportEntryTableManager(dbConnector.getConnection());
        this.tokenTableManager = new TokenTableManager(dbConnector.getConnection());
    }


    /**
     * sets the task to close db connection when runtime closes
     */
    private void setOnShutDown(){
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            dbConnector.closeConnection();
        }));

    }
}

