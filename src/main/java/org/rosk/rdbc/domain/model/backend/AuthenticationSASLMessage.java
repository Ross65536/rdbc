package org.rosk.rdbc.domain.model.backend;

import java.util.List;
import org.rosk.rdbc.client.UnsupportedProtocolFeatureException;

public record AuthenticationSASLMessage(List<String> mechanisms) implements AuthenticationMessage {
  public AuthenticationSASLMessage {
    if (mechanisms.isEmpty()) {
      throw new UnsupportedProtocolFeatureException("Must have at least one SASL mechanism");
    }

    if (!mechanisms.contains("SCRAM-SHA-256") && !mechanisms.contains("SCRAM-SHA-256-PLUS")) {
      throw new UnsupportedProtocolFeatureException("Unsupported SASL mechanism: " + String.join(", ", mechanisms));
    }
  }
}
