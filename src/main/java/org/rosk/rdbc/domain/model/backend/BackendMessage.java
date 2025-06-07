package org.rosk.rdbc.domain.model.backend;

public sealed interface BackendMessage permits QueryMessage, AuthenticationMessage {
}
