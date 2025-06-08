package org.rosk;

import java.io.IOException;
import org.rosk.rdbc.Postgres;
import org.rosk.rdbc.client.PostgresConfiguration;

public class Main {

  public static void main(String[] args) {
    var domain = new PostgresConfiguration.Domain(null, 5432);
    var user = new PostgresConfiguration.User("user", "user");
    var configuration = new PostgresConfiguration(domain, user, "sample");
    try {
      var client = Postgres.connect(configuration);
      client.execute("CREATE TABLE IF NOT EXISTS sample(name TEXT)");
      client.execute("TRUNCATE TABLE sample");
      client.execute("INSERT INTO sample VALUES ('abc'), ('def')");
      client.execute("SELECT * FROM sample");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
