package org.rosk.rdbc.client;

import com.ongres.scram.client.ScramClient;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

  public List<QueryResult> execute(String query) throws IOException {
    writer.write(new Query(query));

    // TODO: handle correctly when query contains multiple commands
    var results = new ArrayList<QueryResult>();
    var currentResult = new QueryResult.Builder();
    while (true) {
      var message = reader.read();
      switch (message) {
        case RowDescription header -> {
          currentResult.setHeader(header);
        }
        case DataRow data -> {
          currentResult.append(data);
        }
        case CommandComplete(String commandTag) -> {
          System.out.println("SQL Command '" + commandTag + "' has completed");
          currentResult.setCommand(commandTag);
          results.add(currentResult.build());
          currentResult = new QueryResult.Builder();
        }
        case ReadyForQuery ready -> {
          System.out.println(
              "Server is ready for queries with transaction status: " + ready.status());
          return results;
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
