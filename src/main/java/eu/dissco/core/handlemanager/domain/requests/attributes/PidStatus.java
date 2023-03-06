package eu.dissco.core.handlemanager.domain.requests.attributes;

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



}
