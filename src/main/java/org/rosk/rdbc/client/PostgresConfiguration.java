package org.rosk.rdbc.client;

public record PostgresConfiguration(String host, int port, String user, String database) {

  public PostgresConfiguration {
    if (port < 0 || port >= 65536) {
      throw new IllegalArgumentException("Invalid port number: " + port);
    }

  }
}
