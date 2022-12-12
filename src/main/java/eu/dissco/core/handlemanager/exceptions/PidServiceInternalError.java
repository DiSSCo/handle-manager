package eu.dissco.core.handlemanager.exceptions;

public class PidServiceInternalError extends Exception {

  // Response code = 500 INTERNAL SERVICE ERROR
  private final Throwable exceptionCause;

  public PidServiceInternalError(String s) {
    super(s);
    this.exceptionCause = null;
  }

  public PidServiceInternalError(String s, Throwable exceptionCause) {
    super(s);
    this.exceptionCause = exceptionCause;
  }

  public Throwable getExceptionCause() {
    return exceptionCause;
  }
}
