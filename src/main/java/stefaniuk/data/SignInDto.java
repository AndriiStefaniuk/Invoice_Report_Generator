package stefaniuk.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * container class representing data received from client when client signs in
 * contains only username and password
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SignInDto {

    @JsonProperty("user_name")
    private String userName;

    @JsonProperty("password")
    private String password;

    /**
     * only for testing purposes, DON'T USE in actual coding
     */
    public SignInDto(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public SignInDto() {}

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
}
