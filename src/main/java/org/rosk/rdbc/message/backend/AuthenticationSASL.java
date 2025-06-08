package org.rosk.rdbc.message.backend;

import java.util.List;
import org.rosk.rdbc.exception.UnsupportedProtocolFeatureException;

public record AuthenticationSASL(List<String> mechanisms) implements AuthenticationMessage {
  public AuthenticationSASL {
    if (mechanisms.isEmpty()) {
      throw new UnsupportedProtocolFeatureException("Must have at least one SASL mechanism");
    }

    if (!mechanisms.contains("SCRAM-SHA-256") && !mechanisms.contains("SCRAM-SHA-256-PLUS")) {
      throw new UnsupportedProtocolFeatureException("Unsupported SASL mechanism: " + String.join(", ", mechanisms));
    }
  }
}
