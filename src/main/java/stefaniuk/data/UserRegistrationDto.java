package stefaniuk.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * container class representing user data when creating new account
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRegistrationDto {

    @JsonProperty("user_name")
    private String userName;

    @JsonProperty("password")
    private String password;

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("home_address")
    private String homeAddress;

    @JsonProperty("hst_number")
    private String hstNumber;

    @JsonProperty("hourly_rate")
    private double hourlyRate;

    /**
     * only for testing purposes, DON'T USE in actual coding
     */
    public UserRegistrationDto(String userName, String password, String fullName, String homeAddress, String hstNumber, double hourlyRate) {
        this.userName = userName;
        this.password = password;
        this.fullName = fullName;
        this.homeAddress = homeAddress;
        this.hstNumber = hstNumber;
        this.hourlyRate = hourlyRate;
    }

    public UserRegistrationDto() {}

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
