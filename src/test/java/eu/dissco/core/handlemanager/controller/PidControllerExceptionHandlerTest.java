package eu.dissco.core.handlemanager.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
import eu.dissco.core.handlemanager.exceptions.UnprocessableEntityException;
import eu.dissco.core.handlemanager.responses.ExceptionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class PidControllerExceptionHandlerTest {

  private RestResponseEntityExceptionHandler exceptionHandler;
  private static final String ERROR = "Error";

  @BeforeEach
  void setup() {
    exceptionHandler = new RestResponseEntityExceptionHandler();
  }

  @Test
  void testInvalidRecordInput() {
    // Given
    var expectedBody = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(),
        "Invalid Request", ERROR);

    // When
    var result = exceptionHandler.invalidRecordInputException(
        new InvalidRequestException(ERROR));

    // Then
    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(result.getBody()).isEqualTo(expectedBody);
  }

  @Test
  void testPidResolutionException() {
    // Given
    var expectedBody = new ExceptionResponse(HttpStatus.NOT_FOUND.toString(),
        "Unable to Resolve Persistent Identifier", ERROR);

    // When
    var result = exceptionHandler.pidResolutionException(new PidResolutionException(ERROR));

    // Then
    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(result.getBody()).isEqualTo(expectedBody);
  }

  @Test
  void testUnprocessableEntityException() {
    // Given
    var expectedBody = new ExceptionResponse(HttpStatus.UNPROCESSABLE_ENTITY.toString(),
        "Unprocessable Entity Exception", ERROR);

    // When
    var result = exceptionHandler.unprocessableEntityException(
        new UnprocessableEntityException(ERROR));

    // Then
    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    assertThat(result.getBody()).isEqualTo(expectedBody);
  }

  @Test
  void testDatabaseCopyException() {
    // Given
    var expectedBody = new ExceptionResponse(HttpStatus.SERVICE_UNAVAILABLE.toString(),
        "Database Exception", ERROR);

    // When
    var result = exceptionHandler.databaseCopyException(
        new UnprocessableEntityException(ERROR));

    // Then
    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
    assertThat(result.getBody()).isEqualTo(expectedBody);
  }
}
