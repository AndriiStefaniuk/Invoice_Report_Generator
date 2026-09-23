package stefaniuk.excel;

import stefaniuk.data.IncomingReportDataDto;
import stefaniuk.data.UserSettings;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * class used to provide data, either from the database or calculate some unrelated to database data, which is used in report
 */
public class ReportDataProvider {

    private UserSettings userSettings;
    private IncomingReportDataDto reportData;

    public ReportDataProvider(UserSettings userSettings, IncomingReportDataDto reportData){
        this.userSettings = userSettings;
        this.reportData = reportData;
    }


    public String getHstNumber() {
        return userSettings.getHstNumber();
    }



    /**
     * generates a 6 digit serial number
     * @return String object representing the 6 digit serial number
     */
    public String generateSerialNumber(){
        String userId = String.valueOf(userSettings.getId());

        String timePeriod = String.valueOf(reportData.getPeriodStartDate());
        timePeriod = timePeriod.substring(5).replace("-", "");

        String serialNumber = userId + timePeriod;

        // make sure serial number is at least 6 digits
        while(serialNumber.length() < 6){
            serialNumber = "0" + serialNumber;
        }

        // keep it as 6 digit long
        serialNumber = serialNumber.substring(0, 6);

        return serialNumber;
    }

    /**
     * returns the period covered by the report in format DD.MM - DD.MM.YYYY
     * @return String object representing the period covered by the report in format DD.MM - DD.MM.YYYY
     */
    public String getReportPeriod(){
        DateTimeFormatter periodStartDateFormat = DateTimeFormatter.ofPattern("dd/MM");
        DateTimeFormatter periodEndDateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // returns the day (MONDAY) which is the first day of the period
        LocalDate periodStartDate = reportData.getPeriodStartDate();
        String formattedStart = periodStartDate.format(periodStartDateFormat);

        // this is the last day of the two week period (SATURDAY), Sunday is not working
        LocalDate periodEndDate = periodStartDate.plusDays(12);
        String formattedEnd = periodEndDate.format(periodEndDateFormat);

        return formattedStart + " - " + formattedEnd;
    }


    public String getFullName(){
        return userSettings.getFullName();
    }

    /**
     * @return String object containing street name, and building number (apt)
     */
    public String getStreetAddress(){
        // first part is the street and building number (apt)
        // second is town, province, postal code
        String[] addressParts = userSettings.getHomeAddress().split("\\|");


        return addressParts[0];
    }


    /**
     * @return String object containing town, province, postal code
     */
    public String getCityProvincePostalCode(){
        // first part is the street and building number (apt)
        // second is town, province, postal code
        String[] addressParts = userSettings.getHomeAddress().split("\\|");

        return addressParts[1];
    }

    public List<String> getSiteAddresses(){
        return reportData.getSiteAddresses();
    }


    public String getFirstWeekDate() {
        // start date is always Monday
        LocalDate week1StartDate =  reportData.getPeriodStartDate();
        // end date has to fall on Saturday
        LocalDate week1EndDate = week1StartDate.plusDays(5);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");

        return week1StartDate.format(formatter) + " - " + week1EndDate.format(formatter);
    }


    public String getSecondWeekDate() {
        // start date is always Monday
        LocalDate week2StartDate =  reportData.getPeriodStartDate().plusDays(7);
        // end date has to fall on Saturday
        LocalDate week2EndDate = week2StartDate.plusDays(5);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");

        return week2StartDate.format(formatter) + " - " + week2EndDate.format(formatter);
    }


    /**
     * @return String object containing the number of hours
     */
    public String getFirstWeekHours(){
        return String.valueOf(reportData.getFirstWeekHours());
    }

    /**
     * @return String object containing the number of hours
     */
    public String getSecondWeekHours(){
        return String.valueOf(reportData.getSecondWeekHours());
    }


    /**
     * @return String object containing the total number of hours
     */
    public String getTotalHours(){
        double totalHours = reportData.getFirstWeekHours() + reportData.getSecondWeekHours();
        return String.format("%.2f", totalHours);
    }

    public String getHourlyRate(){
        return String.valueOf(userSettings.getHourlyRate());
    }



    /**
     * returns the amount earned in first week (hourly rate * hours worked)
     * @return array of integers, first element is dollars, second is cents
     */
    public int[] getFirstWeekEarned(){
        double hourlyRate = userSettings.getHourlyRate();
        double hoursWorked = reportData.getFirstWeekHours();

        double earned = hourlyRate * hoursWorked; // in dollars and cents

        return separateDollarsAndCents(earned);
    }


    /**
     * returns the amount earned in second week (hourly rate * hours worked)
     * @return array of integers, first element is dollars, second is cents
     */
    public int[] getSecondWeekEarned(){
        double hourlyRate = userSettings.getHourlyRate();
        double hoursWorked = reportData.getSecondWeekHours();

        double earned = hourlyRate * hoursWorked; // in dollars and cents

        return separateDollarsAndCents(earned);
    }


    /**
     * returns the amount earned in two weeks (hourly rate * hours worked)
     * @return array of integers, first element is dollars, second is cents
     */
    public int[] getTotalEarned(){
        double hourlyRate = userSettings.getHourlyRate();
        double hoursWorked = reportData.getFirstWeekHours() + reportData.getSecondWeekHours();

        double earned = hourlyRate * hoursWorked; // in dollars and cents

        return separateDollarsAndCents(earned);
    }


    /**
     * returns the HST amount (13% of earned total)
     * @return array of integers, first element is dollars, second is cents
     */
    public int[] getHstAmount(){
        int[] dollarsAndCents = getTotalEarned();

        BigDecimal dollars = new BigDecimal(String.valueOf(dollarsAndCents[0]));
        BigDecimal cents = new BigDecimal(String.valueOf(dollarsAndCents[1]));
        BigDecimal amount = dollars.add(cents.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));

        BigDecimal hstRate = new BigDecimal("0.13"); // 13% tax rate

        BigDecimal hstAmount = amount.multiply(hstRate).setScale(2, RoundingMode.HALF_UP);

        // Extract dollars and cents directly from BigDecimal without double conversion
        int dollarsInt = hstAmount.intValue();

        // if hstAmount = 19.53
        // hstAmount.remainder(BigDecimal.ONE) -> 0.53 <- (19.53 % 1)
        // movePointRight(2) -> 53.00
        // intValue() -> 53 (cents)
        int centsInt = hstAmount.remainder(BigDecimal.ONE).movePointRight(2).intValue();

        return new int[] {dollarsInt, centsInt};
    }


    /**
     * returns the total of report including HST amount + Total earned in two weeks
     * @return array of integers, first element is dollars, second is cents
     */
    public int[] getTotalWithHst(){
        // first element is dollar amount, second element is cent amoun
        int[] earned = getTotalEarned();
        int[] hst = getHstAmount();

        // Convert both to total cents
        int totalEarnedCents = (earned[0] * 100) + earned[1];
        int totalHstCents = (hst[0] * 100) + hst[1];

        int totalCents = totalEarnedCents + totalHstCents;

        return new int[] { totalCents / 100, totalCents % 100 };
    }

    /**
     * splits the provided amount into dollars and cents
     * @param amount double object representing the amount of money to split
     * @return array of in where first element is dollar amount and second is cents
     */
    private int[] separateDollarsAndCents(double amount){
        // Math.round fixes binary floating-point imprecision
        // by using the int conversion:
        //          int earnedCents = (int) (earned * 100);
        //  19.99 * 100 can evaluate to 1998.9999999999998, not 1999
        int earnedCents = (int) Math.round(amount * 100.0);

        // separate dollars and cents earned
        int dollars = earnedCents / 100;
        int cents = earnedCents % 100;

        return new int[] {dollars, cents};
    }
}
