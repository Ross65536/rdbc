package org.rosk.rpsql.wire.writer;

record FrontendData(Character identifier, byte[] contents) {

  public int size() {
    // include int32 size (4 bytes)
    return contents.length + 4;
  }
}
