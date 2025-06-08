package org.rosk.rdbc.serialization.reader;

public record BackendData(char identifier, int length, byte[] contents) {
}
