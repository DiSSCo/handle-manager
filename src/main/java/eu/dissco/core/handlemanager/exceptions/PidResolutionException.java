package eu.dissco.core.handlemanager.exceptions;

public class PidResolutionException extends RuntimeException {

  // Response code = 404 NOT FOUND
  public PidResolutionException(String s) {
    super(s);
  }

}
