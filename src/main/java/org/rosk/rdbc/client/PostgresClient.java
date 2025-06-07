package org.rosk.rdbc.client;

import com.ongres.scram.client.ScramClient;
import com.ongres.scram.common.exception.ScramInvalidServerSignatureException;
import com.ongres.scram.common.exception.ScramParseException;
import com.ongres.scram.common.exception.ScramServerErrorException;
import java.io.IOException;
import java.net.Socket;
import org.rosk.rdbc.client.PostgresConfiguration.User;
import org.rosk.rdbc.client.reader.MessageReader;
import org.rosk.rdbc.client.writer.MessageWriter;
import org.rosk.rdbc.domain.model.backend.AuthenticationOk;
import org.rosk.rdbc.domain.model.backend.AuthenticationSASLContinueMessage;
import org.rosk.rdbc.domain.model.backend.AuthenticationSASLFinal;
import org.rosk.rdbc.domain.model.backend.AuthenticationSASLMessage;
import org.rosk.rdbc.domain.model.backend.BackendKeyData;
import org.rosk.rdbc.domain.model.backend.ParameterStatus;
import org.rosk.rdbc.domain.model.backend.ReadyForQuery;
import org.rosk.rdbc.domain.model.frontend.SASLInitialResponse;
import org.rosk.rdbc.domain.model.frontend.SASLResponse;
import org.rosk.rdbc.domain.model.frontend.StartupMessage;

/**
 * Implements the Postgres message protocol:
 * https://www.postgresql.org/docs/current/protocol-flow.html
 */
public class PostgresClient {

  private final PostgresConfiguration configuration;
  private final MessageWriter writer;
  private final MessageReader reader;

  public PostgresClient(PostgresConfiguration configuration, MessageWriter writer, MessageReader reader) {
    this.configuration = configuration;
    this.writer = writer;
    this.reader = reader;
  }

  private void authenticate() throws IOException {
    writer.write(new StartupMessage(configuration.user().user(), configuration.database()));
    var response = reader.read();

    if (response instanceof AuthenticationSASLMessage authMessage) {
      saslAuthenticate(authMessage);
    } else {
      throw new UnexpectedServerResponseException(response);
    }
  }

  private void saslAuthenticate(AuthenticationSASLMessage message)
      throws IOException {
    ScramClient scramClient = scramClient(configuration.user(), message);

    var initialRequest = new SASLInitialResponse(scramClient.getScramMechanism().getName(), scramClient.clientFirstMessage());
    writer.write(initialRequest);

    var initialResponse = reader.read();

    if (!(initialResponse instanceof AuthenticationSASLContinueMessage(String saslData))) {
      throw new UnexpectedServerResponseException(initialResponse);
    }

    try {
      scramClient.serverFirstMessage(saslData);
    } catch (ScramParseException e) {
      throw new AuthenticationError("Failed to parse server first response", e);
    }

    var finalRequest = new SASLResponse(scramClient.clientFinalMessage());
    writer.write(finalRequest);

    var finalResponse = reader.read();
    if (!(finalResponse instanceof AuthenticationSASLFinal(String finalSaslMessage))) {
      throw new UnexpectedServerResponseException(finalResponse);
    }

    try {
      scramClient.serverFinalMessage(finalSaslMessage);
    } catch (ScramParseException e) {
      throw new AuthenticationError("Failed to parse server final response", e);
    } catch (ScramServerErrorException e) {
      throw new AuthenticationError("Failed to parse server final response", e);
    } catch (ScramInvalidServerSignatureException e) {
      throw new AuthenticationError("Failed to parse server final response", e);
    }

    var okResponse = reader.read();
    if (!(okResponse instanceof AuthenticationOk)) {
      throw new UnexpectedServerResponseException(okResponse);
    }
  }

  private void waitUntilReady() throws IOException {
    while (true) {
      var message = reader.read();
      switch (message) {
        case ParameterStatus status -> System.out.println(status);
        case BackendKeyData keyData -> {
          // TODO: implement cancellation request from operator
        }
        case ReadyForQuery ready -> {
          System.out.println("Server is ready for queries with transaction status: " + ready.status());
          return;
        }
        default -> throw new UnexpectedServerResponseException(message);
      }
    }
  }

  private static ScramClient scramClient(User user, AuthenticationSASLMessage message) {
    return ScramClient.builder()
        .advertisedMechanisms(message.mechanisms())
        .username(user.user())
        .password(user.password().toCharArray())
        .build();
  }

  public static void connect(PostgresConfiguration configuration) throws IOException {
    var socket = new Socket(configuration.domain().host(), configuration.domain().port());

    var writer = new MessageWriter(socket.getOutputStream());
    var reader = new MessageReader(socket.getInputStream());
    var client = new PostgresClient(configuration, writer, reader);
    client.authenticate();
    client.waitUntilReady();
  }
}

