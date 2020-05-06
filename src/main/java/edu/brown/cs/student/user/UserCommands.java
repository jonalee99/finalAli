package edu.brown.cs.student.user;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Queue;
import com.google.gson.Gson;
import java.util.regex.Pattern;

import edu.brown.cs.student.general.CommandManager;
import edu.brown.cs.student.general.CommandManager.Command;
import edu.brown.cs.student.login.LoginCommands;
import edu.brown.cs.student.general.InvalidArgsException;
import edu.brown.cs.student.recipe.Food;
import edu.brown.cs.student.database.DatabaseDAO;
import edu.brown.cs.student.database.NoDatabaseException;

/**
 * Commands for the user.
 */
public class UserCommands {

  private static final Gson GSON = new Gson();

  /**
   * Registers all the commands.
   *
   * @param commandManager is where we register the commands
   */
  public UserCommands(CommandManager commandManager) {

    // Import the commands
    this.importCommands(commandManager);
  }

  /**
   * This function registers all the commands defined by the user.
   *
   * @param cm is the command manager
   */
  private void importCommands(CommandManager cm) {
    cm.register("item", new Item());
    cm.register("inventory", new Inventory());
    cm.register("delete", new Delete());
  }

  /**
   * This command deletes an item given id and quantity.
   */
  private class Delete implements Command {

    @Override
    public void execute(String tokens, PrintWriter pw, Boolean gui) {
      try {

        // Check if a database is loaded
        if (!DatabaseDAO.isConnected()) {
          throw new NoDatabaseException("");
        }

        if (LoginCommands.getLoggedUser() == null) {
          throw new InvalidArgsException("Please login first");
        }

        // If there are no arguments
        if (tokens == null) {
          throw new InvalidArgsException("No arguments inputted");
        }

        Pattern regex = Pattern.compile("\\s+(?=(?:[^\"]*[\"][^\"]*[\"])*[^\"]*$)");

        // Split argument by spaces
        String[] args = tokens.split(regex.pattern(), 2);

        // Check if there are only 2 arguments
        if (args.length != 2) {
          throw new InvalidArgsException("Must have 2 arguments, not " + args.length);
        }

        // Casts id as integer and quantity as double
        Integer id;
        double quant;
        try {
          id = Integer.parseInt(args[0]);
          quant = Double.parseDouble(args[1]);
        } catch (Exception e) {
          throw new InvalidArgsException("Invalid quantity");
        }

        // Adds to inventory
        try {
          UserDatabase.deleteItem(id, quant, LoginCommands.getLoggedUser().getUsername());
        } catch (SQLException e) {
          e.printStackTrace();
          throw new InvalidArgsException(e.getLocalizedMessage());
        }

        pw.println("Success! Entry updated");

      } catch (InvalidArgsException exception) {

        // Print the format of how way should look
        pw.print("ERROR: Input should be in form of 'delete <id> <quantity>': ");

        // Print the specific error causing it
        pw.println(exception.getLocalizedMessage());
      } catch (NoDatabaseException exception) {
        pw.println("ERROR: Please load a database using 'connect' command");
      }
    }
  }

  /**
   * This command registers a new item.
   */
  private class Item implements Command {

    @Override
    public void execute(String tokens, PrintWriter pw, Boolean gui) {
      try {

        // Check if a database is loaded
        if (!DatabaseDAO.isConnected()) {
          throw new NoDatabaseException("asdf");
        }

        if (LoginCommands.getLoggedUser() == null) {
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
        String type = args[2].replace("\"", "");
        try {
          quant = Double.parseDouble(args[0]);
          new SimpleDateFormat("dd/MM/yyyy").parse(args[3]);
        } catch (Exception e) {
          throw new InvalidArgsException("quantity or date invalid");
        }

        // Adds to inventory
        try {
          UserDatabase.createInventoryItem(LoginCommands.getLoggedUser().getUsername(),
              args[4].replace("\"", ""), quant, args[3], type, args[1]);
        } catch (SQLException e) {
          e.printStackTrace();
          throw new InvalidArgsException(e.getLocalizedMessage());
        }

        pw.println("Success! " + quant + " " + args[4].replace("\"", "") + " have been added to "
            + LoginCommands.getLoggedUser().getFullname() + "'s inventory.");

      } catch (InvalidArgsException exception) {

        // Print the format of how way should look
        pw.print(
            "ERROR: Input should be in form of 'item <quantity> <units> <type> "
                + "<expiration dd/MM/yyyy> <food>': ");

        // Print the specific error causing it
        pw.println(exception.getLocalizedMessage());
      } catch (NoDatabaseException exception) {
        pw.println("ERROR: Please load a database using 'connect' command");
      }
    }
  }

  /**
   * This command returns the inventory.
   */
  private class Inventory implements Command {

    @Override
    public void execute(String tokens, PrintWriter pw, Boolean gui) {
      try {

        // Check if a database is loaded
        if (!DatabaseDAO.isConnected()) {
          throw new NoDatabaseException("");
        }

        if (LoginCommands.getLoggedUser() == null) {
          throw new InvalidArgsException("Please login first");
        }

        // If there are no arguments
        if (tokens != null) {
          throw new InvalidArgsException("No arguments permitted");
        }

        // Get the inventory sorted on expiration
        Queue<Food> inventoryQueue = UserDatabase.getExpiry(LoginCommands.getLoggedUser());
        int size = inventoryQueue.size();

        if (gui) {
          // This array will be outputted to the front end
          String[][] stringArray = new String[size][6];

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
            stringArray[i][5] = currentFood.getId().toString();
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
        pw.print("ERROR: Input should be in form of 'item <quantity> <units> "
            + "<type> <expiration dd/MM/yyyy> <food>': ");

        // Print the specific error causing it
        pw.println(exception.getLocalizedMessage());

      } catch (NoDatabaseException exception) {
        pw.println("ERROR: Please load a database using 'connect' command");
      }
    }
  }

}
