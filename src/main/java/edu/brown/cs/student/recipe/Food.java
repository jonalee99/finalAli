package edu.brown.cs.student.recipe;

/**
 * This class represents a food. Stores its name, amount, and days until expiration.
 */
public class Food {

  // These store the name and amount
  private String name;
  private Double amount;
  private Double daysTillExpiration;
  private String type;
  private String unit;
  private Integer id;

  /**
   * Constructor for food.
   * @param name of food
   * @param amount of food
   * @param unit of food
   * @param daysTillExpiration of food
   * @param type of food
   * @param id of food
   */
  public Food(String name, Double amount, String unit, Double daysTillExpiration,
      String type, Integer id) {
    this.name = name;
    this.amount = amount;
    this.daysTillExpiration = daysTillExpiration;
    this.type = type;
    this.unit = unit;
    this.id = id;
  }

  /**
   * getter for ID.
   * @return id
   */
  public Integer getId() {
    return this.id;
  }

  /**
   * Getter for name.
   * @return name
   */
  public String getName() {
    return this.name;
  }

  /**
   * getter for amount.
   * @return amount
   */
  public Double getAmount() {
    return this.amount;
  }

  /**
   * Getter for days until expiration.
   * @return daystillexpiration
   */
  public Double getDaysTillExpiration() {
    return this.daysTillExpiration;
  }

  /**
   * Getter for type.
   * @return type
   */
  public String getType() {
    return this.type;
  }

  /**
   * Getter for units.
   * @return units
   */
  public String getUnit() {
    return this.unit;
  }

  @Override
  public String toString() {
    String description = this.name + " | " + this.type + " | "
        + this.amount + " | " + this.unit + " | " + this.daysTillExpiration;
    return description;
  }
}
