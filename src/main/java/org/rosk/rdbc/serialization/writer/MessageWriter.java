package org.rosk.rdbc.serialization.writer;

import java.io.IOException;
import java.io.OutputStream;
import org.rosk.rdbc.message.frontend.FrontendMessage;
import org.rosk.rdbc.message.frontend.Query;
import org.rosk.rdbc.message.frontend.SASLInitialResponse;
import org.rosk.rdbc.message.frontend.SASLResponse;
import org.rosk.rdbc.message.frontend.StartupMessage;
import org.rosk.rdbc.client.Writer;

public class MessageWriter implements Writer {
  private final MessageTypesOutputStream out;

  public MessageWriter(OutputStream out) {
    this.out = new MessageTypesOutputStream(out);
  }

  @Override
  public void write(FrontendMessage message) throws IOException {
    System.out.println("request: " + message);

    FrontendData contents = switch (message) {
      case StartupMessage startupMessage -> Serializer.serialize(startupMessage);
      case SASLInitialResponse saslInitialResponse -> Serializer.serialize(saslInitialResponse);
      case SASLResponse saslResponse -> Serializer.serialize(saslResponse);
      case Query query -> Serializer.serialize(query);
    };

    write(contents);
  }

  private void write(FrontendData data) throws IOException {
    if (data.identifier() != null) {
      out.write(data.identifier());
    }

    out.writeNetworkOrder(data.size());
    out.write(data.contents());
  }
}
