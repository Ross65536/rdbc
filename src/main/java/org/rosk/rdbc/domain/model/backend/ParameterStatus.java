package org.rosk.rdbc.domain.model.backend;

public record ParameterStatus(String parameter, String value) implements BackendMessage {
}
