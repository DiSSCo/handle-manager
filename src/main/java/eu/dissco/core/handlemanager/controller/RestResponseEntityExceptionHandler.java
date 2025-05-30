package eu.dissco.core.handlemanager.controller;

import eu.dissco.core.handlemanager.exceptions.DatabaseCopyException;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
import eu.dissco.core.handlemanager.exceptions.UnprocessableEntityException;
import eu.dissco.core.handlemanager.responses.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {


  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(InvalidRequestException.class)
  public ResponseEntity<ExceptionResponse> invalidRecordInputException(InvalidRequestException e) {
    ExceptionResponse exceptionResponse = new ExceptionResponse(
        String.valueOf(HttpStatus.BAD_REQUEST), "Invalid Request", e.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(PidResolutionException.class)
  public ResponseEntity<ExceptionResponse> pidResolutionException(PidResolutionException e) {
    ExceptionResponse exceptionResponse = new ExceptionResponse(
        String.valueOf(HttpStatus.NOT_FOUND), "Unable to Resolve Persistent Identifier",
        e.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionResponse);
  }

  @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
  @ExceptionHandler(UnprocessableEntityException.class)
  public ResponseEntity<ExceptionResponse> unprocessableEntityException(
      UnprocessableEntityException e) {
    var exceptionResponse = new ExceptionResponse(String.valueOf(HttpStatus.UNPROCESSABLE_ENTITY),
        "Unprocessable Entity Exception", e.getMessage());
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(exceptionResponse);
  }

  @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
  @ExceptionHandler(DatabaseCopyException.class)
  public ResponseEntity<ExceptionResponse> databaseCopyException(
      UnprocessableEntityException e) {
    var exceptionResponse = new ExceptionResponse(String.valueOf(HttpStatus.SERVICE_UNAVAILABLE),
        "Database Exception", e.getMessage());
    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(exceptionResponse);
  }

  @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
  @ExceptionHandler(UnsupportedOperationException.class)
  public ResponseEntity<ExceptionResponse> unsupportedOperationException(
      UnsupportedOperationException e) {
    var exceptionResponse = new ExceptionResponse(String.valueOf(HttpStatus.METHOD_NOT_ALLOWED),
        "Unsupported Operation", e.getMessage());
    return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(exceptionResponse);
  }

}
