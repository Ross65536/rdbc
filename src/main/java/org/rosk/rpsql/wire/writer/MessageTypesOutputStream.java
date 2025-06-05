package org.rosk.rpsql.wire.writer;

import java.io.DataOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * OutputStream decorator for https://www.postgresql.org/docs/current/protocol-message-types.html
 */
class MessageTypesOutputStream extends FilterOutputStream {
  public MessageTypesOutputStream(OutputStream outputStream) {
    super(outputStream);
  }

  void write(String string) throws IOException {
    write(string.getBytes(StandardCharsets.US_ASCII));
    write(C_STRING_TERMINATOR);
  }

  void writeNetworkOrder(int int32) throws IOException {
    var dos = new DataOutputStream(out);
    dos.writeInt(int32);
    dos.flush();
  }

  private static final byte C_STRING_TERMINATOR = 0x0;
}
