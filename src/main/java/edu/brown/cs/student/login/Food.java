package edu.brown.cs.student.login;

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

  /**
   * Constructor for food. Takes in a name and amount.
   */
  public Food(String name, Double amount, String unit, Double daysTillExpiration, String type) {
    this.name = name;
    this.amount = amount;
    this.daysTillExpiration = daysTillExpiration;
    this.type = type;
    this.unit = unit;
  }

  public String getName() {
    return this.name;
  }

  public Double getAmount() {
    return this.amount;
  }

  public Double getDaysTillExpiration() {
    return this.daysTillExpiration;
  }

  public String getType() {
    return this.type;
  }

  public String getUnit() {
    return this.unit;
  }

  @Override
  public String toString() {
    String description = this.name + " | " + this.type + " | " + this.amount + " | " + this.unit + " | " + this.daysTillExpiration;
    return description;
  }

}