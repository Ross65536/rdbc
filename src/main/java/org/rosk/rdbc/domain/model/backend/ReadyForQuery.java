package org.rosk.rdbc.domain.model.backend;

public record ReadyForQuery(TransactionStatus status) implements BackendMessage {
  public enum TransactionStatus {
    IDLE, TRANSACTION_BLOCK, FAILED_TRANSACTION_BLOCK;
  }
}
