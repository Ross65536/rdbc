package org.rosk.rdbc.message.backend;

import java.util.Map;

public record ErrorResponse(Map<Field, String> fields) implements BackendMessage {

  public enum Field {
    SEVERITY,
    SEVERITY_LOCALIZED,
    SQLSTATE_CODE,
    MESSAGE,
    DETAIL,
    HINT,
    POSITION,
    INTERNAL_POSITION,
    INTERNAL_QUERY,
    WHERE,
    SCHEMA_NAME,
    TABLE_NAME,
    COLUMN_NAME,
    DATA_TYPE_NAME,
    CONSTRAINT_NAME,
    FILE_NAME,
    LINE,
    ROUTINE,
  }
}
