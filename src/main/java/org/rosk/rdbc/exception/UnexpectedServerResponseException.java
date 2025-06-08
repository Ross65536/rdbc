package org.rosk.rdbc.exception;

import org.rosk.rdbc.message.backend.BackendMessage;

public class UnexpectedServerResponseException extends RuntimeException {
  public UnexpectedServerResponseException(BackendMessage response) {
    super("Unexpected message received from server: " + response);
  }
}
