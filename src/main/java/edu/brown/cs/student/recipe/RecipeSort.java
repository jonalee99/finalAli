package edu.brown.cs.student.recipe;

import edu.brown.cs.student.database.DatabaseDAO;
import edu.brown.cs.student.login.LoginCommands;
import edu.brown.cs.student.user.User;
import edu.brown.cs.student.user.UserDatabase;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * This class handles sorting the recipes.
 */
public final class RecipeSort {

  private RecipeSort() {
    //Utility class
  }

  /**
   * This method returns a list of recipes ordered by when the ingredients
   * used to make them will expire.
   *
   * @return list of recipes sorted by when ingredients expire
   * @throws SQLException stinky
   */
  public static String[][] expirySort() throws SQLException {
    Set<Recipe> recipes = new HashSet<>();
    User currentUser = LoginCommands.getLoggedUser();
    Queue<Food> expiringSoon = UserDatabase.getExpiry(currentUser);
    for (Food ingredient : expiringSoon) {
      //System.out.println(ingredient.getName());
      //System.out.println(ingredient.getAmount());
      String query = "SELECT recipes_ingredients.recipeId FROM recipes_ingredients, ingredients"
                         + " WHERE ingredients.food = ? AND ingredients.quantity <= ? AND "
                         + "ingredients.id = recipes_ingredients.ingredientId";
      PreparedStatement statement = DatabaseDAO.getConnection().prepareStatement(query);
      statement.setString(1, ingredient.getName());
      statement.setDouble(2, ingredient.getAmount());
      ResultSet rs = statement.executeQuery();
      while (rs.next()) {
        //System.out.println(ingredient.getName() + " is in value update");
        Recipe currentRecipe = RecipeQueries.getUncheckedRecipe(rs.getInt(1));
        recipes.remove(currentRecipe);
        double updatedVal = updateRecipeVal(ingredient, currentRecipe.getValue());
        if (ingredient.getDaysTillExpiration() < 0) {
          currentRecipe.setNumExpired(currentRecipe.getNumExpired() + 1);
        }
        currentRecipe.setValue(updatedVal);
        recipes.add(currentRecipe);
      }
    }
    List<Recipe> sortedRecipes = new ArrayList<>(recipes);
    RecipeComparator recipeComparator = new RecipeComparator(true);
    sortedRecipes.sort(recipeComparator);
    //System.out.println("recipe size " + recipes.size());
    String[][] recipeList = new String[recipes.size()][4];
    for (int i = 0; i < recipes.size(); i++) {
      recipeList[i][0] = sortedRecipes.get(i).getTitle();
      recipeList[i][1] = sortedRecipes.get(i).getDescription();
      recipeList[i][2] = sortedRecipes.get(i).getUrl();
      recipeList[i][3] = Integer.toString(sortedRecipes.get(i).getNumExpired());
    }
    //reset values
    for (Recipe recipe : sortedRecipes) {
      //System.out.println(recipe.getTitle() + " : " + recipe.getValue());
      recipe.setValue(0);
    }
    return recipeList;
  }

  /**
   * Allows user to return recipes sorted by type of cuisine and orders the recipes
   * by number of ingredients they currently have in their inventory.
   * @param category of food
   * @return list of recipes for that cuisine sorted by number of ingredients the user has
   * @throws SQLException stinky
   */
  public static String[][] categorySort(String category) throws SQLException {
    Set<Recipe> recipes = new HashSet<>();
    User currentUser = LoginCommands.getLoggedUser();
    String query = "SELECT recipeId FROM category WHERE category = ?";
    PreparedStatement statement = DatabaseDAO.getConnection().prepareStatement(query);
    statement.setString(1, category);
    ResultSet rs = statement.executeQuery();
    while (rs.next()) {
      //System.out.println("get in the loop");
      Recipe currentRecipe = RecipeQueries.getUncheckedRecipe(rs.getInt(1));
      double updatedVal = updateCatVal(currentRecipe.getId(), currentUser);
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
      //System.out.println(recipe.getTitle() + " : " + recipe.getInventoryVal());
      recipe.setInventValue(0);
    }
    return recipeList;
  }

  /**
   * Update the value of the recipe.
   * @param ingredient of the recipe
   * @param previousVal of the recipe
   * @return updated value
   */
  public static double updateRecipeVal(Food ingredient, Double previousVal) {
    // LocalDate currentDate = LocalDate.now();
    double exp = ingredient.getDaysTillExpiration();
    double quant = ingredient.getAmount();
    String type = ingredient.getType();
    double mult = 1;
    if (type.equals("vegetable") || type.equals("fruit")) {
      mult = 2;
    } else if (type.equals("meat") || type.equals("poultry") || type.equals("seafood")) {
      mult = 2.5;
    }
    if (quant >= 5) {
      mult = mult + .5;
    }
    if (exp > 5) {
      mult = 0;
    }
    //System.out.println(ingredient.getName() + " : " + exp);
    return previousVal + ((exp * mult));
  }

  /**
   * Update the category value.
   * @param recipeId of recipe
   * @param user of logged user
   * @return updated value
   * @throws SQLException stinky
   */
  public static double updateCatVal(int recipeId, User user) throws SQLException {
    double catVal = 0;
    Set<Food> ingredients = RecipeQueries.ingredientsFor(recipeId);
    for (Food ingredient : ingredients) {
      if (UserDatabase.inInventory(ingredient.getName(), user)) {
        if (RecipeQueries.enoughQuantity(ingredient, user)) {
          catVal++;
        }
      }
    }
    return catVal * -1;
  }

  /**
   * This compare recipes.
   */
  private static class RecipeComparator implements Comparator<Recipe> {

    private final boolean expiry;

    /**
     * contsruct takes in true or false.
     * @param expiry boolean
     */
    RecipeComparator(boolean expiry) {
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

}
