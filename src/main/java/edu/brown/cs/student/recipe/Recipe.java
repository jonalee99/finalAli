package edu.brown.cs.student.recipe;

/**
 * This class stores recipes.
 */
public class Recipe {

  private double value;
  private String title;
  private String description;
  private String url;
  private int id;
  private double inventoryVal;
  private int numExpiring;

  /**
   * Constructor for recipe.
   * @param rTitle title
   * @param rDescription description
   * @param rUrl url
   */
  public Recipe(String rTitle, String rDescription, String rUrl) {
    setTitle(rTitle);
    setDescription(rDescription);
    setUrl(rUrl);
    numExpiring = 0;
  }

  /**
   * Setter for title.
   * @param recipeTitle title
   */
  public void setTitle(String recipeTitle) {
    title = recipeTitle;
  }

  /**
   * Setter for description.
   * @param recipeDescription description
   */
  public void setDescription(String recipeDescription) {
    description = recipeDescription;
  }

  /**
   * Setter for URL.
   * @param recipeUrl URL
   */
  public void setUrl(String recipeUrl) {
    url = recipeUrl;
  }

  /**
   * Setter for ID.
   * @param recipeId id
   */
  public void setId(int recipeId) {
    id = recipeId;
  }

  /**
   * Setter for value.
   * @param currRecipeValue value
   */
  public void setValue(double currRecipeValue) {
    value = currRecipeValue;
  }

  /**
   * SEtter for inventory value.
   * @param currRecipeValue value
   */
  public void setInventValue(double currRecipeValue) {
    inventoryVal = currRecipeValue;
  }

  /**
   * Setter for number expired.
   * @param expiring number
   */
  public void setNumExpired(int expiring) {
    numExpiring = expiring;
  }

  /**
   * Getter for title.
   * @return title.
   */
  public String getTitle() {
    return title;
  }

  /**
   * Getter for description.
   * @return description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Getter for URL.
   * @return URL
   */
  public String getUrl() {
    return url;
  }

  /**
   * Getter for ID.
   * @return ID
   */
  public int getId() {
    return id;
  }

  /**
   * Getter for value.
   * @return value
   */
  public double getValue() {
    return value;
  }

  /**
   * Getter for inventory value.
   * @return inventory value
   */
  public double getInventoryVal() {
    return inventoryVal;
  }

  /**
   * Getter for number expired.
   * @return number expired
   */
  public int getNumExpired() {
    return numExpiring;
  }
}
