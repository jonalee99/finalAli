package edu.brown.cs.student.general;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import spark.*;
import spark.template.freemarker.FreeMarkerEngine;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

import freemarker.template.Configuration;

/**
 * The Main class of our project. This is where execution begins.
 *
 */
public final class Main {

  private static final int DEFAULT_PORT = 4567;
  private static final Gson GSON = new Gson();

  private Repl repl;

  /**
   * The initial method called when execution begins.
   *
   * @param args An array of command line arguments
   */
  public static void main(String[] args) {
    new Main(args).run();
  }

  private String[] args;

  private Main(String[] args) {
    this.args = args;
  }

  private void run() {
    // Parse command line arguments
    OptionParser parser = new OptionParser();
    parser.accepts("gui");
    parser.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo(DEFAULT_PORT);
    OptionSet options = parser.parse(args);

    if (options.has("gui")) {
      runSparkServer((int) options.valueOf("port"));
    }

    // Create a new instance of Repl and start running it
    repl = new Repl();
    repl.runRepl();
  }

  private static FreeMarkerEngine createEngine() {
    Configuration config = new Configuration();
    File templates = new File("src/main/resources/spark/template/freemarker");
    try {
      config.setDirectoryForTemplateLoading(templates);
    } catch (IOException ioe) {
      System.out.printf("ERROR: Unable use %s for template loading.%n", templates);
      System.exit(1);
    }
    return new FreeMarkerEngine(config);
  }

  private void runSparkServer(int port) {
    Spark.port(port);
    Spark.externalStaticFileLocation("src/main/resources/static");
    Spark.exception(Exception.class, new ExceptionPrinter());

    FreeMarkerEngine freeMarker = createEngine();

    // These load the pages
    Spark.get("/demeter/register", new RegisterHandler(), freeMarker);
    Spark.get("/demeter/login", new LoginHandler(), freeMarker);
    Spark.get("/demeter/inventory", new InventoryHandler(), freeMarker);
    Spark.get("/demeter/recipe", new RecipesHandler(), freeMarker);
    Spark.get("/demeter/home", new HomeHandler(), freeMarker);

    // These are post requests
    Spark.post("/demeter/register", new RegisterPost());
    Spark.post("/demeter/login", new LoginPost());
    Spark.post("/demeter/inventory", new InventoryPost());
    Spark.post("/demeter/recipe/:category", new RecipePost());
    Spark.post("/demeter/home", new HomePost());
    Spark.post("/dropdown", new DropdownPost());
  }

  /**
   * Handle requests to the front page of our Stars website.
   *
   */
  private static class FrontHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      return new ModelAndView(null, "register.ftl");
    }
  }

  /**
   * Display an error page when an exception occurs in the server.
   *
   */
  private static class ExceptionPrinter implements ExceptionHandler {
    @Override
    public void handle(Exception e, Request req, Response res) {
      res.status(500);
      StringWriter stacktrace = new StringWriter();
      try (PrintWriter pw = new PrintWriter(stacktrace)) {
        pw.println("<pre>");
        e.printStackTrace(pw);
        pw.println("</pre>");
      }
      res.body(stacktrace.toString());
    }
  }

  /**
   * This method inputs the command to the repl and returns the output as a
   * String.
   * 
   * @param command line input
   * @return string output
   */
  private String enterCommand(String command) {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    repl.getCommandManager().process(command, printWriter, true);
    String output = stringWriter.toString();
    printWriter.close();
    try {
      stringWriter.close();
    } catch (IOException e) {
      System.out.println("IOError");
    }
    return output;
  }

  /**
   * This is the post request for register
   */
  private class RegisterPost implements Route {

    @Override
    public Object handle(Request request, Response response) throws Exception {

      // Receives the parameters
      QueryParamsMap qm = request.queryMap();
      String name = qm.value("name");
      String username = qm.value("username");
      String password = qm.value("password");

      // If the username or password has spaces
      if (username.split(" ").length != 1 || password.split(" ").length != 1) {
        Map<String, Object> variables = ImmutableMap.of("content", "Username and Password " + "cannot contain spaces");
        return GSON.toJson(variables);
      }

      // Create the command
      String commandString = "register " + username + " " + password + " " + name;

      // Input the command and get the output
      String stringOutput = enterCommand(commandString);

      // Return the map
      Map<String, Object> variables = ImmutableMap.of("content", stringOutput);
      return GSON.toJson(variables);
    }
  }

  /**
   * This is the post request for login
   */
  private class LoginPost implements Route {

    @Override
    public Object handle(Request request, Response response) throws Exception {

      // Receives the parameters
      QueryParamsMap qm = request.queryMap();
      String username = qm.value("username");
      String password = qm.value("password");

      // If the username or password has spaces
      if (username.split(" ").length != 1 || password.split(" ").length != 1) {
        Map<String, Object> variables = ImmutableMap.of("content", "Username and Password " + "cannot contain spaces");
        return GSON.toJson(variables);
      }

      // Create the command
      String commandString = "login " + username + " " + password;

      // Input the command and get the output
      String stringOutput = enterCommand(commandString);

      // Return the map
      Map<String, Object> variables = ImmutableMap.of("content", stringOutput);
      return GSON.toJson(variables);
    }
  }

  /**
   * This is the post request for login
   */
  private class InventoryPost implements Route {

    @Override
    public Object handle(Request request, Response response) throws Exception {
      return enterCommand("inventory");
    }
  }

  /**
   * This is the post request for recipe
   */
  private class RecipePost implements Route {

    @Override
    public Object handle(Request request, Response response) throws Exception {
      String category = request.params(":category");
      return enterCommand("sort " + category);
    }
  }

  /**
   * This is the post request for home
   */
  private class HomePost implements Route {

    @Override
    public Object handle(Request request, Response response) throws Exception {
      return enterCommand("expire go");
    }
  }

  /**
   * This is the post request for cuisine
   */
  private class DropdownPost implements Route {

    @Override
    public Object handle(Request request, Response response) throws Exception {
      return enterCommand("cuisines go");
    }
  }

  /**
   * Loads the registration page
   *
   */
  private static class RegisterHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      return new ModelAndView(null, "register.ftl");
    }
  }

  /**
   * Loads the login page
   */
  private class LoginHandler implements TemplateViewRoute {

    @Override
    public ModelAndView handle(Request req, Response res) {
      return new ModelAndView(null, "login.ftl");
    }
  }

  /**
   * Loads the inventory page
   */
  private class InventoryHandler implements TemplateViewRoute {

    @Override
    public ModelAndView handle(Request req, Response res) {
      return new ModelAndView(null, "/inventory.ftl");
    }
  }

  /**
   * Loads the recipes page
   */
  private class RecipesHandler implements TemplateViewRoute {

    @Override
    public ModelAndView handle(Request req, Response res) {
      return new ModelAndView(null, "/recipes.ftl");
    }
  }

  /**
   * Loads the home page
   */
  private class HomeHandler implements TemplateViewRoute {

    @Override
    public ModelAndView handle(Request req, Response res) {
      return new ModelAndView(null, "/home.ftl");
    }
  }
}
