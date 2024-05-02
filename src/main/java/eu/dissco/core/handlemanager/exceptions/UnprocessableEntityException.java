package eu.dissco.core.handlemanager.exceptions;

public class UnprocessableEntityException extends RuntimeException {

  // Response code = 422 UNPROCESSBLE ENTITY
  public UnprocessableEntityException(String s) {
    super(s);
  }

}
