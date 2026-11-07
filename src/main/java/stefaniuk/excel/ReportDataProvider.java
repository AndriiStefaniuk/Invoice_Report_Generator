package stefaniuk.excel;

import stefaniuk.data.IncomingReportDataDto;
import stefaniuk.data.UserSettings;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

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
     * formats the hours to be shown as 00:00, not as fractions
     * @param decimalHours double object representing hours as decimal
     *                     Example: 30 min = 0.5
     * @return String object representing formated hours as 00:00
     */
    private String formatHoursToTime(double decimalHours) {
        if (Double.isNaN(decimalHours) || decimalHours <= 0.0) {
            return "00:00";
        }
        long totalMinutes = Math.round(decimalHours * 60.0);

        long hours = totalMinutes / 60;
        long minutes = totalMinutes % 60;

        return String.format("%02d:%02d", hours, minutes);
    }


    /**
     * @return String object containing the number of hours
     */
    public String getFirstWeekHours(){
        return formatHoursToTime(reportData.getFirstWeekHours());
    }

    /**
     * @return String object containing the number of hours
     */
    public String getSecondWeekHours(){
        return formatHoursToTime(reportData.getSecondWeekHours());
    }


    /**
     * @return String object containing the total number of hours
     */
    public String getTotalHours(){
        double totalHours = reportData.getFirstWeekHours() + reportData.getSecondWeekHours();
        return formatHoursToTime(totalHours);
    }

    public String getHourlyRate() {
        return String.format(Locale.CANADA, "%.2f", getHourlyRateAsBigDecimal());
    }


    /**
     * Converts the user's hourly rate into a BigDecimal.
     *
     * @return BigDecimal representation of the hourly rate
     */
    private BigDecimal getHourlyRateAsBigDecimal() {
        return BigDecimal.valueOf(userSettings.getHourlyRate());
    }

    /**
     * Converts decimal hours into a BigDecimal.
     *
     * @param hours worked hours as a decimal
     * @return BigDecimal representation of the hours
     */
    private BigDecimal getHoursAsBigDecimal(double hours) {
        return BigDecimal.valueOf(hours);
    }

    /**
     * Splits a monetary amount into dollars and two-digit cents.
     *
     * @param amount monetary value
     * @return two-element array: [0] = dollars, [1] = two-digit cents
     */
    private String[] toDollarsAndCents(BigDecimal amount) {
        BigDecimal scaled = amount.setScale(2, RoundingMode.HALF_UP);
        int dollars = scaled.intValue();
        int cents = scaled.remainder(BigDecimal.ONE).movePointRight(2).intValue();
        return new String[]{ String.valueOf(dollars), String.format(Locale.CANADA, "%02d", cents) };
    }



    /**
     * Calculates earnings for a given number of hours rounded to standard currency scale.
     *
     * @param hours worked hours
     * @return rounded BigDecimal amount
     */
    private BigDecimal calculateEarnedAmount(double hours) {
        return getHourlyRateAsBigDecimal()
                .multiply(getHoursAsBigDecimal(hours))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public String[] getFirstWeekEarned() {
        BigDecimal earned = calculateEarnedAmount(reportData.getFirstWeekHours());
        return toDollarsAndCents(earned);
    }

    public String[] getSecondWeekEarned() {
        BigDecimal earned = calculateEarnedAmount(reportData.getSecondWeekHours());
        return toDollarsAndCents(earned);
    }

    /**
     * Returns the total earned in two weeks as the exact sum of both line items,
     * preventing off-by-a-penny invoice display discrepancies.
     *
     * @return array of Strings: index 0 is dollars, index 1 is two-digit cents
     */
    public String[] getTotalEarned() {
        BigDecimal week1 = calculateEarnedAmount(reportData.getFirstWeekHours());
        BigDecimal week2 = calculateEarnedAmount(reportData.getSecondWeekHours());
        BigDecimal totalEarned = week1.add(week2);
        return toDollarsAndCents(totalEarned);
    }

    /**
     * Returns the HST amount (13% of earned total).
     *
     * @return array of Strings: index 0 is dollars, index 1 is two-digit cents
     */
    public String[] getHstAmount() {
        BigDecimal week1 = calculateEarnedAmount(reportData.getFirstWeekHours());
        BigDecimal week2 = calculateEarnedAmount(reportData.getSecondWeekHours());
        BigDecimal totalEarned = week1.add(week2);

        BigDecimal hstRate = new BigDecimal("0.13");
        BigDecimal hstAmount = totalEarned.multiply(hstRate).setScale(2, RoundingMode.HALF_UP);

        return toDollarsAndCents(hstAmount);
    }

    /**
     * Returns the grand total (earned subtotal + HST).
     *
     * @return array of Strings: index 0 is dollars, index 1 is two-digit cents
     */
    public String[] getTotalWithHst() {
        BigDecimal week1 = calculateEarnedAmount(reportData.getFirstWeekHours());
        BigDecimal week2 = calculateEarnedAmount(reportData.getSecondWeekHours());
        BigDecimal totalEarned = week1.add(week2);

        BigDecimal hstRate = new BigDecimal("0.13");
        BigDecimal hstAmount = totalEarned.multiply(hstRate).setScale(2, RoundingMode.HALF_UP);

        BigDecimal grandTotal = totalEarned.add(hstAmount);

        return toDollarsAndCents(grandTotal);
    }
}
