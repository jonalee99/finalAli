package edu.brown.cs.student.database;

import java.io.PrintWriter;
import java.sql.SQLException;

import edu.brown.cs.student.general.CommandManager;
import edu.brown.cs.student.general.CommandManager.Command;
import edu.brown.cs.student.general.InvalidArgsException;

/**
 * Commands for the database.
 */
public class DatabaseCommands {

  /**
   * Import the commands.
   * @param commandManager commandManager
   */
  public DatabaseCommands(CommandManager commandManager) {

    // Import the commands
    this.importCommands(commandManager);
  }

  /**
   * This function registers all the commands defined by the user.
   *
   * @param cm is the command manager
   */
  private void importCommands(CommandManager cm) {
    cm.register("connect", new Connect());
    cm.register("database", new Database());
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
        DatabaseDAO.connect(tokens);

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
   * This command creates a new database.
   */
  private class Database implements Command {

    @Override
    public void execute(String tokens, PrintWriter pw, Boolean gui) {
      try {

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
          DatabaseDAO.createDatabase(args[0]);
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
}
