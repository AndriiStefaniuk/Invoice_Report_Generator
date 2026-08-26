package stefaniuk;

import stefaniuk.data.SignInDto;
import stefaniuk.data.UserRegistrationDto;
import stefaniuk.database.DBConnector;
import stefaniuk.database.ReportEntryTableManager;
import stefaniuk.database.UserTableManager;

/**
 * class containing and managing all end points and requests from client
 */
public class Main {

    private DBConnector dbConnector;
    private UserTableManager userTableManager;
    private ReportEntryTableManager reportEntryTableManager;


    public static void main(String[] args) {
        Main main = new Main();

        main.initialize();

//        main.userTableManager.signUp(new UserRegistrationDto(
//                "johndoe",                    // userName
//                "SecureP@ssword123",           // password (raw string input from form)
//                "John Doe",                    // fullName
//                "123 Main Street, Toronto, ON",// homeAddress
//                "123456789 RT0001",           // hstNumber
//                45.50                          // hourlyRate (double)
//        ));

//        SignInDto signInDto =  new SignInDto("johndoe", "SecureP@ssword123");
//        int id = main.userTableManager.getIDbyNameAndPassword(signInDto);
//
//        System.out.println(main.userTableManager.fetchUserSettings(id));



//        main.userTableManager.updateUserSettings(1, new UserRegistrationDto(
//                "Varenukpolslij",
//                "SecureP@ssword123",
//                "Andrii Stefaniuk",
//                "123 Some Street, Toronto, ON",
//                "123456789 RT0001",
//                50.50
//        ));
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
     * sets the task to close db connection when runtime closes
     */
    private void onShutDown(){
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            dbConnector.closeConnection();
        }));

    }
}

