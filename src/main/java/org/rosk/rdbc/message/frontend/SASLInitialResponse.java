package org.rosk.rdbc.message.frontend;

import com.ongres.scram.client.ScramClient;
import com.ongres.scram.common.ClientFirstMessage;

public record SASLInitialResponse(String selectedMechanism, ClientFirstMessage initialResponse) implements FrontendMessage {

  public static SASLInitialResponse build(ScramClient scramClient) {
    return new SASLInitialResponse(scramClient.getScramMechanism().getName(), scramClient.clientFirstMessage());
  }
}
