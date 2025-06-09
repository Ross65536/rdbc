package org.rosk.rdbc.client;

public record PostgresConfiguration(Domain domain, User user, String database) {
  public record Domain(String host, int port) {
    public Domain {
      if (port < 0 || port >= 65536) {
        throw new IllegalArgumentException("Invalid port number: " + port);
      }
    }
  }
  public record User(String user, String password) {}

  @Override
  public String toString() {
    return String.format("%s@%s:%d/%s",
        user.user, domain.host == null ? "localhost" : domain.host, domain.port, database
    );
  }
}
