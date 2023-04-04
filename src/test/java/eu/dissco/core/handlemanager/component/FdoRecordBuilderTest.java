package eu.dissco.core.handlemanager.component;

import static eu.dissco.core.handlemanager.testUtils.TestUtils.DOC_BUILDER_FACTORY;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.TRANSFORMER_FACTORY;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genDigitalSpecimenBotanyRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genDigitalSpecimenRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genDoiRecordRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genHandleRecordRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genMediaRequestObject;
import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FdoRecordBuilderTest {
  private FdoRecordBuilder fdoRecordBuilder;

  private final byte[] handle = HANDLE.getBytes(StandardCharsets.UTF_8);

  @BeforeEach
  void init(){
    fdoRecordBuilder = new FdoRecordBuilder(TRANSFORMER_FACTORY, DOC_BUILDER_FACTORY);
  }

  @Test
  void testPrepareHandleRecordAttributes() throws Exception {
    // Given
    var request = genHandleRecordRequestObject();

    // When
    var result = fdoRecordBuilder.prepareHandleRecordAttributes(request, handle);

    // Then
    assertThat(result).hasSize(10);
  }

  @Test
  void testPrepareDoiRecordAttributes() throws Exception {
    // Given
    var request = genDoiRecordRequestObject();

    // When
    var result = fdoRecordBuilder.prepareDoiRecordAttributes(request, handle);

    // Then
    assertThat(result).hasSize(12);
  }

  @Test
  void testPrepareMediaObjectAttributes() throws Exception {
    // Given
    var request = genMediaRequestObject();

    // When
    var result = fdoRecordBuilder.prepareMediaObjectAttributes(request, handle);

    // Then
    assertThat(result).hasSize(16);
  }

  @Test
  void testPrepareDigitalSpecimenRecordAttributes() throws Exception {
    // Given
    var request = genDigitalSpecimenRequestObject();

    // When
    var result = fdoRecordBuilder.prepareDigitalSpecimenRecordAttributes(request, handle);

    // Then
    assertThat(result).hasSize(16);
  }

  @Test
  void testPrepareDigitalSpecimenBotanyAttributes() throws Exception {
    // Given
    var request = genDigitalSpecimenBotanyRequestObject();

    // When
    var result = fdoRecordBuilder.prepareDigitalSpecimenBotanyRecordAttributes(request, handle);

    // Then
    assertThat(result).hasSize(18);
  }
}
