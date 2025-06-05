package org.rosk.rpsql.wire.reader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.rosk.rpsql.domain.model.backend.AuthenticationMessage;
import org.rosk.rpsql.domain.model.backend.BackendMessage;
import org.rosk.rpsql.domain.model.backend.SASLAuthenticationMessage;

public class Deserializer {

  static BackendMessage deserialize(BackendData data) throws IOException {
    return switch (data.identifier()) {
      case 'R' -> deserialize(data.contents());
      default -> throw new IllegalStateException(
          "Cannot deserialize message with identifier: " + data.identifier());
    };
  }

  static AuthenticationMessage deserialize(byte[] contents) throws IOException {
    var bis = new ByteArrayInputStream(contents);
    var in = new MessageTypeInputStream(bis);

    int subtype = in.readNetworkOrderInt32();

    return switch (subtype) {
      case SASL_AUTH_TYPE -> new SASLAuthenticationMessage(in.readCStringList());
      default -> throw new IllegalStateException(
          "Cannot deserialize authentication message subtype: " + subtype);
    };
  }

  private static final int SASL_AUTH_TYPE = 10;
}
