package org.rosk.rdbc.domain.model.backend;

public sealed interface AuthenticationMessage extends BackendMessage permits
    SASLAuthenticationMessage {
}
