package org.rosk.rdbc.client;

import java.io.IOException;
import java.net.Socket;
import org.rosk.rdbc.domain.model.backend.SASLAuthenticationMessage;
import org.rosk.rdbc.domain.model.frontend.StartupMessage;
import org.rosk.rdbc.client.reader.MessageReader;
import org.rosk.rdbc.client.writer.MessageWriter;

/**
 * Implements the Postgres message protocol:
 * https://www.postgresql.org/docs/current/protocol-flow.html
 */
public class PostgresClient {

  private final MessageWriter writer;
  private final MessageReader reader;

  public PostgresClient(MessageWriter writer, MessageReader reader) {
    this.writer = writer;
    this.reader = reader;
  }

  private void authenticate(PostgresConfiguration configuration, String password)
      throws IOException {
    writer.write(new StartupMessage(configuration.user(), configuration.database()));
    var response = reader.read();
    System.out.println(response);

    if (response instanceof SASLAuthenticationMessage) {
      saslAuthenticate();
    } else {
      throw new IllegalStateException("Unexpected message from server: " + response);
    }
  }

  private void saslAuthenticate() {

  }

  public static void connect(PostgresConfiguration configuration, String password)
      throws IOException {
    var socket = new Socket(configuration.host(), configuration.port());

    var writer = new MessageWriter(socket.getOutputStream());
    var reader = new MessageReader(socket.getInputStream());
    var client = new PostgresClient(writer, reader);

  }
}

