package edu.brown.cs.student.general;

/**
 * This exception is thrown when there are invalid arguments put in.
 */
public class InvalidArgsException extends Exception {

  private static final long serialVersionUID = 1L;

  /**
   * Nothing much going on here. Passing message to super.
   *
   * @param message of what the error is
   */
  public InvalidArgsException(String message) {
    super(message);
  }
}
