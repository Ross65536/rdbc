package org.rosk.rdbc.domain.model.backend;

import java.util.Map;

public record ErrorResponseMessage(Map<Field, String> fields) implements BackendMessage {

  public enum Field {
    SEVERITY_LOCALIZED,
    SEVERITY_NON_LOCALIZED,
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
