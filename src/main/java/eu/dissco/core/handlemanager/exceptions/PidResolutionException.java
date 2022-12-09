package eu.dissco.core.handlemanager.exceptions;

public class PidResolutionException extends Exception {

  // Response code = 404 NOT FOUND
  public PidResolutionException(String s) {
    super(s);
  }

}
