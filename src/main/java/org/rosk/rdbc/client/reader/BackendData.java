package org.rosk.rdbc.client.reader;

public record BackendData(char identifier, int length, byte[] contents) {
}
