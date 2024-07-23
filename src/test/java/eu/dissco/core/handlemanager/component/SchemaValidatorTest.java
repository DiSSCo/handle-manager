package eu.dissco.core.handlemanager.component;

import static eu.dissco.core.handlemanager.testUtils.TestUtils.MAPPER;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenAnnotation;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDataMapping;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDigitalMedia;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDigitalSpecimen;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDoiKernel;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenHandleKernel;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenMas;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenOrganisation;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenPatchRequest;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenPostRequest;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenSourceSystem;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion.VersionFlag;
import eu.dissco.core.handlemanager.domain.fdo.FdoType;
import eu.dissco.core.handlemanager.domain.requests.PatchRequest;
import eu.dissco.core.handlemanager.domain.requests.PostRequest;
import eu.dissco.core.handlemanager.domain.requests.PostRequestData;
import eu.dissco.core.handlemanager.domain.validation.SchemaLibrary;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import eu.dissco.core.handlemanager.properties.JsonSchemaProperties;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SchemaValidatorTest {

  SchemaValidator schemaValidator;

  @BeforeEach
  void init() throws IOException {
    var library = schemaLibrary();
    schemaValidator = new SchemaValidator(library);
  }

  SchemaLibrary schemaLibrary() throws IOException {
    var factory = JsonSchemaFactory.getInstance(VersionFlag.V202012);
    JsonSchemaProperties pathProperties = new JsonSchemaProperties();
    return new SchemaLibrary(
        setSchema(pathProperties.getHandleSchemaPath(), factory),
        setSchema(pathProperties.getDoiSchemaPath(), factory),
        setSchema(pathProperties.getAnnotationSchemaPath(), factory),
        setSchema(pathProperties.getDataMappingSchemaPath(), factory),
        setSchema(pathProperties.getDigitalMediaSchemaPath(), factory),
        setSchema(pathProperties.getDigitalSpecimenSchemaPath(), factory),
        setSchema(pathProperties.getMasSchemaPath(), factory),
        setSchema(pathProperties.getOrganisationSchemaPath(), factory),
        setSchema(pathProperties.getSourceSystemPath(), factory)
    );
  }

  private JsonSchema setSchema(String schema, JsonSchemaFactory factory) throws IOException {
    try (var input = this.getClass().getClassLoader().getResourceAsStream(schema)) {
      return factory.getSchema(input);
    }
  }

  @ParameterizedTest
  @MethodSource("validPostRequest")
  void testValidatePostSchemas(PostRequest request) {
    // When
    assertDoesNotThrow(() -> schemaValidator.validatePost(List.of(request)));
  }

  @ParameterizedTest
  @MethodSource("validPatchRequest")
  void testValidatePatchSchemas(PatchRequest request) {
    // When
    assertDoesNotThrow(() -> schemaValidator.validatePatch(List.of(request)));
  }

  @Test
  void testMissingRequiredAttribute() {
    // Given
    var request = new PostRequest(new PostRequestData(FdoType.HANDLE, MAPPER.createObjectNode()));

    // Then
    var e = assertThrows(InvalidRequestException.class,
        () -> schemaValidator.validatePost(List.of(request)));

    // Then
    assertThat(e.getMessage()).contains("Missing attributes");
  }

  @Test
  void testUnknownAttribute() {
    // Given
    var request = new PostRequest(new PostRequestData(FdoType.HANDLE, MAPPER.createObjectNode()
        .put("bad field!", "bad val!")));

    // Then
    var e = assertThrows(InvalidRequestException.class,
        () -> schemaValidator.validatePost(List.of(request)));

    // Then
    assertThat(e.getMessage()).contains("Unrecognized attributes");
  }

  @Test
  void testEnumError() {
    // Given
    var requestAttributes = ((ObjectNode) MAPPER.valueToTree(givenDigitalSpecimen()))
        .put("topicDiscipline", "bad val!");
    var request = new PostRequest(new PostRequestData(FdoType.DIGITAL_SPECIMEN, requestAttributes));

    // Then
    var e = assertThrows(InvalidRequestException.class,
        () -> schemaValidator.validatePost(List.of(request)));

    // Then
    assertThat(e.getMessage()).contains("Enum errors");
  }


  private static Stream<Arguments> validPostRequest() {
    return Stream.of(
        Arguments.of(givenPostRequest(givenHandleKernel(), FdoType.HANDLE)),
        Arguments.of(givenPostRequest(givenDoiKernel(), FdoType.DOI)),
        Arguments.of(givenPostRequest(givenAnnotation(true), FdoType.ANNOTATION)),
        Arguments.of(givenPostRequest(givenDataMapping(), FdoType.DATA_MAPPING)),
        Arguments.of(givenPostRequest(givenDigitalMedia(), FdoType.DIGITAL_MEDIA)),
        Arguments.of(givenPostRequest(givenDigitalSpecimen(), FdoType.DIGITAL_SPECIMEN)),
        Arguments.of(givenPostRequest(givenMas(), FdoType.MAS)),
        Arguments.of(givenPostRequest(givenOrganisation(), FdoType.ORGANISATION)),
        Arguments.of(givenPostRequest(givenSourceSystem(), FdoType.SOURCE_SYSTEM))
    );
  }

  private static Stream<Arguments> validPatchRequest() {
    return Stream.of(
        Arguments.of(givenPatchRequest(givenHandleKernel(), FdoType.HANDLE)),
        Arguments.of(givenPatchRequest(givenDoiKernel(), FdoType.DOI)),
        Arguments.of(givenPatchRequest(givenAnnotation(true), FdoType.ANNOTATION)),
        Arguments.of(givenPatchRequest(givenDataMapping(), FdoType.DATA_MAPPING)),
        Arguments.of(givenPatchRequest(givenDigitalMedia(), FdoType.DIGITAL_MEDIA)),
        Arguments.of(givenPatchRequest(givenDigitalSpecimen(), FdoType.DIGITAL_SPECIMEN)),
        Arguments.of(givenPatchRequest(givenMas(), FdoType.MAS)),
        Arguments.of(givenPatchRequest(givenOrganisation(), FdoType.ORGANISATION)),
        Arguments.of(givenPatchRequest(givenSourceSystem(), FdoType.SOURCE_SYSTEM))
    );
  }


}
