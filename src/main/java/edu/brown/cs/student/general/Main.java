package edu.brown.cs.student.general;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
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
    Spark.get("/demeter", new LoginHandler(), freeMarker);
    Spark.get("/demeter/register", new RegisterHandler(), freeMarker);
    Spark.get("/demeter/login", new LoginHandler(), freeMarker);
    Spark.get("/demeter/inventory", new InventoryHandler(), freeMarker);
    Spark.get("/demeter/recipe", new RecipesHandler(), freeMarker);
    Spark.get("/demeter/home", new HomeHandler(), freeMarker);
    Spark.get("/demeter/additem", new AddHandler(), freeMarker);

    // These are post requests
    Spark.post("/demeter/register", new RegisterPost());
    Spark.post("/demeter/login", new LoginPost());
    Spark.post("/demeter/inventory", new InventoryPost());
    Spark.post("/demeter/recipe/:category", new RecipePost());
    Spark.post("/demeter/home", new HomePost());
    Spark.post("/dropdown", new DropdownPost());
    Spark.post("/demeter/foodCategory", new FoodCategoryPost());
    Spark.post("/demeter/food/:food", new CategoryFoodPost());
    Spark.post("/demeter/units/:food", new UnitPost());
    Spark.post("/demeter/add", new AddPost());
    Spark.post("/demeter/delete", new DeletePost());
    Spark.post("/demeter/user", new UserPost());
    Spark.post("/demeter/logout", new LogoutPost());
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
   * This is the post request for register.
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
        Map<String, Object> variables = ImmutableMap.of("content",
            "Username and Password " + "cannot contain spaces");
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
   * This is the post request for login.
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
        Map<String, Object> variables = ImmutableMap.of("content",
            "Username and Password " + "cannot contain spaces");
        return GSON.toJson(variables);
      }

      // Create the command
      String commandString = "login " + username + " " + password;

      // Return the map
      return enterCommand(commandString);
    }
  }

  /**
   * This is the post request for login.
   */
  private class InventoryPost implements Route {

    @Override
    public Object handle(Request request, Response response) throws Exception {
      return enterCommand("inventory");
    }
  }

  /**
   * This is the post request for recipe.
   */
  private class RecipePost implements Route {

    @Override
    public Object handle(Request request, Response response) throws Exception {
      String category = request.params(":category");
      return enterCommand("sort " + category);
    }
  }

  /**
   * This is the post request for home.
   */
  private class HomePost implements Route {

    @Override
    public Object handle(Request request, Response response) throws Exception {
      return enterCommand("expire go");
    }
  }

  /**
   * This is the post request for cuisine.
   */
  private class DropdownPost implements Route {

    @Override
    public Object handle(Request request, Response response) throws Exception {
      return enterCommand("cuisines go");
    }
  }

  /**
   * This is the post request for food category.
   */
  private class FoodCategoryPost implements Route {

    @Override
    public Object handle(Request request, Response response) throws Exception {
      return enterCommand("types go");
    }
  }

  /**
   * This is the post request for food.
   */
  private class CategoryFoodPost implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
      String category = request.params(":food");
      return enterCommand("fot " + category);
    }
  }

  /**
   * This is the post request for food.
   */
  private class UnitPost implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
      String category = request.params(":food");
      return enterCommand("units " + category);
    }
  }

  /**
   * This is the post request for add.
   */
  private class AddPost implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {

      // Receives the parameters
      QueryParamsMap qm = request.queryMap();
      String item = qm.value("name");
      String quantity = qm.value("quantity");
      String units = qm.value("units");
      String expiration = qm.value("expiration");
      String type = qm.value("type");

      String command = "item " + quantity + " " + units
          + " " + type + " " + expiration + " " + item;

      return GSON.toJson(enterCommand(command));
    }
  }

  /**
   * This is the post request for delete.
   */
  private class DeletePost implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {

      // Receives the parameters
      QueryParamsMap qm = request.queryMap();
      String item = qm.value("id");
      String quantity = qm.value("quantity");

      String command = "delete " + item + " " + quantity;

      return GSON.toJson(enterCommand(command));
    }
  }

  /**
   * This is the post request for the logged in user.
   */
  private class UserPost implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
      return GSON.toJson(enterCommand("user"));
    }
  }

  /**
   * This is the post request to logout a user.
   */
  private class LogoutPost implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
      return GSON.toJson(enterCommand("logout"));
    }
  }

  /**
   * Loads the registration page.
   *
   */
  private static class RegisterHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      return new ModelAndView(null, "register.ftl");
    }
  }

  /**
   * Loads the login page.
   */
  private class LoginHandler implements TemplateViewRoute {

    @Override
    public ModelAndView handle(Request req, Response res) {
      return new ModelAndView(null, "login.ftl");
    }
  }

  /**
   * Loads the inventory page.
   */
  private class InventoryHandler implements TemplateViewRoute {

    @Override
    public ModelAndView handle(Request req, Response res) {
      return new ModelAndView(null, "/inventory.ftl");
    }
  }

  /**
   * Loads the recipes page.
   */
  private class RecipesHandler implements TemplateViewRoute {

    @Override
    public ModelAndView handle(Request req, Response res) {
      return new ModelAndView(null, "/recipes.ftl");
    }
  }

  /**
   * Loads the home page.
   */
  private class HomeHandler implements TemplateViewRoute {

    @Override
    public ModelAndView handle(Request req, Response res) {
      return new ModelAndView(null, "/home.ftl");
    }
  }

  /**
   * Loads Add Item page.
   */
  private class AddHandler implements TemplateViewRoute {

    @Override
    public ModelAndView handle(Request req, Response res) {
      return new ModelAndView(null, "/additem.ftl");
    }
  }
}
