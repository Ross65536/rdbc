package org.rosk.rdbc.wire;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import org.rosk.rdbc.wire.reader.MessageReader;
import org.rosk.rdbc.wire.writer.MessageWriter;
import org.rosk.rdbc.domain.model.frontend.StartupMessage;

/**
 * Implements the Postgres message protocol: https://www.postgresql.org/docs/current/protocol-flow.html
 */
public class PostgresClient {
  private final MessageWriter writer;
  private final MessageReader reader;

  private PostgresClient(InputStream in, OutputStream out) {
    this.writer = new MessageWriter(out);
    this.reader = new MessageReader(in);
  }

  public static PostgresClient connect(String host, int port, String user, String database) throws IOException {
    var socket = new Socket(host, port);
    var client = new PostgresClient(socket.getInputStream(), socket.getOutputStream());

    client.writer.write(new StartupMessage(user, database));
    var response = client.reader.read();
    System.out.println(response);

    return client;
  }
}
