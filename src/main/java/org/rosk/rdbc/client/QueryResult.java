package org.rosk.rdbc.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.rosk.rdbc.message.backend.DataRow;
import org.rosk.rdbc.message.backend.RowDescription;

public record QueryResult(String command, RowDescription header, List<DataRow> data) {

  public static QueryResult ddl(String command) {
    return new QueryResult(command, null, Collections.emptyList());
  }

  public static class Builder {
    private String command = "";
    private RowDescription header = null;
    private final List<DataRow> data = new ArrayList<>();


    public void setHeader(RowDescription header) {
      this.header = header;
    }

    public void append(DataRow line) {
      data.add(line);
    }

    public void setCommand(String command) {
      this.command = command;
    }

    public QueryResult build() {
      return new QueryResult(command, header, data);
    }
  }
}
