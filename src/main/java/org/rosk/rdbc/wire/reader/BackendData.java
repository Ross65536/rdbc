package org.rosk.rdbc.wire.reader;

public record BackendData(char identifier, int length, byte[] contents) {
}
