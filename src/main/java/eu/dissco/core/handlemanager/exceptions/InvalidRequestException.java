package eu.dissco.core.handlemanager.exceptions;

public class InvalidRequestException extends RuntimeException {

  // Response code = 400 BAD REQUEST
  public InvalidRequestException(String s) {
    super(s);
  }

}
