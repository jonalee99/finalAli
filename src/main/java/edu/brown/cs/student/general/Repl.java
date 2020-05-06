package edu.brown.cs.student.general;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import edu.brown.cs.student.login.LoginCommands;
import edu.brown.cs.student.recipe.RecipeCommands;
import edu.brown.cs.student.user.UserCommands;
import edu.brown.cs.student.database.DatabaseCommands;

/**
 * This class handles getting user input and sending it to the command manager.
 * It also handles the situation when the user inputs a command which isn't
 * registered.
 */
public class Repl {

  private CommandManager commandManager;
  private PrintWriter printWriter;
  private BufferedReader bufferedReader;

  /**
   * Instantiates instance variables.
   */
  public Repl() {

    // This is the Command Manager and the printwriter
    // The printwriter flushes automatically to System.out
    this.commandManager = new CommandManager();
    this.printWriter = new PrintWriter(System.out, true);

    // Register the commands
    new LoginCommands(commandManager);
    new RecipeCommands(commandManager);
    new UserCommands(commandManager);
    new DatabaseCommands(commandManager);
  }

  /**
   * Getter for command manager.
   * @return commandManager
   */
  public CommandManager getCommandManager() {
    return this.commandManager;
  }

  /**
   * This command stars running the Repl,
   * which has a while true loop.
   */
  public void runRepl() {
    try {

      //Enter data using BufferReader
      bufferedReader =
          new BufferedReader(new InputStreamReader(System.in));

      String input;
      while ((input = bufferedReader.readLine()) != null) {

        // Printing the read line
        commandManager.process(input, printWriter, false);
      }

      // Close the buffered reader
      bufferedReader.close();

    } catch (IOException e) {

      // This handles IO errors
      System.out.println("ERROR: IO exception");

    } catch (Exception e) {

      // This catches all errors and closes the buffered reader
      try {
        bufferedReader.close();
      } catch (IOException e1) {
        System.out.println("ERROR: IO exception");
      }
      e.printStackTrace();
      System.out.println("ERROR: Program is shutting down");
    }
  }
}
