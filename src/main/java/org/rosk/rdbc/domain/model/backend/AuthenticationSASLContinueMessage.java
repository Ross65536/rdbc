package org.rosk.rdbc.domain.model.backend;

public record AuthenticationSASLContinueMessage(String saslData) implements AuthenticationMessage {
}
