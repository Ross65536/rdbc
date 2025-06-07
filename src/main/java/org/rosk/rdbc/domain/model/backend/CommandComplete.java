package org.rosk.rdbc.domain.model.backend;

public record CommandComplete(String commandTag) implements BackendMessage {

}
