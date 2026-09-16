package stefaniuk.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import stefaniuk.data.*;
import stefaniuk.exceptions.ReportOverlapException;
import stefaniuk.exceptions.TokenExpiredException;
import stefaniuk.exceptions.UserAlreadyExistsException;
import stefaniuk.exceptions.UserNotFoundException;

import java.time.LocalDate;
import java.util.List;

/**
 * end point class used to handle incoming requests from user and redirect them to Service class (class executor)
 */
public class RequestHandler {

    private final Service service;
    private ObjectMapper objectMapper;


    public RequestHandler(Service service) {
        this.service = service;
        configureObjectMapper();
    }

    /**
     * redirects the request to sign up to Service class
     * @param userDataJSON String object in JSON format containing UserRegistrationDto object
     * @return SignInResponseDto object representing the token (generated for the user) and user settings
     */
    public SignInResponseDto signUp(String userDataJSON){
        if(!isValidString(userDataJSON)){
            return null;
        }
        try {
            UserRegistrationDto userData = objectMapper.readValue(userDataJSON, UserRegistrationDto.class);
            return service.signUp(userData);

        } catch (JsonProcessingException e) {
            // TODO: notify client that the JSON format is invalid (HTTP 400)
            throw new RuntimeException(e);
        } catch(UserAlreadyExistsException e){
            //TODO notify front end that user already exists
            throw new RuntimeException(e);
        } catch (IllegalArgumentException e){
            // TODO notify userData probably contains invalid (probably empty or null) fields
            throw new RuntimeException(e);
        } catch (TokenExpiredException e){
            // TODO suggest to sign in again
            throw new RuntimeException(e);
        } catch (Exception e) {
            // TODO: notify client "Server is currently unavailable, please try again later" (HTTP 500)
            throw new RuntimeException(e);
        }

    }

    /**
     * redirects the request to sign in to Service class
     * @param signInDataJSON String object in JSON format containing SignInDto object
     * @return SignInResponseDto object representing the token (generated for the user) and user settings
     */
    public SignInResponseDto signIn(String signInDataJSON){
        if(!isValidString(signInDataJSON)){
            return null;
        }
        try {
            SignInDto signInData = objectMapper.readValue(signInDataJSON, SignInDto.class);
            return service.signIn(signInData);
            //TODO: display the main page to user

        } catch (JsonProcessingException e) {
            // TODO: notify client that the JSON format is invalid (HTTP 400)
            throw new RuntimeException(e);
        } catch(IllegalArgumentException e){
            // TODO notify that typed either username or password is invalid on client side
            throw new RuntimeException(e);
        } catch (UserNotFoundException e){
            // TODO suggest to sign up first
            throw new RuntimeException(e);
        } catch (Exception e) {
            // TODO: notify client "Server is currently unavailable, please try again later" (HTTP 500)
            throw new RuntimeException(e);
        }
    }


    /**
     * redirects the request to update user settings
     * @param updatedUserDataJSON String object in JSON format containing user's token and
     *                            the UserRegistrationDto containing the updated user data
     */
    public UserSettingsResponseDto updateUserSettings(String updatedUserDataJSON){
        if(!isValidString(updatedUserDataJSON)){
            return null;
        }

        String token;
        UserRegistrationDto updatedUserData;

        // extract the token and the UserRegistrationDto objects
        try {
            JsonNode rootNode = objectMapper.readTree(updatedUserDataJSON);
            token = rootNode.path("token").asText();

            JsonNode updatedUserSettingsNode = rootNode.get("updated_settings");
            updatedUserData = objectMapper.treeToValue(updatedUserSettingsNode, UserRegistrationDto.class);

        } catch (JsonProcessingException e) {
            // TODO: notify client that the JSON format is invalid (HTTP 400)
            throw new RuntimeException(e);
        }

        // update the data in the database
        try {
            // TODO send updated the settings to client
            return service.updateUserSettings(token, updatedUserData);

        } catch(TokenExpiredException e){
            // TODO suggest to sign in again
            throw new RuntimeException(e);
        } catch(IllegalArgumentException e){
            //TODO display the pop up that failed to update settings
            throw new RuntimeException(e);
        } catch (Exception e) {
            // TODO: notify client "Server is currently unavailable, please try again later" (HTTP 500)
            throw new RuntimeException(e);
        }
    }

    /**
     * redirects the request to save a new report to the Service class
     * @param reportDataJSON String object in JSON format containing token and IncomingReportDataDto
     * @return Boolean object representing whether the saving operation was successful or not
     */
    public boolean saveReport(String reportDataJSON){
        if(!isValidString(reportDataJSON)){
            return false;
        }

        try {
            JsonNode rootNode = objectMapper.readTree(reportDataJSON);
            String token = rootNode.path("token").asText();
            JsonNode reportNode = rootNode.get("report_data");
            IncomingReportDataDto reportData = objectMapper.treeToValue(reportNode, IncomingReportDataDto.class);

            service.saveReport(token, reportData);
            return true;

        } catch (JsonProcessingException e) {
            // TODO: notify client that the JSON format is invalid (HTTP 400)
            throw new RuntimeException(e);
        } catch (TokenExpiredException e) {
            // TODO suggest to sign in again
            throw new RuntimeException(e);
        } catch (ReportOverlapException e) {
            // TODO notify client that chosen period overlaps with existing reports
            throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
            // TODO display generic error that saving report failed
            throw new RuntimeException(e);
        } catch (Exception e) {
            // TODO: notify client "Server is currently unavailable, please try again later" (HTTP 500)
            throw new RuntimeException(e);
        }
    }



    /**
     * redirects the request to update an existing report to the Service class
     * @param updatedReportJSON String object in JSON format containing token, initial date, and updated report
     * @return boolean representing whether the updating operation was successful or not
     */
    public boolean updateReport(String updatedReportJSON){
        if(!isValidString(updatedReportJSON)){
            return false;
        }

        try {
            JsonNode rootNode = objectMapper.readTree(updatedReportJSON);
            String token = rootNode.path("token").asText();
            LocalDate initialDate = objectMapper.convertValue(rootNode.path("initial_start_date"), LocalDate.class);
            JsonNode reportNode = rootNode.get("updated_report_data");
            IncomingReportDataDto updatedData = objectMapper.treeToValue(reportNode, IncomingReportDataDto.class);

            service.updateReport(token, initialDate, updatedData);

            return true;

        } catch (JsonProcessingException e) {
            // TODO: notify client that the JSON format is invalid (HTTP 400)
            throw new RuntimeException(e);
        } catch (TokenExpiredException e) {
            // TODO suggest to sign in again
            throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
            // TODO display pop up that failed to update report
            throw new RuntimeException(e);
        } catch (Exception e) {
            // TODO: notify client "Server is currently unavailable, please try again later" (HTTP 500)
            throw new RuntimeException(e);
        }
    }


    /**
     * redirects the request to delete an existing report to the Service class
     * @param incomingDataJSON String object in JSON format containing token and the start date of report to delete
     * @return boolean representing whether the deletion operation was successful or not
     */
    public boolean deleteReport(String incomingDataJSON){
        if(!isValidString(incomingDataJSON)){
            return false;
        }

        try{
            JsonNode rootNode = objectMapper.readTree(incomingDataJSON);
            String token = rootNode.path("token").asText();
            LocalDate periodStartDate = objectMapper.convertValue(rootNode.path("report_start_date"), LocalDate.class);

            service.deleteReport(token, periodStartDate);
            return true;

        } catch (JsonProcessingException e) {
            // TODO: notify client that the JSON format is invalid (HTTP 400)
            throw new RuntimeException(e);
        } catch (TokenExpiredException e){
            // TODO suggest to sign in again
            throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
            // TODO display pop up that failed to delete report
            throw new RuntimeException(e);
        } catch (Exception e) {
            // TODO: notify client "Server is currently unavailable, please try again later" (HTTP 500)
            throw new RuntimeException(e);
        }
    }

    /**
     * redirects the request to fetch all stored reports for the user to Service class
     *
     * @param tokenJSON String object in JSON format containing token value
     * @return List of OutgoingReportDataDto objects representing all reports stored in the database for specific user
     */
    public List<OutgoingReportDataDto> getAllReportList(String tokenJSON){
        if(!isValidString(tokenJSON)){
            return null;
        }

        try {
            JsonNode rootNode = objectMapper.readTree(tokenJSON);
            String token = rootNode.path("token").asText();

            // TODO return the list off all reports as JSON
            return service.getAllReportList(token);

        } catch (JsonProcessingException e) {
            // TODO: notify client that the JSON format is invalid (HTTP 400)
            throw new RuntimeException(e);
        } catch (TokenExpiredException e){
            // TODO suggest to sign in again
            throw new RuntimeException(e);
        } catch (Exception e) {
            // TODO: notify client "Server is currently unavailable, please try again later" (HTTP 500)
            throw new RuntimeException(e);
        }
    }


    /**
     * configures ObjectMapper object, providing some basic settings, such as
     * - registering JavaTimeModule to handle dates and time correctly
     * - ignoring unknown properties from JSON response
     * - indented output for easier reading
     */
    private void configureObjectMapper(){
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    /**
     * checks if string is not null and not blank
     * @param string String object to validate
     * @return true if string is not null and not blank
     */
    private boolean isValidString(String string){
        return string != null && !string.isBlank();
    }
}
