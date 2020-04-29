package edu.brown.cs.student.login;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Queue;
import com.google.gson.Gson;
import java.util.regex.Pattern;

import edu.brown.cs.student.general.CommandManager;
import edu.brown.cs.student.general.CommandManager.Command;
import edu.brown.cs.student.recipe.RecipeDatabase;
import edu.brown.cs.student.sorting.RecipeSort;

/**
 * This class registers all the commands for maps.
 */
public class LoginCommands {

  private static UserDAO userDao = new UserDAO();
  private static User loggedUser = null;
  private static RecipeDatabase recipeDatabase = new RecipeDatabase();
  private static final Gson GSON = new Gson();
  private static RecipeSort recipeSort = new RecipeSort();

  /**
   * Registers all the commands.
   *
   * @param commandManager is where we register the commands
   */
  public LoginCommands(CommandManager commandManager) {

    // Import the commands
    this.importCommands(commandManager);
  }

  /**
   * This function registers all the commands defined by the user.
   *
   * @param cm is the command manager
   */
  public void importCommands(CommandManager cm) {
    cm.register("connect", new Connect());
    cm.register("login", new Login());
    cm.register("register", new Register());
    cm.register("item", new Item());
    cm.register("database", new Database());
    cm.register("inventory", new Inventory());
    recipeDatabase.importRecipeCommands(cm);
    recipeSort.importSortCommands(cm);
  }

  public static User getLoggedUser() {
    return loggedUser;
  }

  /**
   * This command loads in the data from specified path/to/database.
   */
  private class Connect implements Command {

    @Override
    public void execute(String tokens, PrintWriter pw, Boolean gui) {
      try {

        // If there are no arguments
        if (tokens == null) {
          throw new InvalidArgsException("No filepath inputted");
        }

        // Connects the database
        userDao.connect(tokens);

        // Output
        pw.println("user database set to " + tokens);

      } catch (InvalidArgsException exception) {

        // Print the format of how way should look
        pw.print("ERROR: Input should be in form of 'connect <path/to/database>': ");

        // Print the specific error causing it
        pw.println(exception.getLocalizedMessage());

      } catch (ClassNotFoundException | SQLException e) {
        pw.println("ERROR: filepath invalid");
      }
    }
  }

  /**
   * This command check whether a username and password is valid
   */
  private class Login implements Command {

    @Override
    public void execute(String tokens, PrintWriter pw, Boolean gui) {
      try {

        // Check if a database is loaded
        if (!userDao.isConnected()) {
          throw new NoDatabaseException("asdf");
        }

        // If there are no arguments
        if (tokens == null) {
          throw new InvalidArgsException("No arguments inputted");
        }

        // Split argument by spaces
        String[] args = tokens.split(" ");

        // Check if there are only 2 arguments
        if (args.length != 2) {
          throw new InvalidArgsException("Must have 2 arguments, not " + args.length);
        }

        User user;
        try {
          user = userDao.checkLogin(args[0], args[1]);
          if (user == null) {
            throw new InvalidArgsException("Invalid username or password");
          }
        } catch (SQLException e) {
          throw new InvalidArgsException("Invalid username or password");
        }

        loggedUser = user;

        pw.println("Success! " + user.getFullname() + " has been logged in.");

      } catch (InvalidArgsException exception) {

        // Print the format of how way should look
        pw.print("ERROR: Input should be in form of 'login <username> <password>': ");

        // Print the specific error causing it
        pw.println(exception.getLocalizedMessage());

      } catch (NoDatabaseException exception) {
        pw.println("ERROR: Please load a database using 'connect' command");
      }
    }
  }

  /**
   * This command registers a username and password
   */
  private class Register implements Command {

    @Override
    public void execute(String tokens, PrintWriter pw, Boolean gui) {
      try {

        // Check if a database is loaded
        if (!userDao.isConnected()) {
          throw new NoDatabaseException("");
        }

        // If there are no arguments
        if (tokens == null) {
          throw new InvalidArgsException("No arguments inputted");
        }

        // Split argument by spaces
        String[] args = tokens.split(" ", 3);

        // Check if there are only 2 arguments
        if (args.length != 3) {
          throw new InvalidArgsException("Must have 3 arguments, not " + args.length);
        }

        User user;
        try {
          user = userDao.createUser(args[0], args[1], args[2]);
          if (user == null) {
            throw new InvalidArgsException("Invalid username or password");
          }
        } catch (SQLException e) {
          throw new InvalidArgsException(e.getLocalizedMessage());
        }

        pw.println("Success! " + user.getFullname() + " has been registered.");

      } catch (InvalidArgsException exception) {

        // Print the format of how way should look
        pw.print("ERROR: Input should be in form of 'register <username> <password> <name>': ");

        // Print the specific error causing it
        pw.println(exception.getLocalizedMessage());

      } catch (NoDatabaseException exception) {
        pw.println("ERROR: Please load a database using 'connect' command");
      }
    }
  }

  /**
   * This command registers a new item
   */
  private class Item implements Command {

    @Override
    public void execute(String tokens, PrintWriter pw, Boolean gui) {
      try {

        // Check if a database is loaded
        if (!userDao.isConnected()) {
          throw new NoDatabaseException("asdf");
        }

        if (loggedUser == null) {
          throw new InvalidArgsException("Please login first");
        }

        // If there are no arguments
        if (tokens == null) {
          throw new InvalidArgsException("No arguments inputted");
        }
  
        Pattern regex = Pattern.compile("\\s+(?=(?:[^\"]*[\"][^\"]*[\"])*[^\"]*$)");
        // Split argument by spaces
        String[] args = tokens.split(regex.pattern(), 5);

        // Check if there are only 5 arguments
        if (args.length != 5) {
          throw new InvalidArgsException("Must have 5 arguments, not " + args.length);
        }

        // Casts quantity as an integer
        double quant;
        Date date;
        String type = args[2].replace("\"", "");
        try {
          quant = Double.parseDouble(args[0]);
          //System.out.println(args[4].replace("\"", "") + " " + quant);
          date = new SimpleDateFormat("dd/MM/yyyy").parse(args[3]);
        } catch (Exception e) {
          throw new InvalidArgsException("quantity or date invalid");
        }

        // Adds to inventory
        try {
          userDao.createInventoryItem(loggedUser.getUsername(), args[4].replace("\"", ""), quant, args[3], type, args[1]);
        } catch (SQLException e) {
          e.printStackTrace();
          throw new InvalidArgsException(e.getLocalizedMessage());
        }

        pw.println(
            "Success! " + quant + " " + args[4].replace("\"", "") + " have been added to " + loggedUser.getFullname() + "'s inventory.");

      } catch (InvalidArgsException exception) {

        // Print the format of how way should look
        pw.print("ERROR: Input should be in form of 'item <quantity> <units> <type> <expiration dd/MM/yyyy> <food>': ");

        // Print the specific error causing it
        pw.println(exception.getLocalizedMessage());
      } catch (NoDatabaseException exception) {
        pw.println("ERROR: Please load a database using 'connect' command");
      }
    }
  }

  /**
   * This command returns the inventory
   */
  private class Inventory implements Command {

    @Override
    public void execute(String tokens, PrintWriter pw, Boolean gui) {
      try {

        // Check if a database is loaded
        if (!userDao.isConnected()) {
          throw new NoDatabaseException("");
        }

        if (loggedUser == null) {
          throw new InvalidArgsException("Please login first");
        }

        // If there are no arguments
        if (tokens != null) {
          throw new InvalidArgsException("No arguments permitted");
        }

        // Get the inventory sorted on expiration
        Queue<Food> inventoryQueue = userDao.getExpiry(loggedUser);
        int size = inventoryQueue.size();

        if (gui) {
          // This array will be outputted to the front end
          String[][] stringArray = new String[size][5];

          // Loop through every item in inventory
          for (int i = 0; i < size; i++) {

            // Get the current food
            Food currentFood = inventoryQueue.poll();

            // Put the information into the array
            stringArray[i][0] = currentFood.getName();
            stringArray[i][1] = currentFood.getType();
            stringArray[i][2] = currentFood.getAmount().toString();
            stringArray[i][3] = currentFood.getUnit();
            stringArray[i][4] = currentFood.getDaysTillExpiration().toString();
          }

          // Return the Json
          pw.print(GSON.toJson(stringArray));

        } else {
          while (!inventoryQueue.isEmpty()) {

            // Get the current food
            Food currentFood = inventoryQueue.poll();
            pw.println(currentFood);
          }
        }
        

      } catch (InvalidArgsException | SQLException exception) {

        // Print the format of how way should look
        pw.print("ERROR: Input should be in form of 'item <quantity> <units> <type> <expiration dd/MM/yyyy> <food>': ");

        // Print the specific error causing it
        pw.println(exception.getLocalizedMessage());

      } catch (NoDatabaseException exception) {
        pw.println("ERROR: Please load a database using 'connect' command");
      }
    }
  }

  /**
   * This command creates a new database
   */
  private class Database implements Command {

    @Override
    public void execute(String tokens, PrintWriter pw, Boolean gui) {
      try {

        if (loggedUser != null) {
          loggedUser = null;
        }

        // If there are no arguments
        if (tokens == null) {
          throw new InvalidArgsException("No arguments inputted");
        }

        // Split argument by spaces
        String[] args = tokens.split(" ");

        // Check if there are only 2 arguments
        if (args.length != 1) {
          throw new InvalidArgsException("Must have 1 arguments, not " + args.length);
        }

        // Creates a new database
        try {
          userDao.createDatabase(args[0]);
        } catch (SQLException | ClassNotFoundException e) {
          e.printStackTrace();
          throw new InvalidArgsException(e.getLocalizedMessage());
        }

        pw.println("Success! New database " + args[0] + " has been created and connected");

      } catch (InvalidArgsException exception) {

        // Print the format of how way should look
        pw.print("ERROR: ");

        // Print the specific error causing it
        pw.println(exception.getLocalizedMessage());
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
