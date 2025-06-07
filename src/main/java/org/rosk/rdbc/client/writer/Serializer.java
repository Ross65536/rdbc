package org.rosk.rdbc.client.writer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.rosk.rdbc.domain.model.frontend.Query;
import org.rosk.rdbc.domain.model.frontend.SASLInitialResponse;
import org.rosk.rdbc.domain.model.frontend.SASLResponse;
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

  static FrontendData serialize(SASLInitialResponse message) throws IOException {
    var bos = new ByteArrayOutputStream();
    var out = new MessageTypesOutputStream(bos);

    out.write(message.selectedMechanism());
    byte[] binary = message.initialResponse().toString().getBytes();
    out.writeNetworkOrder(binary.length);
    out.write(binary);

    var contents = bos.toByteArray();
    return new FrontendData('p', contents);
  }

  static FrontendData serialize(SASLResponse message) throws IOException {
    var bos = new ByteArrayOutputStream();
    var out = new MessageTypesOutputStream(bos);

    byte[] binary = message.finalMessage().toString().getBytes();
    out.write(binary);

    var contents = bos.toByteArray();
    return new FrontendData('p', contents);
  }

  private static final byte PARAMETERS_TERMINATOR = 0x0;
  private static final int PROTOCOL_NUMBER = 0x30000; // v3

  public static FrontendData serialize(Query message) throws IOException {
    var bos = new ByteArrayOutputStream();
    var out = new MessageTypesOutputStream(bos);

    out.write(message.sql());

    var contents = bos.toByteArray();
    return new FrontendData('Q', contents);
  }
}
