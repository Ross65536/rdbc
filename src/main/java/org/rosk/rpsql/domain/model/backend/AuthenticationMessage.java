package org.rosk.rpsql.domain.model.backend;

public sealed interface AuthenticationMessage extends BackendMessage permits
    SASLAuthenticationMessage {
}
