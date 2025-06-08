package org.rosk.rdbc.message.frontend;

public record Query(String sql) implements FrontendMessage {
}
