package org.rosk.rdbc.serialization.reader;

import static java.util.Map.entry;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.rosk.rdbc.exception.UnsupportedProtocolFeatureException;
import org.rosk.rdbc.message.backend.AuthenticationMessage;
import org.rosk.rdbc.message.backend.AuthenticationOk;
import org.rosk.rdbc.message.backend.AuthenticationSASL;
import org.rosk.rdbc.message.backend.AuthenticationSASLContinue;
import org.rosk.rdbc.message.backend.AuthenticationSASLFinal;
import org.rosk.rdbc.message.backend.BackendKeyData;
import org.rosk.rdbc.message.backend.BackendMessage;
import org.rosk.rdbc.message.backend.CommandComplete;
import org.rosk.rdbc.message.backend.DataRow;
import org.rosk.rdbc.message.backend.ErrorResponse;
import org.rosk.rdbc.message.backend.ErrorResponse.Field;
import org.rosk.rdbc.message.backend.NoticeResponse;
import org.rosk.rdbc.message.backend.ParameterStatus;
import org.rosk.rdbc.message.backend.ReadyForQuery;
import org.rosk.rdbc.message.backend.ReadyForQuery.TransactionStatus;
import org.rosk.rdbc.message.backend.RowDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Deserializer {
  private Deserializer() {}

  private static final Logger LOGGER = LoggerFactory.getLogger(Deserializer.class);

  static BackendMessage deserialize(BackendData data) throws IOException {
    return switch (data.identifier()) {
      case 'R' -> deserializeAuthenticationMessage(data.contents());
      case 'E' -> deserializeErrorResponse(data.contents());
      case 'N' -> deserializeNoticeResponse(data.contents());
      case 'S' -> deserializeParameter(data.contents());
      case 'K' -> deserializeBackendKeyData(data.contents());
      case 'Z' -> desrializeReadyForQuery(data.contents());
      case 'T' -> deserializeRowDescription(data.contents());
      case 'D' -> deserializeDataRow(data.contents());
      case 'C' -> deserializeCommandComplete(data.contents());
      default -> throw new UnsupportedProtocolFeatureException(
          "Cannot deserialize message with identifier: " + data.identifier());
    };
  }

  private static CommandComplete deserializeCommandComplete(byte[] contents) throws IOException {
    var bis = new ByteArrayInputStream(contents);
    var in = new MessageTypeInputStream(bis);

    String commandTag = in.readCString();

    return new CommandComplete(commandTag);
  }

  private static RowDescription deserializeRowDescription(byte[] contents) throws IOException {
    var bis = new ByteArrayInputStream(contents);
    var in = new MessageTypeInputStream(bis);

    short numFields = in.readNetworkOrderInt16();
    Map<String, RowDescription.Field> fields = HashMap.newHashMap(numFields);
    for (int i = 0; i < numFields; i++) {
      String fieldName = in.readCString();
      int tableOid = in.readNetworkOrderInt32();
      short columnAttributeNumber = in.readNetworkOrderInt16();
      int dataTypeOid = in.readNetworkOrderInt32();
      short dataTypeSize = in.readNetworkOrderInt16();
      int typeModifier = in.readNetworkOrderInt32();
      short formatCode = in.readNetworkOrderInt16();

      var fieldMetadata = new RowDescription.Field(tableOid, columnAttributeNumber, dataTypeOid, dataTypeSize, typeModifier, formatCode);
      fields.put(fieldName, fieldMetadata);
    }

    return new RowDescription(fields);
  }

  private static DataRow deserializeDataRow(byte[] contents) throws IOException {
    var bis = new ByteArrayInputStream(contents);
    var in = new MessageTypeInputStream(bis);

    short numFields = in.readNetworkOrderInt16();
    List<String> rows = new ArrayList<>(numFields);
    for (int i = 0; i < numFields; i++) {
      int size = in.readNetworkOrderInt32();
      String value = in.readString(size);
      rows.add(value);
    }

    return new DataRow(rows);
  }

  private static ParameterStatus deserializeParameter(byte[] contents) throws IOException {
    var bis = new ByteArrayInputStream(contents);
    var in = new MessageTypeInputStream(bis);

    String parameter = in.readCString();
    String value = in.readCString();

    return new ParameterStatus(parameter, value);
  }

  private static final Map<Character, ErrorResponse.Field> ERROR_FIELD_TYPE_CODES =
      Map.ofEntries(
          entry('S', Field.SEVERITY_LOCALIZED),
          entry('V', Field.SEVERITY),
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

  private static ErrorResponse deserializeErrorResponse(byte[] contents) throws IOException {
    var bis = new ByteArrayInputStream(contents);
    var in = new MessageTypeInputStream(bis);

    Map<Field, String> fields = deserializeLogFields(in);

    return new ErrorResponse(fields);
  }

  private static NoticeResponse deserializeNoticeResponse(byte[] contents) throws IOException {
    var bis = new ByteArrayInputStream(contents);
    var in = new MessageTypeInputStream(bis);

    Map<Field, String> fields = deserializeLogFields(in);

    return new NoticeResponse(fields);
  }

  private static Map<Field, String> deserializeLogFields(MessageTypeInputStream in)
      throws IOException {
    Map<Field, String> fields = new EnumMap<>(Field.class);
    for (char code = in.readCharacter(); code != ERROR_FIELDS_TERMINATOR;
        code = in.readCharacter()) {
      String value = in.readCString();
      Field field = ERROR_FIELD_TYPE_CODES.get(code);
      if (field == null) {
        LOGGER.warn("Unknown ERROR_RESPONSE field type code: {}. Skipping.", code);
        continue;
      }

      fields.put(field, value);
    }
    return fields;
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
      case SASL_AUTH_TYPE -> new AuthenticationSASL(in.readCStringList());
      case SASL_CONTINUE_AUTH_TYPE -> new AuthenticationSASLContinue(in.readAllAsString());
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
          "Unknown transaction status indicator: " + code);
    };

    return new ReadyForQuery(status);
  }
}
