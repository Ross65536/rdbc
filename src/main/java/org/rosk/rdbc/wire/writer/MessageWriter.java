package org.rosk.rdbc.wire.writer;

import java.io.IOException;
import java.io.OutputStream;
import org.rosk.rdbc.domain.model.frontend.StartupMessage;

public class MessageWriter {
  private final MessageTypesOutputStream out;

  public MessageWriter(OutputStream out) {
    this.out = new MessageTypesOutputStream(out);
  }

  public void write(StartupMessage message) throws IOException {
    var contents = Serializer.serialize(message);
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
