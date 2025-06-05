package org.rosk.rpsql.wire.reader;

import java.io.IOException;
import java.io.InputStream;
import org.rosk.rpsql.domain.model.backend.BackendMessage;

public class MessageReader {

  private final MessageTypeInputStream in;

  public MessageReader(InputStream in) {
    this.in = new MessageTypeInputStream(in);
  }

  public BackendMessage read() throws IOException {
    var data = readData();
    return Deserializer.deserialize(data);
  }

  private BackendData readData() throws IOException {
    var identifier = in.readCharacter();

    var size = in.readNetworkOrderInt32() - 4;
    if (size > MAX_MESSAGE_SIZE) {
      throw new IllegalStateException("Server sent a message too large: " + size);
    }
    if (size < 0) {
      throw new IllegalStateException("Impossible message contents size: " + size);
    }

    var contents = in.readNBytes(size);

    return new BackendData(identifier, size, contents);
  }

  private static final int MAX_MESSAGE_SIZE = 1024 * 1024; // 1 MiB
}
