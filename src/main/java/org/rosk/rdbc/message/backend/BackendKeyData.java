package org.rosk.rdbc.message.backend;

public record BackendKeyData(int pid, int cancellationSecretKey) implements BackendMessage {
}
