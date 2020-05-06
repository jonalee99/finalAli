package edu.brown.cs.student.general;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class handles the commands taken from REPL and registers commands given
 * to it from other classes.
 */
public class CommandManager {

  private Map<String, Command> commands;

  /**
   * Instantiates commands.
   */
  public CommandManager() {
    commands = new HashMap<>();
  }

  /**
   * gets the Hashmap of registered commands.
   *
   * @return map of commands
   */
  public Map<String, Command> getCommands() {
    return commands;
  }

  /**
   * This function registers commands so the command manager knows it exists.
   *
   * @param pattern the string that triggers the command in the REPL
   * @param command the command that is executed
   */
  public void register(String pattern, Command command) {
    commands.put(pattern, command);
  }

  /**
   * This function processes user input from the REPL. If the command exists, then
   * it splits the command into the command and flags, then feeds it into the
   * execute function. If it doesn't, it shows an error and a list of valid
   * commands.
   *
   * @param line        This is the command
   * @param printWriter This is where the output is sent to
   * @param gui         This is the gui
   */
  public void process(String line, PrintWriter printWriter, Boolean gui) {

    // Split the initial command from the arguments
    List<String> inputs = Arrays.asList(line.split(" ", 2));

    // Checks if the command exists
    if (commands.containsKey(inputs.get(0))) {

      // If the command is by itself, feed in null as the arguments to execute
      if (inputs.size() == 1) {
        commands.get(inputs.get(0)).execute(null, printWriter, gui);
      } else {

        // Check if the arguments is a blank string
        if (inputs.get(1).trim().isEmpty()) {
          commands.get(inputs.get(0)).execute(null, printWriter, gui);
        } else {
          // Feed in the arguments
          commands.get(inputs.get(0)).execute(inputs.get(1), printWriter, gui);
        }
      }
    } else {

      // If the command doesn't exist
      printWriter.println("ERROR: Invalid command");
    }
  }

  /**
   * This interface lets other classes know what the command manager needs.
   */
  public interface Command {

    /**
     * This method is what is executed when the proper pattern is inputted into
     * REPL.
     *
     * @param tokens These are the parameters
     * @param pw     This is where the output goes
     * @param gui    This is the gui
     */
    void execute(String tokens, PrintWriter pw, Boolean gui);
  }
}
