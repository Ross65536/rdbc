package org.rosk;

import java.io.IOException;
import org.rosk.rpsql.client.PostgresClient;

public class Main {

  public static void main(String[] args) {
    try {
      var client = PostgresClient.connect(null, 5432, "user", "sample");

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
