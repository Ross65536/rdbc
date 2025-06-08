package org.rosk.rdbc.message.frontend;

import com.ongres.scram.common.ClientFinalMessage;

public record SASLResponse(ClientFinalMessage finalMessage) implements FrontendMessage {
}
