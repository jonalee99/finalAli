package edu.brown.cs.student.login;

import edu.brown.cs.student.user.User;
import edu.brown.cs.student.database.DatabaseDAO;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

/**
 * This class accesses the user data from the database.
 */
public final class LoginDatabase {

  private static Connection connection = DatabaseDAO.getConnection();

  private LoginDatabase() {
    //Utility class
  }

  /**
   * This method checks whether a username and password exist in the database.
   * @param username of user
   * @param password of user
   * @return User instance
   * @throws SQLException yeet
   * @throws ClassNotFoundException error
   */
  public static User checkLogin(String username, String password) throws SQLException {

    // Create the query from arguments
    String query = "SELECT * FROM users WHERE username = ? and password = ?";
    PreparedStatement statement = connection.prepareStatement(query);
    statement.setString(1, username);
    statement.setString(2, password);

    // Get the results
    ResultSet result = statement.executeQuery();

    // Create a user variable
    User user = null;

    // If the results return anything, create the user instance
    if (result.next()) {

      // Create a new user instance
      user = new User();

      // Set the name
      user.setFullname(result.getString("name"));

      // Set the username
      user.setUsername(username);

      // Set the password
      user.setPassword(password);
    }

    // Close the result set
    result.close();

    // Return the user instance
    return user;
  }

  /**
   * This creates a new user.
   * @param username of user
   * @param password of user
   * @param name     of user
   * @throws SQLException error
   * @return created user
   */
  public static User createUser(String username, String password, String name) throws SQLException {


    String query = "SELECT * FROM users WHERE username = ?";
    PreparedStatement statement = connection.prepareStatement(query);
    statement.setString(1, username);
    ResultSet rs = statement.executeQuery();
    Boolean bool = rs.next();
    rs.close();
    if (bool) {
      throw new SQLException("Username already in use, please try again...");
    }

    // Create the query from arguments
    query = "INSERT INTO users (username, password, name) VALUES (?, ?, ?)";
    statement = connection.prepareStatement(query);
    statement.setString(1, username);
    statement.setString(2, password);
    statement.setString(3, name);
    statement.executeUpdate();

    // Create a new user instance
    User user = new User();

    // Set the name
    user.setFullname(name);

    // Set the username
    user.setUsername(username);

    // Set the password
    user.setPassword(password);

    return user;
  }
}
