package stefaniuk;

import stefaniuk.data.IncomingReportDataDto;
import stefaniuk.data.SignInDto;
import stefaniuk.data.UserRegistrationDto;
import stefaniuk.data.UserSettings;
import stefaniuk.database.DBConnector;
import stefaniuk.database.ReportEntryTableManager;
import stefaniuk.database.UserTableManager;
import stefaniuk.excel.ExcelFileWriter;
import stefaniuk.excel.ReportDataProvider;

import java.time.LocalDate;
import java.util.Arrays;

/**
 * class containing and managing all end points and requests from client
 */
public class Main {

    private DBConnector dbConnector;
    private UserTableManager userTableManager;
    private ReportEntryTableManager reportEntryTableManager;
    private ExcelFileWriter fileWriter;
    private ReportDataProvider dataProvider;
    private UserSettings user;


    public static void main(String[] args) {
        Main main = new Main();

        main.initialize();

        IncomingReportDataDto reportData = new IncomingReportDataDto(
                Arrays.asList("123 Main St, New York", "456 Oak Ave, California"),
                LocalDate.of(2026, 8, 24),
                40.5,
                37.0
        );


    }


    private void sighUp(UserRegistrationDto userData){
        if(userData == null){
            return;
        }
        if(userTableManager.doesUserExist(userData)){
            // TODO: notify front end that user exists
            throw new RuntimeException("main -> signUp(): user already exists");
        }
        userTableManager.signUp(userData);
    }


    private void signIn(SignInDto signInData){
        if(signInData == null){
            return;
        }

        int id = userTableManager.getIDbyNameAndPassword(signInData);
        UserSettings user = userTableManager.fetchUserSettings(id);

        if(user == null){
            // TODO notify frontend that user does not exist, suggest sign up
            throw new RuntimeException("Main -> signIn(): user does not exist");
        }
        //TODO send the token to user client (when ever this user does something, client send this token back,
        // server looks for this user in database (based on user id) and then does what user wants
        initializeUser(user);
    }


    private void updateUserSettings(UserRegistrationDto userData){
        if (userData == null) {
            return;
        }

        if(this.user.getId() <= 0 || user == null){
            // once signed in, this condition should never come true
            // it is just to be safe while testing
            throw new RuntimeException("Main -> updateUserSettings(): user does not exist");
        }
        userTableManager.updateUserSettings(this.user.getId(), userData);
    }


    private void createReport(IncomingReportDataDto reportData){
        if(reportData == null){
            return;
        }
        if (user == null){
            return;
        }
        reportEntryTableManager.saveReportData(user.getId(), reportData);
    }


    /**
     * initializes objects and some settings (such as clean up)
     * TODO has to be done right when web site starts (with spring annotation maybe)
     */
    private void initialize(){
        initializeDatabases();
        onShutDown();
    }


    private void initializeDatabases(){
        this.dbConnector = new DBConnector();
        this.userTableManager = new UserTableManager(dbConnector.getConnection());
        this.reportEntryTableManager = new ReportEntryTableManager(dbConnector.getConnection());
    }

    /**
     * initializes the UserSettings class level object representing the user data
     * such as user_id, hst_number, home_address, name...
     * @param userSettings populated UserSettings object retrieved from database
     */
    private void initializeUser(UserSettings userSettings){
        this.user = userSettings;
    }
    /**
     * sets the task to close db connection when runtime closes
     */
    private void onShutDown(){
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            dbConnector.closeConnection();
        }));

    }
}

