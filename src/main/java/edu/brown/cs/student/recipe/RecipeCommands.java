package edu.brown.cs.student.recipe;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.regex.Pattern;

import com.google.gson.Gson;

import edu.brown.cs.student.database.DatabaseDAO;
import edu.brown.cs.student.general.InvalidArgsException;
import edu.brown.cs.student.database.NoDatabaseException;
import edu.brown.cs.student.general.CommandManager;
import edu.brown.cs.student.general.CommandManager.Command;

/**
 * Commands involved with recipes.
 */
public class RecipeCommands {

  private RecipeDatabase recipeDatabase;

  /**
   * Constructor takes in a command Manager.
   * 
   * @param commandManager commandManager
   */
  public RecipeCommands(CommandManager commandManager) {
    this.importCommands(commandManager);
    this.recipeDatabase = new RecipeDatabase();
  }

  private void importCommands(CommandManager cm) {
    cm.register("recipe", new AddRecipe());
    cm.register("ingredient", new Ingredient());
    cm.register("category", new Category());
    cm.register("expire", new ExpireSort());
    cm.register("sort", new CategorySort());
    cm.register("cuisines", new CuisineList());
    cm.register("types", new TypeList());
    cm.register("units", new GetUnits());
    cm.register("fot", new GetFoodOfType());
    cm.register("tff", new GetTypeFromFood());
  }

  /**
   * This class adds new recipes.
   */
  private class AddRecipe implements Command {

    @Override
    public void execute(String tokens, PrintWriter pw, Boolean gui) {
      try {

        // Chdeck if a database is loaded
        if (!DatabaseDAO.isConnected()) {
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
          recipe = recipeDatabase.createRecipe(args[0], args[1], args[2]);
          if (recipe == null) {
            throw new InvalidArgsException("Invalid recipe entry");
          }
        } catch (SQLException e) {
          System.out.println(e.getMessage());
          throw new InvalidArgsException("Invalid recipe entry");
        }

        // recipeHashMap.put(recipe.getId(), recipe);
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
  }

  /**
   * This class adds ingredients.
   */
  private class Ingredient implements Command {

    @Override
    public void execute(String tokens, PrintWriter pw, Boolean gui) {
      try {

        // Check if a database is loaded
        if (!DatabaseDAO.isConnected()) {
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
          recipeDatabase.addIngredient(args[0], args[1], Double.parseDouble(args[2]), args[3],
              Integer.parseInt(args[4]));
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
  }

  /**
   * This adds categories.
   */
  private class Category implements Command {

    @Override
    public void execute(String tokens, PrintWriter pw, Boolean gui) {
      try {

        // Check if a database is loaded
        if (!DatabaseDAO.isConnected()) {
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
          recipeDatabase.addCategory(args[0], Integer.parseInt(args[1]));
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
  }

  /**
   * This sorts recipes by expiring ingredients.
   */
  private class ExpireSort implements CommandManager.Command {

    private final Gson GSON = new Gson();

    @Override
    public void execute(String tokens, PrintWriter pw, Boolean gui) {
      try {
        // If there are no arguments
        if (tokens == null) {
          throw new InvalidArgsException("No input");
        }
        String[][] sortedRecipes = RecipeSort.expirySort();
        if (!gui) {
          for (int i = 0; i < sortedRecipes.length; i++) {
            System.out.println(sortedRecipes[i][0] + " : " + sortedRecipes[i][3]);
          }
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

  /**
   * This sorts recipes by category.
   */
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

        String[][] sortedRecipes = RecipeSort.categorySort(args[0]);
        if (!gui) {
          for (int i = 0; i < sortedRecipes.length; i++) {
            System.out.println(sortedRecipes[i][0]);
          }
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

  /**
   * Returns a list of cuisines.
   */
  private class CuisineList implements CommandManager.Command {

    private final Gson GSON = new Gson();

    @Override
    public void execute(String tokens, PrintWriter pw, Boolean gui) {
      try {
        // If there are no arguments
        if (tokens == null) {
          throw new InvalidArgsException("No input");
        }

        String[] categoryList = RecipeQueries.getCategoryNames();
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

  /**
   * Returns all possible food types.
   */
  private class TypeList implements CommandManager.Command {

    private final Gson GSON = new Gson();

    @Override
    public void execute(String tokens, PrintWriter pw, Boolean gui) {
      try {
        // If there are no arguments
        if (tokens == null) {
          throw new InvalidArgsException("No input");
        }

        String[] typeList = RecipeQueries.getFoodTypes();
        if (!gui) {
          pw.println(Arrays.deepToString(typeList));
        } else if (gui) {
          pw.print(GSON.toJson(typeList));
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

  /**
   * Given a type, returns all possible foods.
   */
  private class GetFoodOfType implements CommandManager.Command {

    private final Gson GSON = new Gson();

    @Override
    public void execute(String tokens, PrintWriter pw, Boolean gui) {
      try {

        if (!DatabaseDAO.isConnected()) {
          System.out.println("Sam connect the database");
          throw new NoDatabaseException("asdf");
        }

        // If there are no arguments
        if (tokens == null) {
          throw new InvalidArgsException("No input");
        }

        Pattern regex = Pattern.compile("\\s+(?=(?:[^\"]*[\"][^\"]*[\"])*[^\"]*$)");
        // Split argument by spaces
        String[] args = tokens.split(regex.pattern(), 1);

        // Check if there are only 1 arguments
        if (args.length != 1) {
          throw new InvalidArgsException("Must have 1 arguments, not " + args.length);
        }

        String[] foodsOfType = RecipeQueries.getFoodsOfType(args[0].replace("\"", ""));
        if (!gui) {
          pw.println(Arrays.toString(foodsOfType));
        } else if (gui) {
          pw.print(GSON.toJson(foodsOfType));
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

  /**
   * Gets units given a food.
   */
  private class GetUnits implements CommandManager.Command {

    private final Gson GSON = new Gson();

    @Override
    public void execute(String tokens, PrintWriter pw, Boolean gui) {
      try {

        if (!DatabaseDAO.isConnected()) {
          System.out.println("Sam connect the database");
          throw new NoDatabaseException("asdf");
        }

        // If there are no arguments
        if (tokens == null) {
          throw new InvalidArgsException("No input");
        }

        // Split argument by spaces
        Pattern regex = Pattern.compile("\\s+(?=(?:[^\"]*[\"][^\"]*[\"])*[^\"]*$)");
        // Split argument by spaces
        String[] args = tokens.split(regex.pattern(), 1);

        // Check if there are only 1 arguments
        if (args.length != 1) {
          throw new InvalidArgsException("Must have 1 arguments, not " + args.length);
        }

        String unit = RecipeQueries.getUnit(args[0]);
        if (!gui) {
          pw.println(unit);
        } else if (gui) {
          pw.print(GSON.toJson(unit));
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
  
  private class GetTypeFromFood implements CommandManager.Command {
    
    private final Gson GSON = new Gson();
    
    @Override
    public void execute(String tokens, PrintWriter pw, Boolean gui) {
      try {
        
        if (!DatabaseDAO.isConnected()) {
          System.out.println("Sam connect the database");
          throw new NoDatabaseException("asdf");
        }
        
        // If there are no arguments
        if (tokens == null) {
          throw new InvalidArgsException("No input");
        }
        
        // Split argument by spaces
        Pattern regex = Pattern.compile("\\s+(?=(?:[^\"]*[\"][^\"]*[\"])*[^\"]*$)");
        // Split argument by spaces
        String[] args = tokens.split(regex.pattern(), 1);
        
        // Check if there are only 1 arguments
        if (args.length != 1) {
          throw new InvalidArgsException("Must have 1 arguments, not " + args.length);
        }
        
        String unit = RecipeQueries.getType(args[0].replace("\"", ""));
        if (!gui) {
          pw.println(unit);
        } else if (gui) {
          pw.print(GSON.toJson(unit));
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

}
