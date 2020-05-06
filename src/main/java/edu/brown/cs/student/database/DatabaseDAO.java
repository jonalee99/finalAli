package edu.brown.cs.student.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;

/**
 * This class connects to and creates databases.
 */
public final class DatabaseDAO {

  private static Connection connection = null;

  private DatabaseDAO() {
    //utility class
  }

  /**
   * Returns whether it is connected or not.
   *
   * @return boolean
   */
  public static boolean isConnected() {
    return connection != null;
  }

  /**
   * This function returns the connecte database.
   *
   * @return connection
   */
  public static Connection getConnection() {
    return connection;
  }

  /**
   * It connects the class to our database. It initializes the private variable
   * "connection", which will be used in subsequent queries.
   *
   * @param fileName the specific file that we want to run Dijkstra's on
   * @throws ClassNotFoundException class not found
   * @throws SQLException           uh o h
   **/
  public static void connect(String fileName) throws ClassNotFoundException, SQLException {

    // Check if the file exists
    File file = new File(fileName);
    if (!file.exists()) {
      throw new SQLException();
    }

    // This loads the connection
    Class.forName("org.sqlite.JDBC");
    String urlToDB = "jdbc:sqlite:" + fileName;
    connection = DriverManager.getConnection(urlToDB);

    // Enforce foreign keys
    Statement stat = connection.createStatement();
    stat.executeUpdate("PRAGMA foreign_keys=ON;");
  }

  /**
   * This method creates a new database with the given name inside the data
   * folder and connects the connection to it.
   *
   * @param name to describe the database
   * @throws SQLException           uh oh
   * @throws ClassNotFoundException stinky
   */
  public static void createDatabase(String name) throws SQLException, ClassNotFoundException {

    // This loads the connection
    Class.forName("org.sqlite.JDBC");
    String urlToDB = "jdbc:sqlite:data/" + name + ".sqlite3";
    connection = DriverManager.getConnection(urlToDB);

    // Create the tables
    PreparedStatement statement = connection.prepareStatement(
        "CREATE TABLE IF NOT EXISTS users ( "
        + "username TEXT NOT NULL, "
        + "password TEXT NOT NULL, "
        + "name TEXT NOT NULL, "
        + "PRIMARY KEY(username) "
        + ");");
    statement.executeUpdate();

    statement = connection.prepareStatement(
        "CREATE TABLE IF NOT EXISTS inventory ( "
        + "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
        + "username TEXT NOT NULL, "
        + "food TEXT NOT NULL, "
        + "type TEXT NOT NULL, "
        + "unit TEXT, "
        + "quantity FLOAT NOT NULL, "
        + "expiration INTEGER NOT NULL, "
        + "FOREIGN KEY(username) REFERENCES users(username)"
        + ");");
    statement.executeUpdate();

    statement = connection.prepareStatement(
        "CREATE TABLE IF NOT EXISTS recipes ( "
        + "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
        + "title TEXT NOT NULL, "
        + "description TEXT NOT NULL, "
        + "url TEXT NOT NULL"
        + ");");
    statement.executeUpdate();

    statement = connection.prepareStatement(
        "CREATE TABLE IF NOT EXISTS ingredients ( "
        + "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
        + "food TEXT NOT NULL, "
        + "type TEXT NOT NULL, "
        + "quantity FLOAT, "
        + "unit TEXT"
        + ");");
    statement.executeUpdate();

    statement = connection.prepareStatement(
        "CREATE TABLE IF NOT EXISTS recipes_ingredients ( "
        + "recipeId INTEGER NOT NULL , "
        + "ingredientId INTEGER NOT NULL"
        + ");");
    statement.executeUpdate();

    statement = connection.prepareStatement(
        "CREATE TABLE IF NOT EXISTS category ( "
        + "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
        + "category TEXT NOT NULL, "
        + "recipeId INTEGER NOT NULL, "
        + "FOREIGN KEY(recipeId) REFERENCES recipes(id)"
        + ");");
    statement.executeUpdate();
  }
}
