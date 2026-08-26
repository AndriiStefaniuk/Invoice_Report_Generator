package stefaniuk.database;

import java.sql.Connection;

/**
 * dedicated to handle queries related to report_entries table in database
 */
public class ReportEntryTableManager {
    private Connection connection;


    public ReportEntryTableManager(Connection connection) {
        this.connection = connection;
    }
}
