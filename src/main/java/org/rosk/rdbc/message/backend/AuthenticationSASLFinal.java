package org.rosk.rdbc.message.backend;

import com.ongres.scram.client.ScramClient;
import com.ongres.scram.common.exception.ScramInvalidServerSignatureException;
import com.ongres.scram.common.exception.ScramParseException;
import com.ongres.scram.common.exception.ScramServerErrorException;
import org.rosk.rdbc.exception.AuthenticationError;

public record AuthenticationSASLFinal(String additionalData) implements AuthenticationMessage {

  public void validate(ScramClient scramClient) {
    try {
      scramClient.serverFinalMessage(additionalData);
    } catch (ScramParseException e) {
      throw new AuthenticationError("Failed to parse server final response", e);
    } catch (ScramServerErrorException e) {
      throw new AuthenticationError("Failed to parse server final response", e);
    } catch (ScramInvalidServerSignatureException e) {
      throw new AuthenticationError("Failed to parse server final response", e);
    }
  }
}
