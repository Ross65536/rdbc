package org.rosk.rdbc.domain.model.backend;

import com.ongres.scram.client.ScramClient;
import com.ongres.scram.common.exception.ScramParseException;
import org.rosk.rdbc.domain.model.exception.AuthenticationError;

public record AuthenticationSASLContinue(String saslData) implements AuthenticationMessage {
  public void validate(ScramClient scramClient) {
    try {
      scramClient.serverFirstMessage(saslData);
    } catch (ScramParseException e) {
      throw new AuthenticationError("Invalid first server SASL response", e);
    }
  }
}
