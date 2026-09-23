package stefaniuk.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;

/**
 * container class containing data received from client
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class IncomingReportDataDto {

    @JsonProperty("site_address_array")
    private List<String> siteAddresses;

    @JsonProperty("period_start_date")
    private LocalDate periodStartDate;

    @JsonProperty("first_week_hours")
    private double firstWeekHours;

    @JsonProperty("second_week_hours")
    private double secondWeekHours;

    /**
     * only for testing purposes, DON'T USE in actual coding
     */
    public IncomingReportDataDto(List<String> siteAddresses, LocalDate periodStartDate, double firstWeekHours, double secondWeekHours) {
        this.siteAddresses = siteAddresses;
        this.periodStartDate = periodStartDate;
        this.firstWeekHours = firstWeekHours;
        this.secondWeekHours = secondWeekHours;
    }

    public IncomingReportDataDto() {}

    public List<String> getSiteAddresses() {
        return siteAddresses;
    }

    public void setSiteAddresses(List<String> siteAddresses) {
        this.siteAddresses = siteAddresses;
    }

    /**
     * returns raw date indicating beginning of report period of two weeks, set by user.
     * is not guaranteed to be the Monday, can be any day of week.
     * (each report starts from Monday and ends on Saturday)
     * @return LocalDate object entered by user, NOT START OF REPORT PERIOD
     */
    public LocalDate getPeriodStartDate() {
        return periodStartDate;
    }

    public void setPeriodStartDate(LocalDate periodStartDate) {
        this.periodStartDate = periodStartDate;
    }

    public double getFirstWeekHours() {
        return firstWeekHours;
    }

    public void setFirstWeekHours(double firstWeekHours) {
        this.firstWeekHours = firstWeekHours;
    }

    public double getSecondWeekHours() {
        return secondWeekHours;
    }

    public void setSecondWeekHours(double secondWeekHours) {
        this.secondWeekHours = secondWeekHours;
    }
}
