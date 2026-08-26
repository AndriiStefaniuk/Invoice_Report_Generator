package stefaniuk.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;

/**
 * container class containing data received from client
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReportDataDto {

    @JsonProperty("site_address_array")
    private List<String> siteAddressArray;

    @JsonProperty("period_start_date")
    private LocalDate periodStartDate;

    @JsonProperty("first_week_hours")
    private double firstWeekHours;

    @JsonProperty("second_week_hours")
    private double secondWeekHours;


    public ReportDataDto() {

    }

    public List<String> getSiteAddressArray() {
        return siteAddressArray;
    }

    public void setSiteAddressArray(List<String> siteAddressArray) {
        this.siteAddressArray = siteAddressArray;
    }

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
