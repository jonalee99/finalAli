package edu.brown.cs.student.user;

import edu.brown.cs.student.database.DatabaseDAO;
import edu.brown.cs.student.recipe.Food;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.sql.PreparedStatement;

/**
 * This handles the user database queries.
 */
public final class UserDatabase {

  private UserDatabase() {
    //Utility class
  }

  /**
   * This method creates a new inventory item.
   * @param food string
   * @param quantity integer
   * @param expiration dd/MM/yyyy
   * @param username username
   * @param type type
   * @param unit units
   * @throws SQLException error
   */
  public static void createInventoryItem(String username, String food,
      double quantity, String expiration, String type, String unit)
      throws SQLException {

    // Create the query from arguments
    String query = "INSERT INTO inventory (username, food, quantity, expiration, type, unit) "
        + "VALUES (?, ?, ?, ?, ?, ?)";
    PreparedStatement statement = DatabaseDAO.getConnection().prepareStatement(query);
    statement.setString(1, username);
    statement.setString(2, food);
    statement.setDouble(3, quantity);
    statement.setString(4, expiration);
    statement.setString(5, type);
    statement.setString(6, unit);
    statement.executeUpdate();
  }

  /**
   * This method deletes an item.
   * @param id of food
   * @param quantity removed
   * @param username of logged user
   * @throws SQLException stinky
   */
  public static void deleteItem(Integer id, double quantity, String username)
      throws SQLException {

    // Check how many of the given item exist
    // Create the query from arguments
    String query = "SELECT * FROM inventory WHERE id = ?";
    PreparedStatement statement = DatabaseDAO.getConnection().prepareStatement(query);
    statement.setInt(1, id);

    // Get the results
    ResultSet result = statement.executeQuery();

    // Store the quantity
    Double currQuant = null;

    // Loop through the results
    while (result.next()) {

      // Get the name, quantity, and expiration date.
      currQuant = result.getDouble("quantity");
    }

    // Close the result set
    result.close();
    statement.close();

    // If the user deletes all the food, delete it. Otherwise, update it
    if (quantity >= currQuant) {
      query = "DELETE FROM inventory WHERE id = ? AND username = ?";
      PreparedStatement statement1 = DatabaseDAO.getConnection().prepareStatement(query);
      statement1.setInt(1, id);
      statement1.setString(2, username);
      statement1.executeUpdate();
      statement1.close();
    } else {
      query = "UPDATE inventory SET quantity = ? WHERE id = ? AND username = ?";
      PreparedStatement statement1 = DatabaseDAO.getConnection().prepareStatement(query);
      statement1.setDouble(1, currQuant - quantity);
      statement1.setInt(2, id);
      statement1.setString(3, username);
      statement1.executeUpdate();
      statement1.close();
    }
  }

  /**
   * This method returns all the inventory expiring before a given date.
   *
   * @param user logged in user
   * @return foodQueue sorted on days until expiration
   * @throws SQLException error
   */
  public static Queue<Food> getExpiry(User user) throws SQLException {

    // Create the query from arguments
    String query = "SELECT * FROM inventory WHERE username = ?";
    PreparedStatement statement = DatabaseDAO.getConnection().prepareStatement(query);
    statement.setString(1, user.getUsername());

    // Get the results
    ResultSet result = statement.executeQuery();

    // This stores the results in a priority queue based on days till expiration
    final int queueSize = 11;
    Queue<Food> foodQueue = new PriorityQueue<Food>(queueSize, new ExpiryComparator());

    // Get the current date
    Date currentDate = java.sql.Date.valueOf(LocalDate.now());

    // Loop through the results
    // If the results return anything, create the user instance
    while (result.next()) {

      // Get the name, quantity, and expiration date.
      Integer id = result.getInt("id");
      String foodName = result.getString("food");
      String type = result.getString("type");
      Double foodQuant = result.getDouble("quantity");
      String unit = result.getString("unit");
      Date foodExpir = null;
      try {
        foodExpir = new SimpleDateFormat("dd/MM/yyyy").parse(result.getString("expiration"));
      } catch (ParseException e) {
        throw new SQLException(e.getCause());
      }

      // Add it to the foodqueue
      foodQueue.add(new Food(foodName, foodQuant, unit, daysBetween(currentDate, foodExpir),
          type, id));
    }

    // Close the result set
    result.close();

    // Return the foodQueue, sorted by days 'till expiration
    return foodQueue;

  }
  /**
   * Checks whether or not a given ingredient is in the users inventory.
   * @param ingredient to be checked
   * @param user who's inventory is being searched
   * @return boolean saying whether or not item is in inventory.
   * @throws SQLException uh oh
   */
  public static boolean inInventory(String ingredient, User user) throws SQLException {
    String query = "SELECT * FROM inventory WHERE username = ? AND food = ?";
    PreparedStatement statement = DatabaseDAO.getConnection().prepareStatement(query);
    statement.setString(1, user.getUsername());
    statement.setString(2, ingredient);
    ResultSet rs = statement.executeQuery();
    Boolean bool = rs.next();
    rs.close();
    return bool;
  }

  /**
   * Give two dates, gives the days in between.
   * @param target earlier date
   * @param expiration later date
   * @return double of days between
   */
  private static double daysBetween(Date target, Date expiration) {
    double days = 0.0;
    try {
      long difference = expiration.getTime() - target.getTime();
      final int denominator = 1000 * 60 * 60 * 24;
      days = (difference / denominator);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return days;
  }

  /**
   * This private class compares foods based on days 'till expiration.
   */
  private static class ExpiryComparator implements Comparator<Food> {
    @Override
    public int compare(Food arg0, Food arg1) {
      return Double.compare(arg0.getDaysTillExpiration(), arg1.getDaysTillExpiration());
    }
  }
}
