package edu.brown.cs.student.login;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import edu.brown.cs.student.recipe.Recipe;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.sql.PreparedStatement;

import static java.time.temporal.ChronoUnit.DAYS;

/**
 * This class accesses the user data from the database.
 */
public class UserDAO {

  private static Connection connection;

  /**
   * Set connection as null by default.
   */
  public UserDAO() {
    this.connection = null;
  }

  /**
   * Returns whether it is connected or not.
   * @return boolean
   */
  public boolean isConnected() {
    return connection != null;
  }

  public static Connection getConnection() {
    return connection;
  }

  /**
   * This method creates a new database with the given name inside the data
   * folder and connects the connection to it.
   *
   * @param name to describe the database
   * @throws SQLException           uh oh
   * @throws ClassNotFoundException stinky
   */
  public void createDatabase(String name) throws SQLException, ClassNotFoundException {

    // This loads the connection
    Class.forName("org.sqlite.JDBC");
    String urlToDB = "jdbc:sqlite:data/" + name + ".sqlite3";
    connection = DriverManager.getConnection(urlToDB);
    
    // Create the tables
    PreparedStatement statement = connection.prepareStatement(
        "CREATE TABLE IF NOT EXISTS users ( "
        + "username	TEXT NOT NULL, "
        + "password	TEXT NOT NULL, "
        + "name	TEXT NOT NULL, "
        + "PRIMARY KEY(username) "
        + ");");
    statement.executeUpdate();

    statement = connection.prepareStatement(
        "CREATE TABLE IF NOT EXISTS inventory ( "
        + "id	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
        + "username	TEXT NOT NULL, "
        + "food	TEXT NOT NULL, "
        + "type	TEXT NOT NULL, "
        + "unit TEXT, "
        + "quantity	FLOAT NOT NULL, "
        + "expiration	INTEGER NOT NULL, "
        + "FOREIGN KEY(username) REFERENCES users(username)"
        + ");");
    statement.executeUpdate();

    statement = connection.prepareStatement(
        "CREATE TABLE IF NOT EXISTS recipes ( "
        + "id	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
        + "title TEXT NOT NULL, "
        + "description TEXT NOT NULL, "
        + "url TEXT NOT NULL"
        + ");");
    statement.executeUpdate();

    statement = connection.prepareStatement(
        "CREATE TABLE IF NOT EXISTS ingredients ( "
        + "id	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
        + "food	TEXT NOT NULL, "
        + "type	TEXT NOT NULL, "
        + "quantity	FLOAT, "
        + "unit TEXT"
        + ");");
    statement.executeUpdate();

    statement = connection.prepareStatement(
        "CREATE TABLE IF NOT EXISTS recipes_ingredients ( "
        + "recipeId	INTEGER NOT NULL , "
        + "ingredientId	INTEGER NOT NULL"
        + ");");
    statement.executeUpdate();

    statement = connection.prepareStatement(
        "CREATE TABLE IF NOT EXISTS category ( "
        + "id	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
        + "category	TEXT NOT NULL, "
        + "recipeId	INTEGER NOT NULL, "
        + "FOREIGN KEY(recipeId) REFERENCES recipes(id)"
        + ");");
    statement.executeUpdate();

  }

  /**
   * It connects the class to our database. It initializes the private variable
   * "connection", which will be used in subsequent queries.
   *
   * @param fileName the specific file that we want to run Dijkstra's on
   * @throws ClassNotFoundException class not found
   * @throws SQLException           uh o h
   **/
  public void connect(String fileName) throws ClassNotFoundException, SQLException {

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

    // Check if table names are correct
    String[] tablenames = {"users" };
    DatabaseMetaData dmd = connection.getMetaData();
    ResultSet rs = null;
    for (String name : tablenames) {
      rs = dmd.getTables(null, null, name, null);
      if (!rs.next()) {
        throw new SQLException();
      }
    }
    rs.close();
  }

  /**
   * This method checks whether a username and password exist in the database.
   * @param username of user
   * @param password of user
   * @return User instance
   * @throws SQLException
   * @throws ClassNotFoundException error
   */
  public User checkLogin(String username, String password) throws SQLException {

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
   * This creates a new user
   * @param username of user
   * @param password of user
   * @param name     of user
   * @throws SQLException error
   */
  public User createUser(String username, String password, String name) throws SQLException {
    

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

  /**
   * This method creates a new inventory item
   * @param food string
   * @param quantity integer
   * @param expiration dd/MM/yyyy
   * @throws SQLException error
   */
  public void createInventoryItem(String username, String food, double quantity, String expiration, String type, String unit)
      throws SQLException {

    // Create the query from arguments
    String query = "INSERT INTO inventory (username, food, quantity, expiration, type, unit) " + "VALUES (?, ?, ?, ?, ?, ?)";
    PreparedStatement statement = connection.prepareStatement(query);
    statement.setString(1, username);
    statement.setString(2, food);
    statement.setDouble(3, quantity);
    statement.setString(4, expiration);
    statement.setString(5, type);
    statement.setString(6, unit);
    statement.executeUpdate();
  }

  /**
   * This method returns all the inventory expiring before a given date
   *
   * @return foodQueue sorted on days until expiration
   * @throws SQLException error
   */
  public Queue<Food> getExpiry(User user) throws SQLException {

    // Create the query from arguments
    String query = "SELECT * FROM inventory WHERE username = ?";
    PreparedStatement statement = connection.prepareStatement(query);
    statement.setString(1, user.getUsername());

    // Get the results
    ResultSet result = statement.executeQuery();

    // This stores the results in a priority queue based on days till expiration
    Queue<Food> foodQueue = new PriorityQueue<Food>(11, new ExpiryComparator());
    
    // Get the current date
    Date currentDate = java.sql.Date.valueOf(LocalDate.now());

    // Loop through the results
    // If the results return anything, create the user instance
    while (result.next()) {

      // Get the name, quantity, and expiration date.
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
      foodQueue.add(new Food(foodName, foodQuant, unit, daysBetween(currentDate, foodExpir), type));
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
  public boolean inInventory(String ingredient, User user) throws SQLException {
    String query = "SELECT * FROM inventory WHERE username = ? AND food = ?";
    PreparedStatement statement = connection.prepareStatement(query);
    statement.setString(1, user.getUsername());
    statement.setString(2, ingredient);
    ResultSet rs = statement.executeQuery();
    Boolean bool = rs.next();
    rs.close();
    return bool;
  }
  
  public Recipe getRecipe(int recipeId) throws SQLException {
    String query = "SELECT * FROM recipes WHERE id = ?";
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
  
  private CacheLoader<Integer, Recipe> recipeLoader = new CacheLoader<Integer, Recipe>() {
    @Override
    public Recipe load(Integer recipeId) throws SQLException {
      return getRecipe(recipeId);
    }
  };
  
  private LoadingCache<Integer, Recipe> recipeCache = CacheBuilder.newBuilder().maximumSize(20).build(recipeLoader);
  
  public Recipe getUncheckedRecipe(int recipeId) {
    return recipeCache.getUnchecked(recipeId);
  }

  /**
   * TODO: FILL THIS IN
   * @param ingredient
   * @param user
   * @return
   * @throws SQLException
   */
  public double updateRecipeVal(Food ingredient, Double previousVal, User user) throws SQLException {
    LocalDate currentDate = LocalDate.now();
    String query = "SELECT * FROM inventory WHERE username = ? AND food = ?";
    PreparedStatement statement = connection.prepareStatement(query);
    statement.setString(1, user.getUsername());
    statement.setString(2, ingredient.getName());
    ResultSet rs = statement.executeQuery();
    double exp = ingredient.getDaysTillExpiration();
    double quant = ingredient.getAmount();
    String type = ingredient.getType();
    double mult = 1;
    if (type.equals("vegetable") || type.equals("fruit")) {
      mult = 2.5;
    } else if (type.equals("meat") || type.equals("poultry") || type.equals("seafood")) {
      mult = 1.5;
    }
    if (quant >= 5) {
      mult = mult + .5;
    }
    return previousVal + ((exp * mult) * -1);
  }
  
  public double updateCatVal(int recipeId, User user) throws SQLException {
    double catVal = 0;
    Set<Food> ingredients = ingredientsFor(recipeId);
    for (Food ingredient : ingredients) {
      if (inInventory(ingredient.getName(), user)) {
        if (enoughQuantity(ingredient, user)) {
          catVal ++;
        }
      }
    }
    return catVal * -1;
  }
  
  public Set<Food> ingredientsFor(int recipeId) throws SQLException {
    Set<Food> ingredients = new HashSet<>();
    String query = "SELECT ingredients.food, ingredients.quantity, ingredients.unit "
                 + "FROM ingredients, recipes_ingredients "
                 + "WHERE recipes_ingredients.recipeId = ?";
    PreparedStatement statement = connection.prepareStatement(query);
    statement.setInt(1, recipeId);
    ResultSet rs = statement.executeQuery();
    while (rs.next()) {
      String name = rs.getString("food");
      double quantity = rs.getDouble("quantity");
      String unit = rs.getString("unit");
      Food food = new Food(name, quantity, unit, 0.0, null);
      ingredients.add(food);
    }
    rs.close();
    return ingredients;
  }
  
  public boolean enoughQuantity(Food ingredient, User user) throws SQLException {
    String query = "SELECT food, quantity, unit FROM inventory WHERE food = ? AND quantity >= ? AND unit = ?";
    PreparedStatement statement = connection.prepareStatement(query);
    statement.setString(1, ingredient.getName());
    statement.setDouble(2, ingredient.getAmount());
    statement.setString(3, ingredient.getUnit());
    ResultSet rs = statement.executeQuery();
    boolean isEnough = rs.next();
    rs.close();
    return isEnough;
  }
  
  public String[] getCategoryNames() throws SQLException {
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
   * Give two dates, gives the days in between.
   * @param target earlier date
   * @param expiration later date
   * @return double of days between
   */
  private double daysBetween(Date target, Date expiration) {
    double days = 0.0;
    try {
      long difference = expiration.getTime() - target.getTime();
      days = (difference / (1000 * 60 * 60 * 24));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return days;
  }

  /**
   * This private class compares foods based on days 'till expiration.
   */
  private class ExpiryComparator implements Comparator<Food> {
    @Override
    public int compare(Food arg0, Food arg1) {
      return Double.compare(arg0.getDaysTillExpiration(), arg1.getDaysTillExpiration());
    }
  }
}
