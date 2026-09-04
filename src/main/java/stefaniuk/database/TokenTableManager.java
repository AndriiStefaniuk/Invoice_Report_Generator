package stefaniuk.database;


import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;


/**
 * used to deal with token table from database
 */
public class TokenTableManager {

    private Connection connection;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private final String alphaNumericCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private final int tokenLength = 32;

    
    public TokenTableManager(Connection connection) {
        this.connection = connection;
    }


    /**
     * creates a new row in tokens table
     * @param token  String object representing random set of alphaNumeric Characters related to specific user (encrypted userId)
     * @param userId int object representing user identifier in database, not encrypted
     */
    public void insertToken(String token, int userId) {
        checkConnectionValidity("insertToken");

        if (!isTokenValid(token) || userId <= 0) {
            throw new IllegalArgumentException("TokenTableManager -> insertToken(): invalid arguments: \n"
                    + "  - token: " + token + "  userID:" + userId);
        }

        String sql = "INSERT INTO tokens (token, user_id) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, token);
            preparedStatement.setInt(2, userId);

            if (preparedStatement.executeUpdate() == 1) {
                System.out.println("TokenTableManager -> insertToken(): inserted new token successfully");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * converts token to userId.
     * searches the userId in tokens table based on token passed in as an argument
     *
     * @param token String object token given by client MUST BE:
     *              - 32 characters long
     *              - contain only English upper and lower case letters and numbers
     *              - not expired
     * @return int object representing the userId (user identifier in database)
     * or returns 0 if no match found in database
     */
    public int getUserId(String token) {
        checkConnectionValidity("getUserId");

        if (!isTokenValid(token)) {
            return 0;
        }
        if(isTokenExpired(token)){
            throw new IllegalStateException("TokenTableManager -> getUserId(): token expired. ask user to sign in again");
        }

        String sql = "SELECT user_id FROM tokens WHERE token = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setString(1, token);

            try(ResultSet resultSet = preparedStatement.executeQuery()) {

                if (resultSet.next()) {
                    return resultSet.getInt("user_id");
                }
            }
        } catch (SQLException e) {
            return 0;
        }
        return 0;
    }


    /**
     * scans the tokens table in database and deletes expired tokens from the table
     */
    public void cleanUpExpiredTokens() {
        checkConnectionValidity("CleanUpExpiredTokens");

        String sql = "DELETE FROM tokens WHERE expires_at <= NOW()";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)){

            int rowsDeleted = preparedStatement.executeUpdate();
            System.out.println("TokenTableManager -> CleanUpExpiredTokens(): deleted " + rowsDeleted + " expired rows");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Generates a  random alphanumeric string 32 characters long
     */
    public String generateRandomString() {
        // generates a 32-character unique string (letters and numbers)
        return UUID.randomUUID().toString().replace("-", "");
    }


    /**
     * Checks whether token meets the following requirements:
     * - 32 characters long
     * - contain only English upper and lower case letters and numbers
     *
     * @param token String object representing the token given by client
     * @return true if token is valid
     */
    private boolean isTokenValid(String token) {
        if (token == null || token.length() != tokenLength) {
            return false;
        }

        for (char c : token.toCharArray()) {
            if (!alphaNumericCharacters.contains(String.valueOf(c))) {
                return false;
            }
        }
        return true;
    }

    /**
     * checks if the token is expired
     * @param token String object representing the token given by client
     * @return true if token is expired
     */
    private boolean isTokenExpired(String token){
        checkConnectionValidity("isTokenExpired");
        if(!isTokenValid(token)){
            return true;
        }

        String sql = "SELECT expires_at FROM tokens WHERE token = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setString(1, token);

            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                LocalDateTime expiryDate = resultSet.getTimestamp("expires_at").toLocalDateTime();
                return expiryDate.isBefore(LocalDateTime.now(ZoneOffset.UTC));
            }
        } catch (SQLException e) {
            return false; // token not found, this class checks if it is expired, not if it found or not
        }
        return false; // token not found, this class checks if it is expired, not if it found or not
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

    private boolean isConnectionValid () {
        try {
            return this.connection != null && !this.connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

}



