package org.rosk.rdbc.client.writer;

import java.io.IOException;
import java.io.OutputStream;
import org.rosk.rdbc.domain.model.frontend.Query;
import org.rosk.rdbc.domain.model.frontend.SASLInitialResponse;
import org.rosk.rdbc.domain.model.frontend.SASLResponse;
import org.rosk.rdbc.domain.model.frontend.StartupMessage;

public class MessageWriter {
  private final MessageTypesOutputStream out;

  public MessageWriter(OutputStream out) {
    this.out = new MessageTypesOutputStream(out);
  }

  public void write(StartupMessage message) throws IOException {
    System.out.println("Client message: " + message);
    var contents = Serializer.serialize(message);
    write(contents);
  }

  public void write(SASLInitialResponse message) throws IOException {
    System.out.println("Client message: " + message);
    var contents = Serializer.serialize(message);
    write(contents);
  }

  public void write(SASLResponse message) throws IOException {
    System.out.println("Client message: " + message);
    var contents = Serializer.serialize(message);
    write(contents);
  }

  public void write(Query message) throws IOException {
    System.out.println("Client message: " + message);
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
