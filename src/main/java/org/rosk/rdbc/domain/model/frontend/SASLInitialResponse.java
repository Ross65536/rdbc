package org.rosk.rdbc.domain.model.frontend;

import com.ongres.scram.client.ScramClient;
import com.ongres.scram.common.ClientFirstMessage;

public record SASLInitialResponse(String selectedMechanism, ClientFirstMessage initialResponse) {

  public static SASLInitialResponse build(ScramClient scramClient) {
    return new SASLInitialResponse(scramClient.getScramMechanism().getName(), scramClient.clientFirstMessage());
  }
}
