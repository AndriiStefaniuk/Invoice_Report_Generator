package stefaniuk.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import stefaniuk.data.IncomingReportDataDto;
import stefaniuk.data.SignInDto;
import stefaniuk.data.UserRegistrationDto;
import stefaniuk.exceptions.ReportOverlapException;
import stefaniuk.exceptions.TokenExpiredException;
import stefaniuk.exceptions.UserAlreadyExistsException;
import stefaniuk.exceptions.UserNotFoundException;

import java.time.LocalDate;

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
     */
    public void signUp(String userDataJSON){
        if(!isValidString(userDataJSON)){
            return;
        }
        try {
            UserRegistrationDto userData = objectMapper.readValue(userDataJSON, UserRegistrationDto.class);
            service.signUp(userData);

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
     */
    public void signIn(String signInDataJSON){
        if(!isValidString(signInDataJSON)){
            return;
        }
        try {
            SignInDto userData = objectMapper.readValue(signInDataJSON, SignInDto.class);
            String token = service.signIn(userData);
            //TODO: display the main page to user
            // TODO: return the token to client and return the userSettings to display as well

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
    public void updateUserSettings(String updatedUserDataJSON){
        if(!isValidString(updatedUserDataJSON)){
            return;
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
            service.updateUserSettings(token, updatedUserData);

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
     */
    public void saveReport(String reportDataJSON){
        if(!isValidString(reportDataJSON)){
            return;
        }

        try {
            JsonNode rootNode = objectMapper.readTree(reportDataJSON);
            String token = rootNode.path("token").asText();
            JsonNode reportNode = rootNode.get("report_data");
            IncomingReportDataDto reportData = objectMapper.treeToValue(reportNode, IncomingReportDataDto.class);

            service.saveReport(token, reportData);

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
     */
    public void updateReport(String updatedReportJSON){
        if(!isValidString(updatedReportJSON)){
            return;
        }

        try {
            JsonNode rootNode = objectMapper.readTree(updatedReportJSON);
            String token = rootNode.path("token").asText();
            LocalDate initialDate = objectMapper.convertValue(rootNode.path("initial_start_date"), LocalDate.class);
            JsonNode reportNode = rootNode.get("updated_report_data");
            IncomingReportDataDto updatedData = objectMapper.treeToValue(reportNode, IncomingReportDataDto.class);

            service.updateReport(token, initialDate, updatedData);

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
     */
    public void deleteReport(String incomingDataJSON){
        if(!isValidString(incomingDataJSON)){
            return;
        }

        try{
            JsonNode rootNode = objectMapper.readTree(incomingDataJSON);
            String token = rootNode.path("token").asText();
            LocalDate periodStartDate = objectMapper.convertValue(rootNode.path("report_start_date"), LocalDate.class);

            service.deleteReport(token, periodStartDate);

        } catch (JsonProcessingException e) {
            // TODO: notify client that the JSON format is invalid (HTTP 400)
            throw new RuntimeException(e);
        } catch (TokenExpiredException e){
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
     * redirects the request to fetch all stored reports for the user to Service class
     * @param tokenJSON String object in JSON format containing token value
     */
    public void getAllReportList(String tokenJSON){
        if(!isValidString(tokenJSON)){
            return;
        }

        try {
            JsonNode rootNode = objectMapper.readTree(tokenJSON);
            String token = rootNode.path("token").asText();

            // TODO return the list off all reports as JSON
            service.getAllReportList(token);

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
