package org.rosk.rdbc.message.backend;

import java.util.Map;
import org.rosk.rdbc.message.backend.ErrorResponse.Field;

public record NoticeResponse(Map<Field, String> fields) implements BackendMessage {

}

