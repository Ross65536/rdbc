package org.rosk.rpsql.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import org.rosk.rpsql.client.reader.MessageReader;
import org.rosk.rpsql.client.writer.MessageWriter;
import org.rosk.rpsql.domain.model.frontend.StartupMessage;

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
    client.reader.read();

    return client;
  }
}
