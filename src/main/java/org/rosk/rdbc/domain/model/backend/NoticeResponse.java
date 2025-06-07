package org.rosk.rdbc.domain.model.backend;

import java.util.Map;
import org.rosk.rdbc.domain.model.backend.ErrorResponse.Field;

public record NoticeResponse(Map<Field, String> fields) implements BackendMessage {

}

