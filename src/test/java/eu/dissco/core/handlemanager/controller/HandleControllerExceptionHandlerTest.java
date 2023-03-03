package eu.dissco.core.handlemanager.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import eu.dissco.core.handlemanager.exceptions.ExceptionResponse;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import eu.dissco.core.handlemanager.exceptions.PidCreationException;
import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
import eu.dissco.core.handlemanager.exceptions.PidServiceInternalError;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.RECORD_TYPE_DOI;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.RECORD_TYPE_DS;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.RECORD_TYPE_DS_BOTANY;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.RECORD_TYPE_HANDLE;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.RECORD_TYPE_MEDIA;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(properties = "spring.main.lazy-initialization=true")
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
        PidCreationException.class.getSimpleName(), errorMessage);

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
        InvalidRequestException.class.getSimpleName(), errorMessage);

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
        PidResolutionException.class.getSimpleName(), errorMessage);

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
        PidServiceInternalError.class.getSimpleName(), expectedMessage);

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
        PidServiceInternalError.class.getSimpleName(), errorMessage);

    // When
    var result = exceptionHandler.pidServiceInternalError(
        new PidServiceInternalError(errorMessage, null));

    // Then
    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    assertThat(result.getBody()).isEqualTo(expectedBody);
  }
}
