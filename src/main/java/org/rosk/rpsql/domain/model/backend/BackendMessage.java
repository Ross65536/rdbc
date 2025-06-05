package org.rosk.rpsql.domain.model.backend;

public sealed interface BackendMessage permits QueryMessage, AuthenticationMessage {
}
