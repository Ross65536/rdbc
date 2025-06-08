package org.rosk.rdbc.client;

import com.ongres.scram.client.ScramClient;
import java.io.IOException;
import org.rosk.rdbc.exception.UnexpectedServerResponseException;
import org.rosk.rdbc.message.backend.AuthenticationOk;
import org.rosk.rdbc.message.backend.AuthenticationSASL;
import org.rosk.rdbc.message.backend.AuthenticationSASLContinue;
import org.rosk.rdbc.message.backend.AuthenticationSASLFinal;
import org.rosk.rdbc.message.backend.BackendKeyData;
import org.rosk.rdbc.message.backend.CommandComplete;
import org.rosk.rdbc.message.backend.DataRow;
import org.rosk.rdbc.message.backend.NoticeResponse;
import org.rosk.rdbc.message.backend.ParameterStatus;
import org.rosk.rdbc.message.backend.ReadyForQuery;
import org.rosk.rdbc.message.backend.RowDescription;
import org.rosk.rdbc.message.frontend.Query;
import org.rosk.rdbc.message.frontend.SASLInitialResponse;
import org.rosk.rdbc.message.frontend.SASLResponse;
import org.rosk.rdbc.message.frontend.StartupMessage;
import org.rosk.rdbc.client.PostgresConfiguration.User;

/**
 * Implements the Postgres message protocol:
 * https://www.postgresql.org/docs/current/protocol-flow.html
 */
public class PostgresClient {

  private final PostgresConfiguration configuration;
  private final Writer writer;
  private final Reader reader;

  public PostgresClient(PostgresConfiguration configuration, Writer writer,
      Reader reader) {
    this.configuration = configuration;
    this.writer = writer;
    this.reader = reader;
  }

  public void execute(String query) throws IOException {
    writer.write(new Query(query));

    while (true) {
      var message = reader.read();
      switch (message) {
        case RowDescription rows -> System.out.println("Row description: " + rows);
        case DataRow rows -> System.out.println("Row data: " + rows);
        case CommandComplete commandComplete -> System.out.println("Query has completed: " + commandComplete.commandTag());
        case ReadyForQuery ready -> {
          System.out.println(
              "Server is ready for queries with transaction status: " + ready.status());
          return;
        }
        case NoticeResponse notice -> System.out.println("Warning: " + notice);
        default -> throw new UnexpectedServerResponseException(message);
      }
    }
  }

  public void authenticate() throws IOException {
    writer.write(new StartupMessage(configuration.user().user(), configuration.database()));
    var response = read(AuthenticationSASL.class);
    saslAuthenticate(response);
    waitUntilReady();
  }

  private void saslAuthenticate(AuthenticationSASL message)
      throws IOException {
    ScramClient scramClient = scramClient(configuration.user(), message);

    writer.write(SASLInitialResponse.build(scramClient));

    read(AuthenticationSASLContinue.class)
        .validate(scramClient);

    writer.write(new SASLResponse(scramClient.clientFinalMessage()));

    read(AuthenticationSASLFinal.class)
        .validate(scramClient);

    read(AuthenticationOk.class);
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
          System.out.println(
              "Server is ready for queries with transaction status: " + ready.status());
          return;
        }
        default -> throw new UnexpectedServerResponseException(message);
      }
    }
  }

  private <T> T read(Class<T> clazz) throws IOException {
    var message = reader.read();
    if (message.getClass().isAssignableFrom(clazz)) {
      return (T) message;
    }

    throw new UnexpectedServerResponseException(message);
  }

  private static ScramClient scramClient(User user, AuthenticationSASL message) {
    return ScramClient.builder()
        .advertisedMechanisms(message.mechanisms())
        .username(user.user())
        .password(user.password().toCharArray())
        .build();
  }


}

