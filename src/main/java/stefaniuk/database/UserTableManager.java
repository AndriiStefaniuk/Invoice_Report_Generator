package stefaniuk.database;

import stefaniuk.data.SignInDto;
import stefaniuk.data.UserRegistrationDto;
import stefaniuk.data.UserSettings;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * dedicated to handle queries related to user table in database
 */
public class UserTableManager {

    private final Connection connection;

    public UserTableManager(Connection connection){
        this.connection = connection;

    }

    /**
     * inserts new row into database's user table, creating new user
     * @param userData populated UserRegistrationDto object representing user settings
     */
    public void signUp(UserRegistrationDto userData){
        if(userData == null){
            return;
        }

        checkConnectionValidity("signUp");

        String sql ="INSERT INTO users (user_name, password, full_name, home_address, hst_number, hourly_rate) " +
                "VALUES(?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            // fills in all ? placeholders in sql String with values
            preparedStatement.setString(1, userData.getUserName());
            preparedStatement.setString(2, userData.getPassword());
            preparedStatement.setString(3, userData.getFullName());
            preparedStatement.setString(4, userData.getHomeAddress());
            preparedStatement.setString(5, userData.getHstNumber());
            preparedStatement.setDouble(6, userData.getHourlyRate());

            int rowsInserted = preparedStatement.executeUpdate();

            if(rowsInserted >= 1){
                System.out.println("UserTableManager -> signUp(): added new user successfully");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * fetches the unique id corresponding to the user.
     * searches for user id based on username and password (provided when you sign in)
     * @param singInData populated SignInDto object containing the username and password
     * @return int representing the id, -1 if no username or password found in the database
     */
    public int getIDbyNameAndPassword(SignInDto singInData){
        if(singInData == null){
            return -1;
        }

        checkConnectionValidity("getIDbyNameAndPassword");

        String sql = "SELECT id FROM users WHERE user_name = ? AND password = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            // fills in all ? placeholders in sql String with values
            preparedStatement.setString(1, singInData.getUserName());
            preparedStatement.setString(2, singInData.getPassword());

            // Executes the query to fetch the stored id of user with provided username and password
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if(resultSet.next()){ // if pointer can move to actual data (if data exists)
                    return resultSet.getInt("id");
                } else{
                    return -1;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * fetches the user settings for user with corresponding id (id passed as argument)
     * @param id int representing the id of user to search the settings for
     * @return populated UserSettings objects containing user settings or null, if no user exists
     */
    public UserSettings fetchUserSettings(int id){
        // getIDbyNameAndPassword() returns id -1 if no user found
        if(id == -1) return null;

        checkConnectionValidity("fetchUserSettings");

        String sql = "SELECT full_name, home_address, hst_number, hourly_rate FROM users WHERE id = ?";

        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)){

            // fills in ? placeholders in sql String with id values
            preparedStatement.setInt(1, id);

            try(ResultSet resultSet = preparedStatement.executeQuery()){
                if(resultSet.next()){ // if pointer can move to actual data (if data exists)

                    return new UserSettings(id,
                            resultSet.getString("full_name"),
                            resultSet.getString("home_address"),
                            resultSet.getString("hst_number"),
                            resultSet.getDouble("hourly_rate"));

                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    /**
     * updates existing row for the user in the users table in database
     * @param userID unique ID for the user for who you change data
     * @param userData populated UserRegistrationDto object representing new data
     */
    public void updateUserSettings(int userID, UserRegistrationDto userData){
        if(userData == null || userID <= 0){
            return;
        }

        checkConnectionValidity("updateUserSettings");

        String sql = "UPDATE users SET user_name = ?, password = ?, full_name = ?," +
                " home_address = ?, hst_number = ?, hourly_rate = ? WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            // fills in ? placeholders in sql String with  values
            preparedStatement.setString(1, userData.getUserName());
            preparedStatement.setString(2, userData.getPassword());
            preparedStatement.setString(3, userData.getFullName());
            preparedStatement.setString(4, userData.getHomeAddress());
            preparedStatement.setString(5, userData.getHstNumber());
            preparedStatement.setDouble(6, userData.getHourlyRate());
            preparedStatement.setInt(7, userID);

            preparedStatement.executeUpdate();

        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }

    /**
     * checks whether the user already exists in the database
     * @return true if user exists in database
     */
    public boolean doesUserExist(UserRegistrationDto userData){
        if(userData == null){
            return false;
        }

        checkConnectionValidity("doesUserExist");

        String sql = "SELECT EXISTS (SELECT 1 FROM users WHERE user_name = ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            // fills in ? placeholders in sql String with values
            preparedStatement.setString(1, userData.getUserName());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                // Moves cursor to the single returned row containing the boolean result
                if (resultSet.next())
                    return resultSet.getBoolean(1); // true if record exists, false if it does not

            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return false;
    }

    /**
     * checks if connection object (connection to database) is valid
     * @param callerMethod String object representing the name of method calling this function NO PARATHESIS
     * @throws RuntimeException if connection is invalid
     */
    private void checkConnectionValidity(String callerMethod) {
        if(!isConnectionValid()){
            throw new RuntimeException(this.getClass().getName() + " -> " + callerMethod + "(): Invalid db connection ");
        }
    }

    private boolean isConnectionValid(){
        try {
            return this.connection != null && !this.connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}


