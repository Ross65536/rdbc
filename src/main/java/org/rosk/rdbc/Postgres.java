package org.rosk.rdbc;

import java.io.IOException;
import java.net.Socket;
import org.rosk.rdbc.client.PostgresClient;
import org.rosk.rdbc.client.PostgresConfiguration;
import org.rosk.rdbc.serialization.reader.MessageReader;
import org.rosk.rdbc.serialization.writer.MessageWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Postgres {
  private static final Logger LOGGER = LoggerFactory.getLogger(Postgres.class);

  public static PostgresClient connect(PostgresConfiguration configuration) throws IOException {
    var socket = new Socket(configuration.domain().host(), configuration.domain().port());

    var writer = new MessageWriter(socket.getOutputStream());
    var reader = new MessageReader(socket.getInputStream());
    var client = new PostgresClient(configuration, writer, reader);
    client.authenticate();

    LOGGER.info("Connected to Postgres server with parametes: {}", configuration);
    return client;
  }
}
