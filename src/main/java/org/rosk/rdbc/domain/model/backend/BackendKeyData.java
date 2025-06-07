package org.rosk.rdbc.domain.model.backend;

public record BackendKeyData(int pid, int cancellationSecretKey) implements BackendMessage {
}
