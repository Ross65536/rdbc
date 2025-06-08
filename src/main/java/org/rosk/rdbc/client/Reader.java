package org.rosk.rdbc.client;

import java.io.IOException;
import org.rosk.rdbc.message.backend.BackendMessage;

public interface Reader {
  BackendMessage read() throws IOException;
}
