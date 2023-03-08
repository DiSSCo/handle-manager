package eu.dissco.core.handlemanager.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import eu.dissco.core.handlemanager.exceptions.PidCreationException;
import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
import eu.dissco.core.handlemanager.exceptions.PidServiceInternalError;
import eu.dissco.core.handlemanager.responses.ExceptionResponse;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class HandleControllerExceptionHandlerTest {

  private RestResponseEntityExceptionHandler exceptionHandler;
  private static final String errorMessage = "Error";

  @BeforeEach
  void setup() {
    exceptionHandler = new RestResponseEntityExceptionHandler();
  }

  @Test
  void testPidCreationException() throws Exception {
    // Given
    var expectedBody = new ExceptionResponse(HttpStatus.CONFLICT.toString(),
        "Unable to Create PID Record", errorMessage);

    // When
    var result = exceptionHandler.pidCreationException(new PidCreationException(errorMessage));

    // Then
    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    assertThat(result.getBody()).isEqualTo(expectedBody);
  }

  @Test
  void testInvalidRecordInput() throws Exception {
    // Given
    var expectedBody = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(),
        "Invalid Request", errorMessage);

    // When
    var result = exceptionHandler.invalidRecordInputException(new InvalidRequestException(errorMessage));

    // Then
    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(result.getBody()).isEqualTo(expectedBody);
  }

  @Test
  void testPidResolutionException() throws Exception {
    // Given
    var expectedBody = new ExceptionResponse(HttpStatus.NOT_FOUND.toString(),
        "Unable to Resolve Persistent Identifier", errorMessage);

    // When
    var result = exceptionHandler.pidResolutionException(new PidResolutionException(errorMessage));

    // Then
    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(result.getBody()).isEqualTo(expectedBody);
  }

  @Test
  void testPidServiceInternalErrorWithCause() throws Exception {
    // Given
    var cause = new IOException(errorMessage);
    var expectedMessage = errorMessage + ". Cause: " + cause + "\n " + cause.getLocalizedMessage();
    var expectedBody = new ExceptionResponse(HttpStatus.UNPROCESSABLE_ENTITY.toString(),
        "Unprocessable Entity", expectedMessage);

    // When
    var result = exceptionHandler.pidServiceInternalError(
        new PidServiceInternalError(errorMessage, cause));

    // Then
    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    assertThat(result.getBody()).isEqualTo(expectedBody);
  }

  @Test
  void testPidServiceInternalErrorNullCause() throws Exception {
    // Given
    var expectedBody = new ExceptionResponse(HttpStatus.UNPROCESSABLE_ENTITY.toString(),
        "Unprocessable Entity", errorMessage);

    // When
    var result = exceptionHandler.pidServiceInternalError(
        new PidServiceInternalError(errorMessage, null));

    // Then
    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    assertThat(result.getBody()).isEqualTo(expectedBody);
  }
}
