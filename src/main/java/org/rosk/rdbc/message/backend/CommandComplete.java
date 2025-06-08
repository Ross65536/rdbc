package org.rosk.rdbc.message.backend;

public record CommandComplete(String commandTag) implements BackendMessage {

}
