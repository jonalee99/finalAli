package edu.brown.cs.student.login;

import java.io.PrintWriter;
import java.sql.SQLException;

import com.google.gson.Gson;

import edu.brown.cs.student.database.DatabaseDAO;
import edu.brown.cs.student.database.NoDatabaseException;
import edu.brown.cs.student.general.CommandManager;
import edu.brown.cs.student.general.CommandManager.Command;
import edu.brown.cs.student.general.InvalidArgsException;
import edu.brown.cs.student.user.User;

/**
 * This class registers all the commands for maps.
 */
public class LoginCommands {

  private static User loggedUser;
  private static final Gson GSON = new Gson();


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
  private void importCommands(CommandManager cm) {
    cm.register("login", new Login());
    cm.register("register", new Register());
    cm.register("user", new LoggedUser());
    cm.register("logout", new Logout());
  }

  /**
   * Returns the logged user.
   * @return loggedUser
   */
  public static User getLoggedUser() {
    return loggedUser;
  }

  /**
   * This command check whether a username and password is valid.
   */
  private class Login implements Command {

    @Override
    public void execute(String tokens, PrintWriter pw, Boolean gui) {
      try {

        // Check if a database is loaded
        if (!DatabaseDAO.isConnected()) {
          throw new NoDatabaseException("");
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
          user = LoginDatabase.checkLogin(args[0], args[1]);
          if (user == null) {
            if (gui) {
              String[] returnArray = new String[2];
              returnArray[0] = "No User Logged In";
              returnArray[1] = "Invalid Username or Password";
              pw.println(GSON.toJson(returnArray));
              return;
            } else {
              throw new InvalidArgsException("Invalid username or password");
            }
          }
        } catch (SQLException e) {
          throw new InvalidArgsException("Invalid username or password");
        }

        loggedUser = user;

        if (gui) {
          String[] returnArray = new String[2];
          returnArray[0] = user.getFullname();
          returnArray[1] = "Success! " + user.getFullname() + " has been logged in.";
          pw.println(GSON.toJson(returnArray));
        } else {
          pw.println("Success! " + user.getFullname() + " has been logged in.");
        }

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
   * This command registers a username and password.
   */
  private class Register implements Command {

    @Override
    public void execute(String tokens, PrintWriter pw, Boolean gui) {
      try {

        // Check if a database is loaded
        if (!DatabaseDAO.isConnected()) {
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
          user = LoginDatabase.createUser(args[0], args[1], args[2]);
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
   * This command returns the name of the logged in user.
   */
  private class LoggedUser implements Command {

    @Override
    public void execute(String tokens, PrintWriter pw, Boolean gui) {
      try {

        // Check if a database is loaded
        if (!DatabaseDAO.isConnected()) {
          throw new NoDatabaseException("");
        }

        // If there are no arguments
        if (tokens != null) {
          throw new InvalidArgsException("No arguments allowed");
        }

        if (loggedUser == null) {
          pw.println("No User Logged In");
        } else {
          pw.println(loggedUser.getFullname());
        }

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
   * This command logs the user out.
   */
  private class Logout implements Command {

    @Override
    public void execute(String tokens, PrintWriter pw, Boolean gui) {
      try {

        // Check if a database is loaded
        if (!DatabaseDAO.isConnected()) {
          throw new NoDatabaseException("");
        }

        // If there are no arguments
        if (tokens != null) {
          throw new InvalidArgsException("No arguments allowed");
        }

        if (loggedUser == null) {
          pw.println("Please Log In");
        } else {
          String name = loggedUser.getFullname();
          loggedUser = null;
          pw.println(name + " logged out");
        }

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
}
