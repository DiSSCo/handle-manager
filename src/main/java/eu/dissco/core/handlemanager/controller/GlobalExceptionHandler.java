package eu.dissco.core.handlemanager.controller;

import eu.dissco.core.handlemanager.exceptions.ExceptionResponse;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(ConversionFailedException.class)
  public ResponseEntity<ExceptionResponse> badEnum(ConversionFailedException e){
    var unrecognizedVal = String.valueOf(e.getValue());
    var targetType = e.getTargetType().getType().getSimpleName();

    ExceptionResponse exceptionResponse = new ExceptionResponse(
        String.valueOf(HttpStatus.BAD_REQUEST),
        "InvalidRecordInputException",
        String.format("Unable to process request. Value \"%s\" for attribute \"%s\" is not recognized. More information: %s", unrecognizedVal, targetType, e.getMessage())
    );
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
  }


}
