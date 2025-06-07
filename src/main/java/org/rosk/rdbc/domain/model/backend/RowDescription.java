package org.rosk.rdbc.domain.model.backend;

import java.util.Map;

public record RowDescription(Map<String, Field> fields) implements BackendMessage {
  public record Field(int tableObjectId, short columnAttributeNumber, int dataTypeObjectId, short dataTypeSize, int typeModifier, short formatCode) {}
}
