package eu.dissco.core.handlemanager.exceptions;

public class UnprocessableEntityException extends Exception {

  // Response code = 422 UNPROCESSBLE ENTITY
  public UnprocessableEntityException(String s) {
    super(s);
  }

}
