package org.rosk.rdbc.client;

import org.rosk.rdbc.domain.model.backend.BackendMessage;

public class UnexpectedServerResponseException extends RuntimeException {
  public UnexpectedServerResponseException(BackendMessage response) {
    super("Unexpected message received from server: " + response);
  }
}
