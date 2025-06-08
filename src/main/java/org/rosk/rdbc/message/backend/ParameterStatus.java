package org.rosk.rdbc.message.backend;

public record ParameterStatus(String parameter, String value) implements BackendMessage {
}
