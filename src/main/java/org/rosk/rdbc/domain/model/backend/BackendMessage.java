package org.rosk.rdbc.domain.model.backend;

public sealed interface BackendMessage permits AuthenticationMessage, BackendKeyData,
    ErrorResponseMessage, ParameterStatus, QueryMessage, ReadyForQuery {
}
