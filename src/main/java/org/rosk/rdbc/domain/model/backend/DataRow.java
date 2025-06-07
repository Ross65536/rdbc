package org.rosk.rdbc.domain.model.backend;

import java.util.List;

public record DataRow(List<String> cells) implements BackendMessage {

}
