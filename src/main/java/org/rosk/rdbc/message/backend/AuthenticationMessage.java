package org.rosk.rdbc.message.backend;

public sealed interface AuthenticationMessage extends BackendMessage permits AuthenticationOk,
    AuthenticationSASLContinue, AuthenticationSASLFinal, AuthenticationSASL {
}
