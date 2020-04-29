package edu.brown.cs.student.recipe;

public class Recipe {
  
  public double value;
  private String title;
  private String description;
  private String url;
  private int id;
  private double inventoryVal;
  
  public Recipe(String rTitle, String rDescription, String rUrl) {
    setTitle(rTitle);
    setDescription(rDescription);
    setUrl(rUrl);
    
  }


  public void setTitle(String recipeTitle) {
    title = recipeTitle;
  }

  public void setDescription(String recipeDescription) {
    description = recipeDescription;
  }

  public void setUrl(String recipeUrl) {
    url = recipeUrl;
  }
  
  public void setId(int recipeId) {
    id = recipeId;
  }
  
  public void setValue(double currRecipeValue) {
    value = currRecipeValue;
  }
  
  public void setInventValue(double currRecipeValue) {
    inventoryVal = currRecipeValue;
  }

  public String getTitle() {
    return title;
  }
  
  public String getDescription() {
    return description;
  }
  
  public String getUrl() {
    return url;
  }
  
  public int getId() {
    return id;
  }
  
  public double getValue() {
    return value;
  }
  
  public double getInventoryVal() {
    return inventoryVal;
  }
  
}
