package stefaniuk.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * container class being sent to client when client changes the settings
 * containing the user settings, such as full name, hstNumber... ot be displayed in the settings panel
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserSettingsResponseDto {

    @JsonProperty("username")
    private String userName;

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("home_address")
    private String homeAddress;

    @JsonProperty("hst_number")
    private String hstNumber;

    @JsonProperty("hourly_rate")
    private double hourlyRate;

    public UserSettingsResponseDto(String userName, UserSettings userSettings){
        this.userName = userName;
        this.fullName = userSettings.getFullName();
        this.homeAddress = userSettings.getHomeAddress();
        this.hstNumber = userSettings.getHstNumber();
        this.hourlyRate = userSettings.getHourlyRate();

    }

    public UserSettingsResponseDto(String userName, String fullName, String homeAddress, String hstNumber, double hourlyRate) {
        this.userName = userName;
        this.fullName = fullName;
        this.homeAddress = homeAddress;
        this.hstNumber = hstNumber;
        this.hourlyRate = hourlyRate;
    }

    public UserSettingsResponseDto(){}

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }

    public String getHstNumber() {
        return hstNumber;
    }

    public void setHstNumber(String hstNumber) {
        this.hstNumber = hstNumber;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }
}
