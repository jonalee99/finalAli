package edu.brown.cs.student.database;

/**
 * This exception is thrown when there are no database is loaded in.
 */
public class NoDatabaseException extends Exception {

  private static final long serialVersionUID = 1L;

  /**
   * Nothing much going on here. Passing message to super.
   *
   * @param message of what the error is
   */
  public NoDatabaseException(String message) {
    super(message);
  }
}
