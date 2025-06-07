package org.rosk.rdbc.domain.model.backend;

import java.util.List;

public record SASLAuthenticationMessage(List<String> mechanisms) implements AuthenticationMessage {

}
