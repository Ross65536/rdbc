package org.rosk;

import java.io.IOException;
import org.rosk.rdbc.client.PostgresClient;
import org.rosk.rdbc.client.PostgresConfiguration;

public class Main {

  public static void main(String[] args) {
    var configuration = new PostgresConfiguration(null, 5432, "user", "sample");
    try {
      PostgresClient.connect(configuration, "user");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
