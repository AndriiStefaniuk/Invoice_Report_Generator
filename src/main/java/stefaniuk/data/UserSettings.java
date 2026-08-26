package stefaniuk.data;

/**
 * container class storing user data, such as name, HST number, hourly rate...
 */
public class UserSettings {

    private int id;
    private String fullName;
    private String homeAddress;
    private String hstNumber;
    private double hourlyRate;


    public UserSettings(int id, String fullName, String homeAddress, String hstNumber, double hourlyRate) {
        this.id = id;
        this.fullName = fullName;
        this.homeAddress = homeAddress;
        this.hstNumber = hstNumber;
        this.hourlyRate = hourlyRate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    @Override
    public String toString() {
        return "UserSettings{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", homeAddress='" + homeAddress + '\'' +
                ", hstNumber='" + hstNumber + '\'' +
                ", hourlyRate=" + hourlyRate +
                '}';
    }
}
