package org.rosk.rdbc.client.reader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.rosk.rdbc.domain.model.backend.AuthenticationSASL;

class MessageReaderTest {

  @Test
  void should_deserialize_AuthenticationSASL_message() throws IOException {
    var input = new byte[]{
        // Byte1('R'): Identifies the message as an authentication request.
        'R',
        // Int32: Length of message contents in bytes, including self.
        0x00, 0x00, 0x00, (4 + 4 + 14 + 1),
        // Int32(10): Specifies that SASL authentication is required.
        0x00, 0x00, 0x00, 10,
        // The message body is a list of SASL authentication mechanisms, in the server's order of preference. A zero byte is required as terminator after the last authentication mechanism name. For each mechanism, there is the following:
        // String: Name of a SASL authentication mechanism.
        'S', 'C', 'R', 'A', 'M', '-', 'S', 'H', 'A', '-', '2', '5', '6', 0x00,
        0x00
    };
    var in = new ByteArrayInputStream(input);
    var reader = new MessageReader(in);
    var actual = reader.read();
    var expected = new AuthenticationSASL(List.of("SCRAM-SHA-256"));

    Assertions.assertEquals(expected, actual);
  }
}
