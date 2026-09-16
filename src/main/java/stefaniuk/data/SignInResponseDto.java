package stefaniuk.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * container class which is being sent to user when user signs in
 * containing the token and user settings (to display in settings for client)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SignInResponseDto {

    // contains user settings that will be displayed for user such as username, full name, hst number...
    @JsonProperty("user_settings")
    private UserSettingsResponseDto userSettings;

    @JsonProperty("token")
    private String token;


    public SignInResponseDto(UserSettingsResponseDto userSettings, String token) {
        this.userSettings = userSettings;
        this.token = token;
    }

    public SignInResponseDto(){}

    public UserSettingsResponseDto getUserSettings() {
        return userSettings;
    }

    public void setUserSettings(UserSettingsResponseDto userSettings) {
        this.userSettings = userSettings;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
