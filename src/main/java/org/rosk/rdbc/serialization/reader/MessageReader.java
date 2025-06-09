package org.rosk.rdbc.serialization.reader;

import java.io.IOException;
import java.io.InputStream;
import org.rosk.rdbc.client.Reader;
import org.rosk.rdbc.message.backend.BackendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageReader implements Reader {
  private static final Logger LOGGER = LoggerFactory.getLogger(MessageReader.class);

  private final MessageTypeInputStream in;

  public MessageReader(InputStream in) {
    this.in = new MessageTypeInputStream(in);
  }

  public BackendMessage read() throws IOException {
    var data = readData();
    var message = Deserializer.deserialize(data);
    LOGGER.trace("Server response: {}", message);
    return message;
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
