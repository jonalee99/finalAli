package edu.brown.cs.student.sorting;

import edu.brown.cs.student.general.CommandManager;
import edu.brown.cs.student.login.LoginCommands;
import edu.brown.cs.student.login.User;
import edu.brown.cs.student.login.UserDAO;
import edu.brown.cs.student.recipe.Recipe;
import edu.brown.cs.student.recipe.RecipeDatabase;
import edu.brown.cs.student.login.Food;

import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.sql.Connection;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import org.w3c.dom.UserDataHandler;

public class RecipeSort {

  private static Connection database;
  private static final UserDAO userDAO = new UserDAO();
  private static LoginCommands loginCommands;
  private static RecipeDatabase recipeDatabase;
  private HashMap<String, Integer> recipeValMap;

  
  public void importSortCommands(CommandManager cm) {
    cm.register("expire", new ExpireSort());
    cm.register("sort", new CategorySort());
    cm.register("cuisines", new CuisineList());
  }
  
  private class ExpireSort implements CommandManager.Command {
  
    private final Gson GSON = new Gson();
  
    @Override
    public void execute(String tokens, PrintWriter pw, Boolean gui) {
      try {
        // If there are no arguments
        if (tokens == null) {
          throw new InvalidArgsException("No input");
        }
        String[][] sortedRecipes = expirySort();
        if (!gui) {
          pw.println(Arrays.deepToString(sortedRecipes));
        } else if (gui) {
          pw.print(GSON.toJson(sortedRecipes));
        }
    
      } catch (InvalidArgsException | SQLException exception) {
    
        // Print the format of how way should look
        pw.print("ERROR: Something went wrong ding dong: ");
    
        // Print the specific error causing it
        pw.println(exception.getLocalizedMessage());
      } catch (Exception e) {
        pw.print("ERROR: Yo there's something mad wrong: ");
        pw.println(e.getMessage());
      }
    }
  }
  
  private class CategorySort implements CommandManager.Command {
  
    private final Gson GSON = new Gson();
  
    @Override
    public void execute(String tokens, PrintWriter pw, Boolean gui) {
      try {
        // If there are no arguments
        if (tokens == null) {
          throw new InvalidArgsException("No input");
        }
  
        Pattern regex = Pattern.compile("\\s+(?=(?:[^\"]*[\"][^\"]*[\"])*[^\"]*$)");
        // Split argument by spaces
        String[] args = tokens.split(regex.pattern(), 1);
        
        
        String[][] sortedRecipes = categorySort(args[0]);
        if (!gui) {
          pw.println(Arrays.deepToString(sortedRecipes));
        } else if (gui) {
          pw.print(GSON.toJson(sortedRecipes));
        }
    
      } catch (InvalidArgsException | SQLException exception) {
    
        // Print the format of how way should look
        pw.print("ERROR: Something went wrong ding dong: ");
    
        // Print the specific error causing it
        pw.println(exception.getLocalizedMessage());
      } catch (Exception e) {
        pw.print("ERROR: Yo there's something mad wrong: ");
        pw.println(e.getMessage());
      }
    }
  }
  
  private class CuisineList implements CommandManager.Command {
  
    private final Gson GSON = new Gson();
    
    @Override
    public void execute(String tokens, PrintWriter pw, Boolean gui) {
      try {
        // If there are no arguments
        if (tokens == null) {
          throw new InvalidArgsException("No input");
        }
    
        String[] categoryList = userDAO.getCategoryNames();
        if (!gui) {
          pw.println(Arrays.deepToString(categoryList));
        } else if (gui) {
          pw.print(GSON.toJson(categoryList));
        }
    
      } catch (InvalidArgsException | SQLException exception) {
    
        // Print the format of how way should look
        pw.print("ERROR: Something went wrong ding dong: ");
    
        // Print the specific error causing it
        pw.println(exception.getLocalizedMessage());
      } catch (Exception e) {
        pw.print("ERROR: Yo there's something mad wrong: ");
        pw.println(e.getMessage());
      }
    }
  }
  
  
  /*
    Expiry sort returns list of recipes you can make that have at least one ingredient
    that's expiring within the given amount of time. It will prioritize first by soonest expiry
    of it's ingredients, and then by number of that expire sooner. JSON.convert or something, handle in
    login commands. printwriter .print every item in the list.*/
  /**
   * This method returns a list of recipes ordered by when the ingredients
   * used to make them will expire.
   *
   * @return list of recipes sorted by when ingredients expire
   */
  public String[][] expirySort() throws SQLException {
    Set<Recipe> recipes = new HashSet<>();
    User currentUser = LoginCommands.getLoggedUser();
    Queue<Food> expiringSoon = userDAO.getExpiry(currentUser);
    for (Food ingredient : expiringSoon) {
      //System.out.println(ingredient.getName());
      //System.out.println(ingredient.getAmount());
      String query = "SELECT recipes_ingredients.recipeId FROM recipes_ingredients, ingredients"
                         + " WHERE ingredients.food = ? AND ingredients.quantity <= ? AND "
                         + "ingredients.id = recipes_ingredients.ingredientId";
      PreparedStatement statement = UserDAO.getConnection().prepareStatement(query);
      statement.setString(1, ingredient.getName());
      statement.setDouble(2, ingredient.getAmount());
      ResultSet rs = statement.executeQuery();
      while (rs.next()) {
        //System.out.println(ingredient.getName() + " is in value update");
        Recipe currentRecipe = userDAO.getUncheckedRecipe(rs.getInt(1));
        recipes.remove(currentRecipe);
        double updatedVal = userDAO.updateRecipeVal(ingredient, currentRecipe.getValue(), currentUser);
        currentRecipe.setValue(updatedVal);
        recipes.add(currentRecipe);
      }
    }
    List<Recipe> sortedRecipes = new ArrayList<>(recipes);
    RecipeComparator recipeComparator = new RecipeComparator(true);
    sortedRecipes.sort(recipeComparator);
    //System.out.println("recipe size " + recipes.size());
    String[][] recipeList = new String[recipes.size()][3];
    for (int i = 0; i < recipes.size(); i++) {
      recipeList[i][0] = sortedRecipes.get(i).getTitle();
      recipeList[i][1] = sortedRecipes.get(i).getDescription();
      recipeList[i][2] = sortedRecipes.get(i).getUrl();
    }
    //reset values
    for (Recipe recipe : sortedRecipes) {
      recipe.setValue(0);
    }
    return recipeList;
  }

  /**
   * Allows user to return recipes sorted by type of cuisine and orders the recipes
   * by number of ingredients they currently have in their inventory.
   * @param category of food
   * @return list of recipes for that cuisine sorted by number of ingredients the user has
   * @throws SQLException
   */
  public String[][] categorySort(String category) throws SQLException {
    Set<Recipe> recipes = new HashSet<>();
    User currentUser = LoginCommands.getLoggedUser();
    String query = "SELECT recipeId FROM category WHERE category = ?";
    PreparedStatement statement = UserDAO.getConnection().prepareStatement(query);
    statement.setString(1, category);
    ResultSet rs = statement.executeQuery();
    while (rs.next()) {
      //System.out.println("get in the loop");
      Recipe currentRecipe = userDAO.getUncheckedRecipe(rs.getInt(1));
      double updatedVal = userDAO.updateCatVal(currentRecipe.getId(), currentUser);
      currentRecipe.setInventValue(updatedVal);
      recipes.add(currentRecipe);
      //System.out.println(currentRecipe.getTitle() + " : " + currentRecipe.getInventoryVal());
    }
    List<Recipe> sortedRecipes = new ArrayList<>(recipes);
    RecipeComparator recipeComparator = new RecipeComparator(false);
    sortedRecipes.sort(recipeComparator);
    String[][] recipeList = new String[recipes.size()][3];
    for (int i = 0; i < recipes.size(); i++) {
      recipeList[i][0] = sortedRecipes.get(i).getTitle();
      recipeList[i][1] = sortedRecipes.get(i).getDescription();
      recipeList[i][2] = sortedRecipes.get(i).getUrl();
    }
    //reset values
    for (Recipe recipe : sortedRecipes) {
      recipe.setInventValue(0);
    }
    return recipeList;
  }

  private static class RecipeComparator implements Comparator<Recipe> {
    
    private final boolean expiry;
    
    public RecipeComparator(boolean expiry) {
      this.expiry = expiry;
    }
  
    @Override
    public int compare(Recipe o1, Recipe o2) {
      if (expiry) {
        return Double.compare(o1.getValue(), o2.getValue());
      } else {
        return Double.compare(o1.getInventoryVal(), o2.getInventoryVal());
      }
    }
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
