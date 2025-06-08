package org.rosk.rdbc.client;

import java.io.IOException;
import org.rosk.rdbc.message.frontend.FrontendMessage;

public interface Writer {
  void write(FrontendMessage message) throws IOException;
}
