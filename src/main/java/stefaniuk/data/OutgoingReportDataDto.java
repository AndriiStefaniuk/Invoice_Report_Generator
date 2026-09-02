package stefaniuk.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;

/**
 * container class containing report data being sent to client
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OutgoingReportDataDto {

    @JsonProperty("id")
    private int id;

    @JsonProperty("site_address_array")
    private List<String> siteAddressArray;

    @JsonProperty("first_week_start_date")
    private LocalDate firstWeekStartDate;

    @JsonProperty("first_week_end_date")
    private LocalDate firstWeekEndDate;

    @JsonProperty("first_week_hours")
    private double firstWeekHours;

    @JsonProperty("second_week_start_date")
    private LocalDate secondWeekStartDate;

    @JsonProperty("second_week_end_date")
    private LocalDate secondWeekEndDate;

    @JsonProperty("second_week_hours")
    private double secondWeekHours;

    public OutgoingReportDataDto(int id, List<String> siteAddressArray, LocalDate firstWeekStartDate,
                                 LocalDate firstWeekEndDate, double firstWeekHours, LocalDate secondWeekStartDate,
                                 LocalDate secondWeekEndDate, double secondWeekHours) {
        this.id = id;
        this.siteAddressArray = siteAddressArray;
        this.firstWeekStartDate = firstWeekStartDate;
        this.firstWeekEndDate = firstWeekEndDate;
        this.firstWeekHours = firstWeekHours;
        this.secondWeekStartDate = secondWeekStartDate;
        this.secondWeekEndDate = secondWeekEndDate;
        this.secondWeekHours = secondWeekHours;
    }

    public OutgoingReportDataDto() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<String> getSiteAddressArray() {
        return siteAddressArray;
    }

    public void setSiteAddressArray(List<String> siteAddressArray) {
        this.siteAddressArray = siteAddressArray;
    }

    public LocalDate getFirstWeekStartDate() {
        return firstWeekStartDate;
    }

    public void setFirstWeekStartDate(LocalDate firstWeekStartDate) {
        this.firstWeekStartDate = firstWeekStartDate;
    }

    public LocalDate getFirstWeekEndDate() {
        return firstWeekEndDate;
    }

    public void setFirstWeekEndDate(LocalDate firstWeekEndDate) {
        this.firstWeekEndDate = firstWeekEndDate;
    }

    public double getFirstWeekHours() {
        return firstWeekHours;
    }

    public void setFirstWeekHours(double firstWeekHours) {
        this.firstWeekHours = firstWeekHours;
    }

    public LocalDate getSecondWeekStartDate() {
        return secondWeekStartDate;
    }

    public void setSecondWeekStartDate(LocalDate secondWeekStartDate) {
        this.secondWeekStartDate = secondWeekStartDate;
    }

    public LocalDate getSecondWeekEndDate() {
        return secondWeekEndDate;
    }

    public void setSecondWeekEndDate(LocalDate secondWeekEndDate) {
        this.secondWeekEndDate = secondWeekEndDate;
    }

    public double getSecondWeekHours() {
        return secondWeekHours;
    }

    public void setSecondWeekHours(double secondWeekHours) {
        this.secondWeekHours = secondWeekHours;
    }
}
