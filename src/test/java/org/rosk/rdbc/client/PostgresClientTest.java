package org.rosk.rdbc.client;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.rosk.rdbc.Postgres;
import org.rosk.rdbc.client.PostgresConfiguration.Domain;
import org.rosk.rdbc.client.PostgresConfiguration.User;
import org.rosk.rdbc.message.backend.DataRow;
import org.testcontainers.containers.PostgreSQLContainer;

class PostgresClientTest {

  // avoid conflict with a postgres running locally on 5432 (e.g. from docker-compose)
  private final static int dbPort = new Random().nextInt(1000, 2000);

  private static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
      "postgres:17.5"
  );


  @BeforeAll
  static void beforeAll() {
    postgres.setPortBindings(List.of(dbPort + ":5432"));
    postgres.start();
  }

  @AfterAll
  static void afterAll() {
    postgres.stop();
  }

  @Test
  void should_authenticate() throws IOException {
    // should not throw exception
    connectedClient();
  }

  @Test
  void should_execute_single_query() throws IOException {
    var client = connectedClient();

    var response = client.execute("CREATE TABLE IF NOT EXISTS test_single_query(name TEXT)");
    var expected = List.of(
        QueryResult.ddl("CREATE TABLE")
    );
    Assertions.assertEquals(expected, response);
  }

  @Test
  void should_execute_multiple_queries() throws IOException {
    var client = connectedClient();

    var response = client.execute("""
          CREATE TABLE IF NOT EXISTS test_multi_query(name TEXT);
          INSERT INTO test_multi_query(name) VALUES ('abc'), ('def');
        """);
    var expected = List.of(
        QueryResult.ddl("CREATE TABLE"),
        QueryResult.ddl("INSERT 0 2")
    );
    Assertions.assertEquals(expected, response);
  }

  @Test
  void should_return_selected_data() throws IOException {
    var client = connectedClient();
    client.execute("CREATE TABLE IF NOT EXISTS test_select(name TEXT, num INTEGER)");
    client.execute("TRUNCATE TABLE test_select");
    client.execute("INSERT INTO test_select(name, num) VALUES ('abc', 1), ('def', 2)");

    var response = client.execute("SELECT * FROM test_select");

    Assertions.assertEquals(1, response.size());
    var result = response.get(0);
    Assertions.assertEquals("SELECT 2", result.command());
    Assertions.assertEquals(List.of(
        new DataRow(List.of("abc", "1")),
        new DataRow(List.of("def", "2"))
    ), result.data());
  }

  private PostgresClient connectedClient() throws IOException {
    var config = new PostgresConfiguration(
        new Domain(postgres.getHost(), (short) dbPort),
        new User(postgres.getUsername(), postgres.getPassword()),
        postgres.getDatabaseName()
    );

    // should not throw
    return Postgres.connect(config);
  }
}