package org.rosk.rdbc.exception;

public class AuthenticationError extends RuntimeException {
  public AuthenticationError(String message, Throwable cause) {
    super(message, cause);
  }
}
