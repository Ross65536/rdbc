package org.rosk;

import java.io.IOException;
import org.rosk.rdbc.client.PostgresClient;
import org.rosk.rdbc.client.PostgresConfiguration;

public class Main {

  public static void main(String[] args) {
    var domain = new PostgresConfiguration.Domain(null, 5432);
    var user = new PostgresConfiguration.User("user", "user");
    var configuration = new PostgresConfiguration(domain, user, "sample");
    try {
      PostgresClient.connect(configuration);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
