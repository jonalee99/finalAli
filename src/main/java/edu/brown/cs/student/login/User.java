package edu.brown.cs.student.login;

/**
 * This class represents a user.
 */
public class User {

  private String fullname;
  private String username;
  private String password;

  /**
   * Set these to null by default.
   */
  User() {
    this.fullname = null;
    this.username = null;
    this.password = null;
  }


  /**
   * Setter for fullname.
   * @param fullname of user
   */
  public void setFullname(String fullname) {
    this.fullname = fullname;
  }

  /**
   * Setter for username.
   * @param username of user
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * Setter for password.
   * @param password of user
   */
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * Getter for fullname.
   */
  public String getFullname() {
    return this.fullname;
  }

  /**
   * Getter for username.
   */
  public String getUsername() {
    return this.username;
  }

  /**
   * Getter for password.
   */
  public String getPassword() {
    return this.password;
  }
}
