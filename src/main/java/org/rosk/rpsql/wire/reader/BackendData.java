package org.rosk.rpsql.wire.reader;

public record BackendData(char identifier, int length, byte[] contents) {
}
