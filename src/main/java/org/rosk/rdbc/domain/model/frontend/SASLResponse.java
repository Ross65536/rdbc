package org.rosk.rdbc.domain.model.frontend;

import com.ongres.scram.common.ClientFinalMessage;

public record SASLResponse(ClientFinalMessage finalMessage) {
}
