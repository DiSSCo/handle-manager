package eu.dissco.core.handlemanager.exceptions;

public class DatabaseCopyException extends RuntimeException {

  // Response code = 503 SERVICE UNAVAILABLE
  public DatabaseCopyException(String s) {
    super(s);
  }

}
