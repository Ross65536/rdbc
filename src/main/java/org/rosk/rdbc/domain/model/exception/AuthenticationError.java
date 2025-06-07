package org.rosk.rdbc.domain.model.exception;

public class AuthenticationError extends RuntimeException {

  public AuthenticationError(String message) {
    super(message);
  }

  public AuthenticationError(String message, Throwable cause) {
    super(message, cause);
  }
}
