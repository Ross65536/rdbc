package org.rosk.rpsql.client.reader;

import java.io.IOException;
import java.io.InputStream;

public class MessageReader {
  private final InputStream in;

  public MessageReader(InputStream in) {
    this.in = in;
  }

  public void read() throws IOException {
    for (int i = 0; i < 5; i++) {
      // identifier + length
      var b = in.read();
      System.out.println(b);
    }
  }
}
