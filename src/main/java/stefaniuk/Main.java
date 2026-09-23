package stefaniuk;

import stefaniuk.controller.RequestHandler;
import stefaniuk.controller.Service;
import stefaniuk.data.IncomingReportDataDto;
import stefaniuk.data.UserSettings;
import stefaniuk.database.DBConnector;
import stefaniuk.database.ReportEntryTableManager;
import stefaniuk.database.TokenTableManager;
import stefaniuk.database.UserTableManager;
import stefaniuk.excel.ExcelFileWriter;
import stefaniuk.excel.ReportDataProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * entry point class to start the server
 */
public class Main {

    private DBConnector dbConnector;
    private RequestHandler requestHandler;
    private Service service;
    private UserTableManager userTableManager;
    private ReportEntryTableManager reportEntryTableManager;
    private TokenTableManager tokenTableManager;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);


    public static void main(String[] args) {
        Main main = new Main();
        main.initializeDatabases();
        main.initializeService();
        main.initializeRequestHandler();

        main.initializeTestingMethods();

        main.setOnShutDown();
        main.startTokenCleanUp();
    }


    private void initializeDatabases(){
        this.dbConnector = new DBConnector();
        this.userTableManager = new UserTableManager(dbConnector.getConnection());
        this.reportEntryTableManager = new ReportEntryTableManager(dbConnector.getConnection());
        this.tokenTableManager = new TokenTableManager(dbConnector.getConnection());
    }

    private void initializeService(){
        this.service = new Service(userTableManager, tokenTableManager, reportEntryTableManager);
    }

    private void initializeRequestHandler(){
        this.requestHandler = new RequestHandler(this.service);
    }


    /**
     * sets the task to close db connection when runtime closes
     */
    private void setOnShutDown(){
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            dbConnector.closeConnection();
            stopTokenCleanUp();
        }));
    }


    private void startTokenCleanUp() {
        Runnable tokenCleanUp = () -> {
            try {
                tokenTableManager.cleanUpExpiredTokens();
            } catch (Exception e) {
                System.err.println("Error while doing token cleanup: " + e.getMessage());
            }
        };

        scheduler.scheduleWithFixedDelay(tokenCleanUp, 0, 10, TimeUnit.HOURS);
    }

    public void stopTokenCleanUp() {
        scheduler.shutdown();
    }


    private void initializeTestingMethods(){

        UserSettings userSettings = new UserSettings(
                1,
                "Sarah Jenkins",
                "4200 University Ave, Unit 12|Mississauga, ON L5L 1C6",
                "987654321RT0002",
                82.00
        );

        IncomingReportDataDto reportData = new IncomingReportDataDto();
        reportData.setSiteAddresses(List.of(
                "100 King Street West, Toronto, ON",
                "333 Bay Street, Toronto, ON"

        ));
        reportData.setPeriodStartDate(LocalDate.of(2026, 8, 31));
        reportData.setFirstWeekHours(40.0);
        reportData.setSecondWeekHours(37.5);


        ReportDataProvider rpd = new ReportDataProvider(userSettings, reportData);

        ExcelFileWriter fileWriter = new ExcelFileWriter(rpd);

        File reportFile = fileWriter.getReportFile();
        try (FileInputStream fileInputStream = new FileInputStream(reportFile)){

            
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

