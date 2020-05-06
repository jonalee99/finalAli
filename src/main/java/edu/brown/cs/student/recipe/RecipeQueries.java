package edu.brown.cs.student.recipe;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import edu.brown.cs.student.database.DatabaseDAO;
import edu.brown.cs.student.user.User;

/**
 * Queries for recipes.
 */
public final class RecipeQueries {

  private RecipeQueries() {
    //Utility class
  }

  /**
   * gets a recipe given a recipe id.
   * @param recipeId id
   * @return recipe instance
   * @throws SQLException stinky
   */
  public static Recipe getRecipe(int recipeId) throws SQLException {
    String query = "SELECT * FROM recipes WHERE id = ?";
    Connection connection = DatabaseDAO.getConnection();
    PreparedStatement statement = connection.prepareStatement(query);
    statement.setInt(1, recipeId);
    ResultSet rs = statement.executeQuery();
    String title = rs.getString("title");
    String description = rs.getString("description");
    String url = rs.getString("url");
    Recipe recipe = new Recipe(title, description, url);
    recipe.setId(recipeId);
    rs.close();
    return recipe;
  }

  private static CacheLoader<Integer, Recipe> recipeLoader = new CacheLoader<Integer, Recipe>() {
    @Override
    public Recipe load(Integer recipeId) throws SQLException {
      return getRecipe(recipeId);
    }
  };

  private static final int MAX = 20;
  private static LoadingCache<Integer, Recipe> recipeCache
      = CacheBuilder.newBuilder()
      .maximumSize(MAX)
      .build(recipeLoader);

  /**
   * Gets uncheck recipe.
   * @param recipeId id
   * @return recipe instance
   */
  public static Recipe getUncheckedRecipe(int recipeId) {
    return recipeCache.getUnchecked(recipeId);
  }

  /**
   * Given a recipe id, returns ingredients.
   * @param recipeId id
   * @return a set of food
   * @throws SQLException stinky
   */
  public static Set<Food> ingredientsFor(int recipeId) throws SQLException {
    Set<Food> ingredients = new HashSet<>();
    Connection connection = DatabaseDAO.getConnection();
    String query = "SELECT ingredients.food, ingredients.quantity, ingredients.unit "
        + "FROM ingredients, recipes_ingredients " + "WHERE recipes_ingredients.recipeId = ? "
        + "AND recipes_ingredients.ingredientId = ingredients.id";
    PreparedStatement statement = connection.prepareStatement(query);
    statement.setInt(1, recipeId);
    ResultSet rs = statement.executeQuery();
    while (rs.next()) {
      String name = rs.getString("food");
      double quantity = rs.getDouble("quantity");
      String unit = rs.getString("unit");
      Food food = new Food(name, quantity, unit, 0.0, null, null);
      ingredients.add(food);
    }
    rs.close();
    return ingredients;
  }

  /**
   * Checks if there is enough food in the user's inventory.
   * @param ingredient food instance
   * @param user that's logged in
   * @return boolean
   * @throws SQLException stinky
   */
  public static boolean enoughQuantity(Food ingredient, User user) throws SQLException {
    Connection connection = DatabaseDAO.getConnection();
    String query = "SELECT food, quantity, unit FROM inventory WHERE food = ? AND quantity >= ? "
                       + "AND unit = ? AND username = ?";
    PreparedStatement statement = connection.prepareStatement(query);
    statement.setString(1, ingredient.getName());
    statement.setDouble(2, ingredient.getAmount());
    statement.setString(3, ingredient.getUnit());
    statement.setString(4, user.getUsername());
    ResultSet rs = statement.executeQuery();
    boolean isEnough = rs.next();
    rs.close();
    return isEnough;
  }

  /**
   * Get the names of categories.
   * @return Array of strings of category names
   * @throws SQLException stinky
   */
  public static String[] getCategoryNames() throws SQLException {
    Connection connection = DatabaseDAO.getConnection();
    Set<String> categorySet = new HashSet<>();
    String query = "SELECT category FROM category";
    PreparedStatement statement = connection.prepareStatement(query);
    ResultSet rs = statement.executeQuery();
    while (rs.next()) {
      categorySet.add(rs.getString(1));
    }
    String[] categoryList = new String[categorySet.size()];
    int i = 0;
    for (String category : categorySet) {
      categoryList[i] = category;
      i++;
    }
    return categoryList;
  }

  /**
   * Return all food types.
   * @return array of strings of food types
   * @throws SQLException stinky
   */
  public static String[] getFoodTypes() throws SQLException {
    Connection connection = DatabaseDAO.getConnection();
    Set<String> typeSet = new HashSet<>();
    String query = "SELECT type FROM ingredients";
    PreparedStatement statement = connection.prepareStatement(query);
    ResultSet rs = statement.executeQuery();
    while (rs.next()) {
      typeSet.add(rs.getString(1));
    }
    String[] typeList = new String[typeSet.size()];
    int i = 0;
    for (String category : typeSet) {
      typeList[i] = category;
      i++;
    }
    return typeList;
  }

  /**
   * given a type, returns all the foods.
   * @param type of food
   * @return array of strings of foods
   * @throws SQLException stinky
   */
  public static String[] getFoodsOfType(String type) throws SQLException {
    Connection connection = DatabaseDAO.getConnection();
    Set<String> foodSet = new HashSet<>();
    String query = "SELECT food FROM ingredients WHERE type = ?";
    PreparedStatement statement = connection.prepareStatement(query);
    statement.setString(1, type);
    ResultSet rs = statement.executeQuery();
    while (rs.next()) {
      foodSet.add(rs.getString(1));
    }
    String[] foodList = new String[foodSet.size()];
    int i = 0;
    for (String food : foodSet) {
      foodList[i] = food;
      i++;
    }
    return foodList;
  }

  /**
   * Gets units given an ingredient name.
   * @param ingredientName name
   * @return type of food
   * @throws SQLException stinky
   */
  public static String getUnit(String ingredientName) throws SQLException {
    Connection connection = DatabaseDAO.getConnection();
    String query = "SELECT unit FROM ingredients WHERE food = ?";
    PreparedStatement statement = connection.prepareStatement(query);
    statement.setString(1, ingredientName);
    ResultSet rs = statement.executeQuery();
    if (rs.next()) {
      return rs.getString(1);
    }
    return null;
  }
  
  public static String getType(String ingredientName) throws SQLException{
    Connection connection = DatabaseDAO.getConnection();
    String query = "SELECT type FROM ingredients WHERE food = ?";
    PreparedStatement statement = connection.prepareStatement(query);
    statement.setString(1, ingredientName);
    ResultSet rs = statement.executeQuery();
    if (rs.next()) {
      return rs.getString(1);
    }
    return null;
  }

}
