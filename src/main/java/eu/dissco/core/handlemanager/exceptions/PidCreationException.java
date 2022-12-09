package eu.dissco.core.handlemanager.exceptions;

import org.springframework.http.HttpStatus;

public class PidCreationException extends Exception {

  // Response code = 500 INTERNAL SERVICE ERROR

  public PidCreationException(String s) {
    super(s);
  }

}
