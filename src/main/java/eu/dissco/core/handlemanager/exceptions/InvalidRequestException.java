package eu.dissco.core.handlemanager.exceptions;

public class InvalidRequestException extends Exception {

  // Response code = 400 BAD REQUEST

  public InvalidRequestException(String s) {
    super(s);
  }

  public InvalidRequestException() {
    super();
  }

}
