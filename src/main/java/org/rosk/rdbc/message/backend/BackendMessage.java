package org.rosk.rdbc.message.backend;

public sealed interface BackendMessage permits AuthenticationMessage, BackendKeyData,
    CommandComplete, DataRow, ErrorResponse, NoticeResponse, ParameterStatus, ReadyForQuery,
    RowDescription {
}
