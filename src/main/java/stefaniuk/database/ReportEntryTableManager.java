package stefaniuk.database;

import stefaniuk.data.IncomingReportDataDto;
import stefaniuk.data.OutgoingReportDataDto;

import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * meant to handle queries related to report_entries table in database
 */
public class ReportEntryTableManager {

    private Connection connection;


    public ReportEntryTableManager(Connection connection) {
        this.connection = connection;
    }

    /**
     * stores the report data to database if valid
     * @param userID int object representing user who sends the request to save the report
     * @param reportData populated IncomingReportDataDto object representing data set by user
     * @throws IllegalArgumentException if reportData contains periodStartDate overlapping with existing reports
     */
    public void saveReportData(int userID, IncomingReportDataDto reportData){
        checkConnectionValidity("saveReportData");

        if (reportData == null || reportData.getPeriodStartDate() == null) {
            throw new IllegalArgumentException("ReportEntryTableManager -> saveReportData(): " +
                    "Report data and period start date must not be null");
        }

        // check if the report period is valid
        LocalDate reportPeriodStart = calculatePeriodStartDate(reportData.getPeriodStartDate());
        if(!isRangeValid(userID, reportPeriodStart)){
            throw new IllegalArgumentException("ReportEntryTableManager -> saveReportData(): " +
                    "Invalid report period (part of period is covered by other report(s) ");
        }
        String sql = "INSERT INTO report_entries (user_id, period_start_date, site_addresses, first_week_hours, second_week_hours) " +
                "VALUES (?, ?, ?, ?, ?)";

        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            // fills in ? placeholders in sql String with values
            preparedStatement.setInt(1, userID);

            // converts raw user-selected day (like Friday) to be the Monday of the same week (proper start of report period)
            preparedStatement.setDate(2, Date.valueOf(reportPeriodStart));

            // convert List to array of Strings
            String[] addressArray = reportData.getSiteAddressArray().toArray(new String[0]);
            //  Wrap the Java array into a SQL TEXT Array
            Array sqlArray = connection.createArrayOf("text", addressArray);
            preparedStatement.setArray(3, sqlArray);

            preparedStatement.setDouble(4, reportData.getFirstWeekHours());
            preparedStatement.setDouble(5, reportData.getSecondWeekHours());

           preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * updates the current record in the database
     *
     * @param userId int object representing the unique user id number
     * @param initialReportStartDate LocalDate object representing the first day of two week period (the report covering this period will be updated)
     * @param updatedReportData IncomingReportDataDto object containing new data to be stored
     *                          NOTE: periodStartDate must be same as initialReportStartDate
     */
    public void updateReport(int userId, LocalDate initialReportStartDate, IncomingReportDataDto updatedReportData){
        checkConnectionValidity("updateReport");

        if(updatedReportData == null || userId <= 0){
            return;
        }
        if(!isMonday(initialReportStartDate)){
            throw new RuntimeException("ReportEntryTableManager -> updateReport(): initialReportStartDate should be Monday");
        }
        if (updatedReportData.getPeriodStartDate() == null) {
            throw new IllegalArgumentException("ReportEntryTableManager -> updateReport(): Updated report period start date cannot be null");
        }

        LocalDate updatedReportStartDate = calculatePeriodStartDate(updatedReportData.getPeriodStartDate());
        if(!initialReportStartDate.equals(updatedReportStartDate)){
            throw new IllegalArgumentException("ReportEntryTableManager -> updateReport(): reportStartDate should not be changed");
        }

        String sql = "UPDATE report_entries SET " +
                "site_addresses = ?, " +
                "first_week_hours = ?, " +
                "second_week_hours = ? " +
                "WHERE user_id = ? AND period_start_date = ?";

        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)){

            String[] siteAddresses = updatedReportData.getSiteAddressArray().toArray(new String[0]);
            Array siteAddressesArray = connection.createArrayOf("text", siteAddresses);
            preparedStatement.setArray(1, siteAddressesArray);

            preparedStatement.setDouble(2, updatedReportData.getFirstWeekHours());
            preparedStatement.setDouble(3, updatedReportData.getSecondWeekHours());
            preparedStatement.setInt(4, userId);
            preparedStatement.setDate(5, Date.valueOf(initialReportStartDate));

            int rowsUpdated = preparedStatement.executeUpdate();

            if(rowsUpdated == 1){
                System.out.println("ReportEntryTableManager -> updateReport(): Successfully updated the report data");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * deletes report record from the database for the user covering specified two week period of time
     * @param userId Int object representing the user unique id number (user whose report to delete)
     * @param periodStartDate LocalDate object representing the first day of two week period
     *                        based on which the report record to delete will be found in database
     */
    public void deleteReport(int userId, LocalDate periodStartDate){
        if (userId <= 0 || periodStartDate == null){
            throw new IllegalArgumentException("ReportEntryTableManager -> deleteReport(): wrong arguments; \n" +
                    "userId = " + userId + "periodStartDate is null = " + (periodStartDate == null));
        }
        checkConnectionValidity("deleteReport");

        if(!isMonday(periodStartDate)){
            periodStartDate = calculatePeriodStartDate(periodStartDate);
        }

        String sql = "DELETE FROM report_entries WHERE user_id = ? AND period_start_date = ?";

        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1, userId);

            preparedStatement.setDate(2, Date.valueOf(periodStartDate));

            int rowsDeleted = preparedStatement.executeUpdate();
            if (rowsDeleted == 1) {
                System.out.println("ReportEntryTableManager -> deleteReport(): successfully deleted report entry from database");
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * retrieves all reports stored in database for specific user
     * @param userId int object representing the id of user to look the reports for
     * @return List containing OutgoingReportDataDto objects representing report data stored in the database
     */
    public List<OutgoingReportDataDto> getAllReportList(int userId){
        checkConnectionValidity("getAllReportList");

        if(userId <= 0) return null;

        String sql = "SELECT * FROM report_entries WHERE user_id = ?";
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1, userId);

            List<OutgoingReportDataDto> reportDataList = new ArrayList<>();

            try(ResultSet resultSet = preparedStatement.executeQuery()){
                while(resultSet.next()){
                    int id = resultSet.getInt("id");
                    String[] siteAddressesArray = (String[]) resultSet.getArray("site_addresses").getArray();
                    List<String> siteAddressesList = Arrays.asList(siteAddressesArray);

                    // period start date contains the first day of the two week period (Monday)
                    LocalDate firstWeekStartDate = resultSet.getDate("period_start_date").toLocalDate();
                    LocalDate firstWeekEndDate = getSaturdayDate(firstWeekStartDate);

                    // Monday of the second week in report
                    LocalDate secondWeekStartDate = firstWeekStartDate.plusDays(7);
                    LocalDate secondWeekEndDate = getSaturdayDate(secondWeekStartDate);

                    double firstWeekHour = resultSet.getDouble("first_week_hours");
                    double secondWeekHour = resultSet.getDouble("second_week_hours");

                    reportDataList.add(new OutgoingReportDataDto(id, siteAddressesList, firstWeekStartDate, firstWeekEndDate,
                            firstWeekHour, secondWeekStartDate, secondWeekEndDate, secondWeekHour));
                }
            }
            return reportDataList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * calculates the date of the end of the working week (Saturday)
     * of the same week as provided mondayDate which has to be Monday
     * @param mondayDate LocalDate object representing the Monday of the week
     * @return LocalDate object representing the Saturday of the same week as mondayDate (Monday)
     * @throws RuntimeException if the mondayDate provided is not Monday
     */
    private LocalDate getSaturdayDate(LocalDate mondayDate) {
        if(!isMonday(mondayDate)){
            throw new RuntimeException("ReportEntryTableManager -> getSaturdayDate(): mondayDate should be Monday");
        }

        // Saturday, used as the end of report week
        LocalDate weekEndDate = mondayDate.plusDays(5);

        if(weekEndDate.getDayOfWeek() != DayOfWeek.SATURDAY){
            // at this point, after previous if statement, this condition should never evaluate to true
            // still keep it for some case
            return null;
        }
        return weekEndDate;
    }


    /**
     * takes raw date as beginning of report invoice period given by user,
     * converts it to proper beginning of period which is definitely a Monday
     *  NOTE:
     *    - SUNDAY is treated as beginning of week
     *    - MONDAY is the proper start of report period
     * @param rawDate LocalDate object representing selected by user start date of report period
     * @return LocalDate object that is guaranteed to be Monday (proper start of report invoice period)
     */
    private LocalDate calculatePeriodStartDate(LocalDate rawDate){
        if(rawDate == null){
            return null;
        }
        // keeps track of what day of week it is (MONDAY, TUESDAY)
        DayOfWeek dayOfWeek = rawDate.getDayOfWeek();
        int steps = 0;
        // goes back/forth until the day of week is MONDAY
        while(dayOfWeek != DayOfWeek.MONDAY){
            // move backward one day if it is not Sunday and not MONDAY
            // (treats Sunday as beginning of actual week, NOT REPORT START PERIOD)
            if(dayOfWeek != DayOfWeek.SUNDAY){
                dayOfWeek = dayOfWeek.minus(1);
                steps --; // move one step back (from Wednesday to Tuesday)
            } else { // move forward one day if it is SUNDAY
                dayOfWeek = dayOfWeek.plus(1);
                steps ++; // move one step forward (from Sunday to Monday)
            }
        }
        // take required number of steps (add or subtrack days) to get to the Monday of the week
        return rawDate.plusDays(steps);
    }


    /**
     * checks if the report period is valid.
     * Checks if the period of two weeks, starting from periodStartDate (Monday), is not covered in any other reports
     * @param userId int object representing user id to look the reports for
     * @param periodStartDate LocalDate object representing the first day of two week report period (has to be Monday)
     * @return true if not even a single day (two weeks starting from periodStartDate) is covered by other reports, false otherwise
     */
    private boolean isRangeValid(int userId, LocalDate periodStartDate){
        // periodStartDate has to be Monday
        if(!isMonday(periodStartDate)){
            throw new RuntimeException("ReportEntryTableManager -> isRangeValid(): periodStartDate should be Monday");
        }
        // check if report with exact same two week period, starting at periodStartDate (Monday), exists
        if(isRangeCovered(userId, periodStartDate)) {
            return false;
        }

        // check if the report, starting previous week Monday exists where
        // first half of two week report starting from periodStartDate (new report I am trying to insert) is covered by existing report
        else if(isRangeCovered(userId, periodStartDate.minusDays(7))) {
            return false;
        }

        // check if the report, starting next week Monday exists where
        // second half of two week report starting from periodStartDate (new report I am trying to insert) is covered by existing report
        else return !isRangeCovered(userId, periodStartDate.plusDays(7));

    }

    /**
     * checks if record with provided period exists in database for specified user
     * @param userId int object representing the user
     * @param periodStartDate LocalDate object representing the first day (Monday) of two week period stored in database
     * @return true if the entry with same range exists, false otherwise
     * @throws RuntimeException if the periodStartDate provided is not Monday
     */
    private boolean isRangeCovered(int userId, LocalDate periodStartDate){
        String sql = "SELECT 1 FROM report_entries WHERE user_id = ? AND period_start_date = ?";
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, userId);
            preparedStatement.setDate(2, Date.valueOf(periodStartDate));

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) { // at least one result was retrieved,
                    // report with same period start date exists in database
                    return true;
                }
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private boolean isMonday(LocalDate day){
        return day.getDayOfWeek() == DayOfWeek.MONDAY;
    }


    /**
     * checks if connection object (connection to database) is valid
     * @param callerMethod String object representing the name of method calling this function NO PARATHESIS
     * @throws RuntimeException if connection is invalid
     */
    private void checkConnectionValidity(String callerMethod) {
        if(!isConnectionValid()){
            throw new RuntimeException(this.getClass().getName() + " -> " + callerMethod + "(): Invalid db connection ");
        }
    }

    private boolean isConnectionValid(){
        try {
            return this.connection != null && !this.connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}