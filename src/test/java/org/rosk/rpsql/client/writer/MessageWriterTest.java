package org.rosk.rpsql.client.writer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.rosk.rpsql.domain.model.frontend.StartupMessage;


class MessageWriterTest {

  @Test
  void should_send_startup_message() throws IOException {
    // see https://www.postgresql.org/docs/current/protocol-message-formats.html#PROTOCOL-MESSAGE-FORMATS-STARTUPMESSAGE

    var out = new ByteArrayOutputStream();
    var writer = new MessageWriter(out);
    writer.write(new StartupMessage("u", "db"));
    var actual = out.toByteArray();

    var expected = new byte[] {
        // Int32: Length of message contents in bytes, including self. total: 28 bytes
        0x00, 0x00, 0x00, (4 + 4 + 7 + 12 + 1),
        // Int32(196608): The protocol version number. The most significant 16 bits are the major version number (3 for the protocol described here). The least significant 16 bits are the minor version number (0 for the protocol described here). (4 bytes)
        0x00, 0x03, 0x00, 0x00,
        // The protocol version number is followed by one or more pairs of parameter name and value strings [...] Parameters can appear in any order
        // user parameter (5 + 2 bytes)
        'u', 's', 'e', 'r', 0x00,
        'u', 0x00,
        // database parameter (9 + 3 bytes)
        'd', 'a', 't', 'a', 'b', 'a', 's', 'e', 0x00,
        'd', 'b', 0x00,
        // A zero byte is required as a terminator after the last name/value pair. (1 byte)
        0x00
    };

    Assertions.assertArrayEquals(expected, actual);
  }
}