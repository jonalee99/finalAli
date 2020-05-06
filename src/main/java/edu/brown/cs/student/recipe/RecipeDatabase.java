package edu.brown.cs.student.recipe;

import edu.brown.cs.student.database.DatabaseDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Queries related to recipes.
 */
public class RecipeDatabase {

  /**
   * Creates a recipe.
   * @param title of recipe
   * @param description of recipe
   * @param url of recipe
   * @return created recipe
   * @throws SQLException stinky
   */
  public Recipe createRecipe(String title, String description, String url) throws SQLException {
    Connection currentConnection = DatabaseDAO.getConnection();
    String query = "SELECT * FROM recipes WHERE title = ?";
    PreparedStatement statement = currentConnection.prepareStatement(query);
    statement.setString(1, title.replace("\"", ""));
    ResultSet rs = statement.executeQuery();
    Boolean bool = rs.next();
    rs.close();

    // Create the query from arguments
    if (!bool) {
      query = "INSERT INTO recipes (title, description, url) VALUES (?, ?, ?)";
      statement = currentConnection.prepareStatement(query);
      statement.setString(1, title.replace("\"", ""));
      statement.setString(2, description.replace("\"", ""));
      statement.setString(3, url);
      statement.executeUpdate();
    }

    Recipe recipe = new Recipe(title.replace("\"", ""), description.replace("\"", ""), url);
    /*
     * query =
     * "SELECT id FROM recipes WHERE title = ? AND description = ? AND url = ?";
     * statement = currentConnection.prepareStatement(query); statement.setString(1,
     * title.replace("\"", "")); statement.setString(2, description.replace("\"",
     * "")); statement.setString(3, url); rs = statement.executeQuery(); int id =
     * rs.getInt(1);
     */
    return recipe;
  }

  /**
   * Adds an ingredient to a recipe.
   * @param food food
   * @param type of food
   * @param quantity of food
   * @param unit of food
   * @param recipeId of recipe
   * @throws SQLException stinky
   */
  public void addIngredient(String food, String type, double quantity, String unit, int recipeId)
      throws SQLException {
    boolean exists = false;
    Connection currentConnection = DatabaseDAO.getConnection();
    String query = "SELECT * FROM ingredients WHERE food = ? AND quantity = ? AND unit = ?";
    PreparedStatement statement = currentConnection.prepareStatement(query);
    statement.setString(1, food.replace("\"", ""));
    statement.setDouble(2, quantity);
    statement.setString(3, unit);
    ResultSet rs = statement.executeQuery();
    Boolean bool = rs.next();
    rs.close();
    if (bool) {
      exists = true;
    }
    String foodStripped = food.replace("\"", "");
    // Create the query from arguments
    if (!exists) {
      query = "INSERT INTO ingredients (food, type, quantity, unit) VALUES (?, ?, ?, ?)";
      statement = currentConnection.prepareStatement(query);
      statement.setString(1, foodStripped);
      statement.setString(2, type.replace("\"", ""));
      statement.setDouble(3, quantity);
      statement.setString(4, unit);
      statement.executeUpdate();
    }

    query = "SELECT id FROM ingredients WHERE food = ? AND quantity = ? AND unit =?";
    statement = currentConnection.prepareStatement(query);
    statement.setString(1, foodStripped);
    statement.setDouble(2, quantity);
    statement.setString(3, unit);
    rs = statement.executeQuery();
    int ingredientId = rs.getInt(1);
    rs.close();

    query = "SELECT * FROM recipes_ingredients WHERE recipeId = ? AND ingredientId = ? ";
    statement = currentConnection.prepareStatement(query);
    statement.setInt(1, recipeId);
    statement.setInt(2, ingredientId);
    rs = statement.executeQuery();
    bool = rs.next();
    rs.close();

    if (!bool) {
      query = "INSERT INTO recipes_ingredients (recipeId, ingredientId) VALUES (?, ?)";
      statement = currentConnection.prepareStatement(query);
      statement.setInt(1, recipeId);
      statement.setInt(2, ingredientId);
      statement.executeUpdate();
    }
  }

  /**
   * Adds a category.
   * @param category name
   * @param recipeId id
   * @throws SQLException stinky
   */
  public void addCategory(String category, int recipeId) throws SQLException {
    Connection currentConnection = DatabaseDAO.getConnection();
    String query = "SELECT * FROM category WHERE category = ? AND recipeId = ?";
    PreparedStatement statement = currentConnection.prepareStatement(query);
    statement.setString(1, category.replace("\"", ""));
    statement.setInt(2, recipeId);
    ResultSet rs = statement.executeQuery();
    Boolean bool = rs.next();
    rs.close();
    if (!bool) {
      query = "INSERT INTO category (category, recipeId) VALUES (?, ?)";
      statement = currentConnection.prepareStatement(query);
      statement.setString(1, category.replace("\"", ""));
      statement.setInt(2, recipeId);
      statement.executeUpdate();
    }
  }
}
