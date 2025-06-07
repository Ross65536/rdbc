package org.rosk.rdbc.client.reader;

import java.io.DataInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * InputStream decorator for https://www.postgresql.org/docs/current/protocol-message-types.html
 */
public class MessageTypeInputStream extends FilterInputStream {
  protected MessageTypeInputStream(InputStream in) {
    super(in);
  }

  /**
   * read 1-byte ASCII character
   */
  char readCharacter() throws IOException {
    return (char) in.read();
  }

  int readNetworkOrderInt32() throws IOException {
    var dis = new DataInputStream(in);
    return dis.readInt();
  }

  short readNetworkOrderInt16() throws IOException {
    var dis = new DataInputStream(in);
    return dis.readShort();
  }

  List<String> readCStringList() throws IOException {
    var result = new ArrayList<String>();

    for (int i = 0; i < MAX_LIST_SIZE; i++) {
      var s = readCString();
      if (s.isEmpty()) {
        return result;
      }

      result.add(s);
    }

    throw new IllegalStateException("List of C-Strings is too large: " + MAX_LIST_SIZE);
  }

  String readAllAsString() throws IOException {
    byte[] binary = in.readAllBytes();
    return new String(binary, StandardCharsets.US_ASCII);
  }

  String readCString() throws IOException {
    var builder = new StringBuilder();

    for (int i = 0; i < MAX_C_STRING_SIZE; i++) {
      var b =  in.read();
      if (b == C_STRING_TERMINATOR) {
        return builder.toString();
      }

      builder.append((char) b);
    }

    throw new IllegalStateException("Reading too many bytes for C-String: " + MAX_C_STRING_SIZE);
  }

  private static final byte C_STRING_TERMINATOR = 0x0;
  private static final int MAX_C_STRING_SIZE = 64 * 1024;
  private static final int MAX_LIST_SIZE = 256;

  public String readString(int numBytes) throws IOException {
    byte[] binary = in.readNBytes(numBytes);
    return new String(binary, StandardCharsets.US_ASCII);
  }
}
