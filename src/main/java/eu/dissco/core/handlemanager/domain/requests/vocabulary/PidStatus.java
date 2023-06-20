package eu.dissco.core.handlemanager.domain.requests.vocabulary;

import java.nio.charset.StandardCharsets;

public enum PidStatus {
  ACTIVE("ACTIVE"),
  ARCHIVED("ARCHIVED"),
  DRAFT("DRAFT"),
  RESERVED("RESERVED"),
  TEST("TEST"),
  TEST2("TEST2"),
  ALL("ALL");

  private final String state;

  private PidStatus(String state) {
    this.state = state;
  }

  @Override
  public String toString() {
    return state;
  }

  public byte[] getBytes(){
    return state.getBytes(StandardCharsets.UTF_8);
  }


}
