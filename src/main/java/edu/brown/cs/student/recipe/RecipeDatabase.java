package edu.brown.cs.student.recipe;

import edu.brown.cs.student.general.CommandManager;
import edu.brown.cs.student.general.CommandManager.Command;
import edu.brown.cs.student.login.UserDAO;


import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import java.util.regex.Pattern;

public class RecipeDatabase {

  private static UserDAO userDAO = new UserDAO();
  private static Connection connection;


  public void importRecipeCommands(CommandManager cm) {
    cm.register("recipe", new AddRecipe());
    cm.register("ingredient", new Ingredient());
    cm.register("category", new Category());
    connection = UserDAO.getConnection();
  }

  private class AddRecipe implements Command {

    @Override
    public void execute(String tokens, PrintWriter pw, Boolean gui) {
      try {

        // Check if a database is loaded
        if (!userDAO.isConnected()) {
          System.out.println("Sam connect the database");
          throw new NoDatabaseException("asdf");
        }

        // If there are no arguments
        if (tokens == null) {
          System.out.println("cmon sam write something");
          throw new InvalidArgsException("No arguments inputted");
        }

        // Split argument by spaces
        Pattern regex = Pattern.compile("\\s+(?=(?:[^\"]*[\"][^\"]*[\"])*[^\"]*$)");
        String[] args = tokens.split(regex.pattern());

        // Check if there are only 3 arguments
        if (args.length != 3) {
          throw new InvalidArgsException("Must have 3 arguments, not " + args.length);
        }

        Recipe recipe = null;
        try {
          recipe = createRecipe(args[0], args[1], args[2]);
          if (recipe == null) {
            throw new InvalidArgsException("Invalid recipe entry");
          }
        } catch (SQLException e) {
          System.out.println(e.getMessage());
          throw new InvalidArgsException("Invalid recipe entry");
        }

        //recipeHashMap.put(recipe.getId(), recipe);
        pw.println("Success! " + recipe.getTitle() + " has been added.");

      } catch (InvalidArgsException exception) {

        // Print the format of how way should look
        pw.print("ERROR: Input should be in form of 'recipe <title> <desc> <url>': ");

        // Print the specific error causing it
        pw.println(exception.getLocalizedMessage());

      } catch (NoDatabaseException exception) {
        pw.println("ERROR: Please load a database using 'connect' command");
      }
    }

    public Recipe createRecipe(String title, String description, String url) throws SQLException {
      Connection currentConnection = UserDAO.getConnection();
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
      
      Recipe recipe = new Recipe(title.replace("\"", ""),
          description.replace("\"", ""), url);
      /*
      query = "SELECT id FROM recipes WHERE title = ? AND description = ? AND url = ?";
      statement = currentConnection.prepareStatement(query);
      statement.setString(1, title.replace("\"", ""));
      statement.setString(2, description.replace("\"", ""));
      statement.setString(3, url);
      rs = statement.executeQuery();
      int id = rs.getInt(1);
       */
      return recipe;
    }
  }

  private class Ingredient implements Command{

    @Override
    public void execute(String tokens, PrintWriter pw, Boolean gui) {
      try {

        // Check if a database is loaded
        if (!userDAO.isConnected()) {
          System.out.println("Sam connect the database");
          throw new NoDatabaseException("asdf");
        }

        // If there are no arguments
        if (tokens == null) {
          System.out.println("cmon sam write something");
          throw new InvalidArgsException("No arguments inputted");
        }

        // Split argument by spaces
        Pattern regex = Pattern.compile("\\s+(?=(?:[^\"]*[\"][^\"]*[\"])*[^\"]*$)");
        String[] args = tokens.split(regex.pattern());

        // Check if there are only 2 arguments
        if (args.length != 5) {
          throw new InvalidArgsException("Must have 5 arguments, not " + args.length);
        }

        try {
          addIngredient(
              args[0], args[1], Double.parseDouble(args[2]), args[3], Integer.parseInt(args[4]));
        } catch (SQLException e) {
          System.out.println(e.getMessage());
          throw new InvalidArgsException("Invalid ingredient entry");
        }

        pw.println("Success! " + args[0].replace("\"", "") + " has been added.");

      } catch (InvalidArgsException exception) {

        // Print the format of how way should look
        pw.print("ERROR: Input should be in form of 'ur fucking wrong': ");

        // Print the specific error causing it
        pw.println(exception.getLocalizedMessage());

      } catch (NoDatabaseException exception) {
        pw.println("ERROR: Please load a database using 'connect' command");
      }
    }

    public void addIngredient(
        String food, String type, double quantity, String unit, int recipeId) throws SQLException {
      boolean exists = false;
      Connection currentConnection = UserDAO.getConnection();
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

  }

  private class Category implements Command{

    @Override
    public void execute(String tokens, PrintWriter pw, Boolean gui) {
      try {

        // Check if a database is loaded
        if (!userDAO.isConnected()) {
          System.out.println("Sam connect the database");
          throw new NoDatabaseException("asdf");
        }

        // If there are no arguments
        if (tokens == null) {
          System.out.println("cmon sam write something");
          throw new InvalidArgsException("No arguments inputted");
        }

        // Split argument by spaces
        Pattern regex = Pattern.compile(" ");
        String[] args = tokens.split(regex.pattern());

        // Check if there are only 2 arguments
        if (args.length != 2) {
          throw new InvalidArgsException("Must have 2 arguments, not " + args.length);
        }

        try {
          addCategory(args[0], Integer.parseInt(args[1]));
        } catch (SQLException e) {
          System.out.println(e.getMessage());
          throw new InvalidArgsException("Invalid category entry");
        }

        pw.println("Success! " + args[0].replace("\"", "") + " has been added.");

      } catch (InvalidArgsException exception) {

        // Print the format of how way should look
        pw.print("ERROR: Input should be in form of 'ur fucking wrong': ");

        // Print the specific error causing it
        pw.println(exception.getLocalizedMessage());

      } catch (NoDatabaseException exception) {
        pw.println("ERROR: Please load a database using 'connect' command");
      }
    }

    public void addCategory(String category, int recipeId) throws SQLException {
      Connection currentConnection = UserDAO.getConnection();
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


  /**
   * Returns the ingredients used in a Recipe given the Recipe name or id??
   * Whichever works best for the database that we choose to work with.
   * @param recipe
   * @return
   */
  public Set<String> getIngredients(String recipe) {
    return null;
  }

  /**
   * This exception is thrown when there are invalid arguments put in.
   */
  private class InvalidArgsException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Nothing much going on here. Passing message to super.
     *
     * @param message of what the error is
     */
    InvalidArgsException(String message) {
      super(message);
    }
  }

  /**
   * This exception is thrown when there are no database is loaded in.
   */
  private class NoDatabaseException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Nothing much going on here. Passing message to super.
     *
     * @param message of what the error is
     */
    NoDatabaseException(String message) {
      super(message);
    }
  }
}
