package eu.dissco.core.handlemanager.domain;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import eu.dissco.core.handlemanager.domain.responses.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class ResponseTest {

  String VALID_DATA = "this data is valid";

  @Test
  void testSetAttributeHandle() throws NoSuchFieldException {
    // Given
    String type = "pidIssuer";
    HandleRecordResponse response = new HandleRecordResponse();

    // When
    response.setAttribute(type, VALID_DATA);

    // Then
    assertThat(response.getPidIssuer()).isEqualTo(VALID_DATA);
  }

  @Test
  void testSetAttributeHandleException() {
    // Given
    String type = "pidIssue";
    HandleRecordResponse response = new HandleRecordResponse();

    // Then
    assertThrows(NoSuchFieldException.class, () -> {
      response.setAttribute(type, VALID_DATA);
    });
  }

  @Test
  void testSetAttributeDoi() throws NoSuchFieldException {
    // Given
    String type = "referent";
    DoiRecordResponse response = new DoiRecordResponse();

    //When
    response.setAttribute(type, VALID_DATA);

    //Then
    assertThat(response.getReferent()).isEqualTo(VALID_DATA);
  }

  @Test
  void testSetAttributeDoiCallsSuper() throws NoSuchFieldException {
    // Given
    String type = "pidIssuer";
    DoiRecordResponse response = new DoiRecordResponse();

    //When
    response.setAttribute(type, VALID_DATA);

    //Then
    assertThat(response.getPidIssuer()).isEqualTo(VALID_DATA);
  }

  @Test
  void testSetAttributeDoiException() {
    // Given
    String type = "pidIssue";
    DoiRecordResponse response = new DoiRecordResponse();

    // Then
    assertThrows(NoSuchFieldException.class, () -> {
      response.setAttribute(type, VALID_DATA);
    });
  }

  @Test
  void testSetAttributeDigitalSpecimen() throws NoSuchFieldException {
    // Given
    String type = "digitalOrPhysical";
    DigitalSpecimenResponse response = new DigitalSpecimenResponse();

    //When
    response.setAttribute(type, VALID_DATA);

    //Then
    assertThat(response.getDigitalOrPhysical()).isEqualTo(VALID_DATA);
  }

  @Test
  void testSetAttributeDigitalSpecimenBotany() throws NoSuchFieldException {
    // Given
    String type = "objectType";
    DigitalSpecimenBotanyResponse response = new DigitalSpecimenBotanyResponse();

    //When
    response.setAttribute(type, VALID_DATA);

    //Then
    assertThat(response.getObjectType()).isEqualTo(VALID_DATA);
  }


}
