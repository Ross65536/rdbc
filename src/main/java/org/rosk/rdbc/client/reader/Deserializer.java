package org.rosk.rdbc.client.reader;

import static java.util.Map.entry;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import org.rosk.rdbc.client.UnsupportedProtocolFeatureException;
import org.rosk.rdbc.domain.model.backend.BackendKeyData;
import org.rosk.rdbc.domain.model.backend.ParameterStatus;
import org.rosk.rdbc.domain.model.backend.AuthenticationMessage;
import org.rosk.rdbc.domain.model.backend.AuthenticationOk;
import org.rosk.rdbc.domain.model.backend.AuthenticationSASLContinueMessage;
import org.rosk.rdbc.domain.model.backend.AuthenticationSASLFinal;
import org.rosk.rdbc.domain.model.backend.BackendMessage;
import org.rosk.rdbc.domain.model.backend.ErrorResponseMessage;
import org.rosk.rdbc.domain.model.backend.ErrorResponseMessage.Field;
import org.rosk.rdbc.domain.model.backend.AuthenticationSASLMessage;
import org.rosk.rdbc.domain.model.backend.ReadyForQuery;
import org.rosk.rdbc.domain.model.backend.ReadyForQuery.TransactionStatus;

public class Deserializer {

  static BackendMessage deserialize(BackendData data) throws IOException {
    return switch (data.identifier()) {
      case 'R' -> deserializeAuthenticationMessage(data.contents());
      case 'E' -> deserializeError(data.contents());
      case 'S' -> deserializeParameter(data.contents());
      case 'K' -> deserializeBackendKeyData(data.contents());
      case 'Z' -> desrializeReadyForQuery(data.contents());
      default -> throw new UnsupportedProtocolFeatureException(
          "Cannot deserialize message with identifier: " + data.identifier());
    };
  }

  private static ParameterStatus deserializeParameter(byte[] contents) throws IOException {
    var bis = new ByteArrayInputStream(contents);
    var in = new MessageTypeInputStream(bis);

    String parameter = in.readCString();
    String value = in.readCString();

    return new ParameterStatus(parameter, value);
  }

  private static Map<Character, ErrorResponseMessage.Field> ERROR_FIELD_TYPE_CODES =
      Map.ofEntries(
          entry('S', Field.SEVERITY_LOCALIZED),
          entry('V', Field.SEVERITY_NON_LOCALIZED),
          entry('C', Field.SQLSTATE_CODE),
          entry('M', Field.MESSAGE),
          entry('D', Field.DETAIL),
          entry('H', Field.HINT),
          entry('P', Field.POSITION),
          entry('p', Field.INTERNAL_POSITION),
          entry('q', Field.INTERNAL_QUERY),
          entry('W', Field.WHERE),
          entry('s', Field.SCHEMA_NAME),
          entry('t', Field.TABLE_NAME),
          entry('c', Field.COLUMN_NAME),
          entry('d', Field.DATA_TYPE_NAME),
          entry('n', Field.CONSTRAINT_NAME),
          entry('F', Field.FILE_NAME),
          entry('L', Field.LINE),
          entry('R', Field.ROUTINE)
      );

  private static ErrorResponseMessage deserializeError(byte[] contents) throws IOException {
    var bis = new ByteArrayInputStream(contents);
    var in = new MessageTypeInputStream(bis);

    Map<Field, String> fields = new EnumMap<>(Field.class);
    for (char code = in.readCharacter(); code != ERROR_FIELDS_TERMINATOR;
        code = in.readCharacter()) {
      String value = in.readCString();
      Field field = ERROR_FIELD_TYPE_CODES.get(code);
      if (field == null) {
        System.out.println("Unknown ERROR_RESPONSE field type code: " + code + ". Skipping.");
        continue;
      }

      fields.put(field, value);
    }

    return new ErrorResponseMessage(fields);
  }


  private static final int AUTHENTICATION_OK_AUTH_TYPE = 0;
  private static final int SASL_AUTH_TYPE = 10;
  private static final int SASL_CONTINUE_AUTH_TYPE = 11;
  private static final int SASL_FINAL_AUTH_TYPE = 12;
  private static final char ERROR_FIELDS_TERMINATOR = 0x0;

  private static AuthenticationMessage deserializeAuthenticationMessage(byte[] contents)
      throws IOException {
    var bis = new ByteArrayInputStream(contents);
    var in = new MessageTypeInputStream(bis);

    int subtype = in.readNetworkOrderInt32();

    return switch (subtype) {
      case AUTHENTICATION_OK_AUTH_TYPE -> new AuthenticationOk();
      case SASL_AUTH_TYPE -> new AuthenticationSASLMessage(in.readCStringList());
      case SASL_CONTINUE_AUTH_TYPE -> new AuthenticationSASLContinueMessage(in.readAllAsString());
      case SASL_FINAL_AUTH_TYPE -> new AuthenticationSASLFinal(in.readAllAsString());
      default -> throw new UnsupportedProtocolFeatureException(
          "Cannot deserialize authentication message subtype: " + subtype);
    };
  }


  private static BackendKeyData deserializeBackendKeyData(byte[] contents) throws IOException {
    var bis = new ByteArrayInputStream(contents);
    var in = new MessageTypeInputStream(bis);

    int pid = in.readNetworkOrderInt32();
    int secret = in.readNetworkOrderInt32();

    return new BackendKeyData(pid, secret);
  }

  private static ReadyForQuery desrializeReadyForQuery(byte[] contents) throws IOException {
    var bis = new ByteArrayInputStream(contents);
    var in = new MessageTypeInputStream(bis);

    int code = in.readCharacter();

    var status = switch (code) {
      case 'I' -> TransactionStatus.IDLE;
      case 'T' -> TransactionStatus.TRANSACTION_BLOCK;
      case 'E' -> TransactionStatus.FAILED_TRANSACTION_BLOCK;
      default -> throw new UnsupportedProtocolFeatureException(
          "Unknown transaction statuc indicator: " + code);
    };

    return new ReadyForQuery(status);
  }
}
