package eu.dissco.core.handlemanager.exceptions;

public class PidCreationException extends Exception {

  // Response code = 500 INTERNAL SERVICE ERROR
  public PidCreationException(String s) {
    super(s);
  }

}
