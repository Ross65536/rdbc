package org.rosk.rdbc.message.frontend;

import java.util.Objects;

/**
 * https://www.postgresql.org/docs/current/protocol-message-formats.html#PROTOCOL-MESSAGE-FORMATS-STARTUPMESSAGE
 */
public record StartupMessage(String user, String database) implements FrontendMessage {

  public StartupMessage {
    Objects.requireNonNull(user, "user is mandatory");
  }
}
