package stefaniuk.controller;

import stefaniuk.data.*;
import stefaniuk.database.ReportEntryTableManager;
import stefaniuk.database.TokenTableManager;
import stefaniuk.database.UserTableManager;
import stefaniuk.excel.ExcelFileWriter;
import stefaniuk.excel.ReportDataProvider;
import stefaniuk.exceptions.ReportOverlapException;
import stefaniuk.exceptions.TokenExpiredException;
import stefaniuk.exceptions.UserAlreadyExistsException;
import stefaniuk.exceptions.UserNotFoundException;

import java.time.LocalDate;
import java.util.List;


/**
 * class executing user requests, defines main logic what happens
 */
public class Service {

    private UserTableManager userTableManager;
    private ReportEntryTableManager reportEntryTableManager;
    private TokenTableManager tokenTableManager;
    private ExcelFileWriter fileWriter;
    private ReportDataProvider dataProvider;

    // temporary objets, used for testing purposes
    private String token;


    public Service(UserTableManager userTableManager, ExcelFileWriter fileWriter,
                   TokenTableManager tokenTableManager, ReportEntryTableManager reportEntryTableManager) {
        this.userTableManager = userTableManager;
        this.fileWriter = fileWriter;
        this.tokenTableManager = tokenTableManager;
        this.reportEntryTableManager = reportEntryTableManager;
    }


    /**
     * creates a new entry in the Database, storing the user sign in information
     * @param userData populated UserRegistrationDto object representing the user settings (username, password, full name...)
     * @throws UserAlreadyExistsException if user account already exists in the database
     * @throws IllegalArgumentException if userData (passed as an argument) contains invalid username or password
     */
    public void signUp(UserRegistrationDto userData){
        if (userData == null) {
            throw new IllegalArgumentException("Main -> signUp(): userData can't be null");
        }
        if(userTableManager.doesUserExist(userData)){
            throw new UserAlreadyExistsException("main -> signUp(): user already exists");
        }
        // create entry in database containing user settings
        userTableManager.signUp(userData);

        // sign in generates the token for user and displays the main page
        this.signIn(new SignInDto(userData.getUserName(), userData.getPassword()));
    }


    /**
     * checks if the user with given info exists in the database
     * if it does, generates the token string and assigns it to the user
     * @param signInData SignInDto object containing the username and password
     * @return String object representing the token (generated for the user)
     * @throws IllegalArgumentException if signInData (passed as an argument) contains invalid username or password
     * @throws UserNotFoundException if no user found in database
     */
    public String signIn(SignInDto signInData){
        if(signInData == null){
            throw new IllegalArgumentException("Main -> sighIn(): signInData (passed as argument) is null");
        }

        int userId = userTableManager.getIDbyNameAndPassword(signInData);
        if(userId <= 0){
            throw new IllegalArgumentException("Main -> sighIn(): invalid username or password (no such match found in the database)");
        }
        UserSettings user = getUser(userId);

        if(user == null){
            // TODO notify frontend that user does not exist, suggest sign up
            throw new UserNotFoundException("Main -> signIn(): user does not exist");
        }

        // assign the token to user and store it in table
        String token = tokenTableManager.generateToken();
        tokenTableManager.insertToken(token, userId);

        // TODO: return the token to client and return the userSettings to display as well
        this.token = token;
        return token;
    }


    /**
     * changes the row in database containing userSettings data
     * @param token the unique 32 character authentication token assigned to the user
     * @param userData UserRegistrationDto object containing updated user settings
     * @throws TokenExpiredException if token is expired
     * @throws IllegalArgumentException indicating failure to update settings (invalid updated user data)
     */
    public void updateUserSettings(String token, UserRegistrationDto userData){
        if (userData == null || token == null || token.isBlank()) {
            throw new IllegalArgumentException("Service -> updateUserSettings(): invalid inputs");
        }

        int userId = getIdByToken(token);
        if(userId <= 0){
            throw new TokenExpiredException("Service -> updateUserSettings(): invalid token");
        }
        userTableManager.updateUserSettings(userId, userData);
    }



    /**
     * stores the new report entry in database
     * @param token the unique 32 character authentication token assigned to the user
     * @param reportData populated IncomingReportDataDto object representing data set by user
     * @throws TokenExpiredException if token is expired
     * @throws IllegalArgumentException if inputs are invalid
     * @throws ReportOverlapException if chosen period overlaps with existing reports
     */
    public void saveReport(String token, IncomingReportDataDto reportData){
        if (reportData == null || token == null || token.isBlank()) {
            throw new IllegalArgumentException("Service -> saveReport(): invalid inputs");
        }

        int userId = getIdByToken(token);
        if(userId <= 0){
            // token might be expired, suggest sign in again
            throw new TokenExpiredException("Service -> saveReport(): invalid token");
        }
        reportEntryTableManager.saveReportData(userId, reportData);
    }

    /**
     * ONLY UPDATES the record for specific report in database
     * @param token the unique 32 character authentication token assigned to the user
     * @param initialReportStartDate LocalDate object representing the first day of two week period
     *                               (the report covering this period will be updated)
     * @param updatedReportData IncomingReportDataDto object containing new data to be stored
     *                          NOTE: periodStartDate must be same as initialReportStartDate
     * @throws TokenExpiredException if token is expired
     * @throws IllegalArgumentException if inputs (arguments) are invalid or period is changed
     */
    public void updateReport(String token, LocalDate initialReportStartDate, IncomingReportDataDto updatedReportData){
        if (initialReportStartDate == null || updatedReportData == null || token == null || token.isBlank()) {
            throw new IllegalArgumentException("Service -> updateReport(): invalid inputs");
        }

        int userId = getIdByToken(token);
        if(userId <= 0){
            // token might be expired, suggest sign in again
            throw new TokenExpiredException("Service -> updateReport(): invalid token");
        }

        reportEntryTableManager.updateReport(userId, initialReportStartDate, updatedReportData);
    }


    /**
     * deletes the existing report record for this specific user from the database
     * @param token the unique 32 character authentication token assigned to the user
     * @param periodStartDate start date of the period that should be deleted
     * @throws TokenExpiredException if token is expired
     * @throws IllegalArgumentException if inputs (arguments) are invalid
     */
    public void deleteReport(String token, LocalDate periodStartDate){
        if (token == null || token.isBlank() || periodStartDate == null) {
            throw new IllegalArgumentException("Service -> deleteReport(): invalid inputs");
        }

        int userId = getIdByToken(token);
        if(userId <= 0){
            // token might be expired, suggest sign in again
            throw new TokenExpiredException("Service -> deleteReport(): invalid token");
        }

        reportEntryTableManager.deleteReport(userId, periodStartDate);
    }


    /**
     * retrieves all reports stored in database for specific user
     * @param token unique 32 character authentication token assigned to the user
     * @return List of OutgoingReportDataDto objects representing all reports stored in the database for specific user
     * @throws TokenExpiredException if token is expired
     */
    public List<OutgoingReportDataDto> getAllReportList(String token){
        if(token == null || token.isBlank()){
            return null;
        }

        int userId = getIdByToken(token);
        if(userId <= 0){
            // token might be expired, suggest sign in again
            throw new TokenExpiredException("Service -> deleteReport(): invalid token");
        }

        return reportEntryTableManager.getAllReportList(userId);
    }


    /**
     * fetches the user settings, such as user's full name, HST number, hourly rate... from database
     * @param userId int object representing the unique number (user identifier in database)
     * @return populated UserSettings object or null if no user with provided ID is found
     */
    private UserSettings getUser(int userId){
        if (userId <= 0){
            return null;
        }
        return this.userTableManager.fetchUserSettings(userId);
    }

    /**
     * returns userId that corresponds to the provided token
     * @param token String object representing the 32 character long AlphaNumeric token
     * @return int object representing userId that corresponds to provided token, or -1 if no match found or token is expired
     */
    private int getIdByToken(String token){
        if(token == null || token.isEmpty()){
            return -1;
        }

        try{
            return this.tokenTableManager.getUserId(token);
        } catch(IllegalStateException e){
            return -1;
        }
    }

}
