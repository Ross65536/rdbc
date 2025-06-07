package org.rosk.rdbc.wire.writer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.rosk.rdbc.domain.model.frontend.StartupMessage;

class Serializer {

  static FrontendData serialize(StartupMessage message) throws IOException {
    var bos = new ByteArrayOutputStream();
    var out = new MessageTypesOutputStream(bos);

    out.writeNetworkOrder(PROTOCOL_NUMBER);

    // "Parameters can appear in any order. user is required, others are optional"

    out.write("user");
    out.write(message.user());

    if (message.database() != null) {
      out.write("database");
      out.write(message.database());
    }

    out.write(PARAMETERS_TERMINATOR);

    var contents = bos.toByteArray();
    return new FrontendData(null, contents);
  }

  private static final byte PARAMETERS_TERMINATOR = 0x0;
  private static final int PROTOCOL_NUMBER = 0x30000; // v3
}
