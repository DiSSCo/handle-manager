package eu.dissco.core.handlemanager.controller;

import eu.dissco.core.handlemanager.exceptions.ExceptionResponse;
import eu.dissco.core.handlemanager.exceptions.InvalidRecordInput;
import eu.dissco.core.handlemanager.exceptions.PidCreationException;
import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
import eu.dissco.core.handlemanager.exceptions.PidServiceInternalError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

  @ResponseStatus(HttpStatus.CONFLICT)
  @ExceptionHandler(PidCreationException.class)
  public ResponseEntity<ExceptionResponse> pidCreationException(PidCreationException e) {
    ExceptionResponse exceptionResponse = new ExceptionResponse(
        String.valueOf(HttpStatus.CONFLICT), e.getClass().getSimpleName(), e.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(exceptionResponse);
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(InvalidRecordInput.class)
  public ResponseEntity<ExceptionResponse> invalidRecordInputException(InvalidRecordInput e) {
    ExceptionResponse exceptionResponse = new ExceptionResponse(
        String.valueOf(HttpStatus.BAD_REQUEST), e.getClass().getSimpleName(), e.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(PidResolutionException.class)
  public ResponseEntity<ExceptionResponse> pidResolutionException(PidResolutionException e) {
    ExceptionResponse exceptionResponse = new ExceptionResponse(
        String.valueOf(HttpStatus.NOT_FOUND), e.getClass().getSimpleName(), e.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionResponse);
  }

  @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
  @ExceptionHandler(PidServiceInternalError.class)
  public ResponseEntity<ExceptionResponse> pidServiceInternalError(PidServiceInternalError e) {
    String message;
    if (e.getCause() != null) {
      message = e.getMessage() + ". Cause: " + e.getCause().toString() + "\n " + e.getCause()
          .getLocalizedMessage();
    } else {
      message = e.getMessage();
    }
    ExceptionResponse exceptionResponse = new ExceptionResponse(
        String.valueOf(HttpStatus.UNPROCESSABLE_ENTITY), e.getClass().getSimpleName(), message);
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(exceptionResponse);
  }


}
