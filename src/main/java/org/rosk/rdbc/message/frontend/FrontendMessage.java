package org.rosk.rdbc.message.frontend;

public sealed interface FrontendMessage permits Query, SASLInitialResponse, SASLResponse,
    StartupMessage {

}
