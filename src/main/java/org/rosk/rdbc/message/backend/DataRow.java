package org.rosk.rdbc.message.backend;

import java.util.List;

public record DataRow(List<String> cells) implements BackendMessage {

}
