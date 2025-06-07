package org.rosk.rdbc.domain.model.backend;

public sealed interface BackendMessage permits AuthenticationMessage, BackendKeyData,
    CommandComplete, DataRow, ErrorResponse, NoticeResponse, ParameterStatus, ReadyForQuery,
    RowDescription {
}
