package eu.dissco.core.handlemanager.domain;

import static eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.PrimarySpecimenObjectIdType.LOCAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.ISSUED_FOR_AGENT_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.NORMALISED_PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.SPECIMEN_HOST_NAME_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.SPECIMEN_HOST_TESTVAL;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import eu.dissco.core.handlemanager.domain.fdo.DigitalSpecimenRequest;
import eu.dissco.core.handlemanager.domain.fdo.DoiRecordRequest;
import eu.dissco.core.handlemanager.domain.fdo.FdoType;
import eu.dissco.core.handlemanager.domain.fdo.HandleRecordRequest;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import org.junit.jupiter.api.Test;

class RequestAttributeTest {

  @Test
  void testHandleRecordRequestDefaults() {
    // Given
    var request = new HandleRecordRequest(
        ISSUED_FOR_AGENT_TESTVAL,
        null,
        null,
        null
    );
    // Then
    assertThat(request.getPidIssuer()).isEqualTo("https://ror.org/04wxnsj81");
  }

  @Test
  void testDoiRecordRequestDefaults() {
    // Given
    var request = new DoiRecordRequest(
        ISSUED_FOR_AGENT_TESTVAL,
        null,
        null,
        null,
        null,
        FdoType.DIGITAL_SPECIMEN.getDigitalObjectName(),
        null
    );

    // Then
    assertThat(request.getPrimaryReferentType()).isEqualTo("creation");
  }

  @Test
  void testDigitalSpecimenRequestDefaults() throws Exception {
    // Given
    var request = new DigitalSpecimenRequest(
        ISSUED_FOR_AGENT_TESTVAL,
        null,
        null,
        null,
        null,
        SPECIMEN_HOST_TESTVAL,
        SPECIMEN_HOST_NAME_TESTVAL,
        PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL,
        null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
        null, null);

    // Then
    assertThat(request.getPrimarySpecimenObjectIdType()).isEqualTo(LOCAL);
  }

  @Test
  void testMutuallyExclusiveElements() {
    // Then
    var e = assertThrows(InvalidRequestException.class, () -> new DigitalSpecimenRequest(
        ISSUED_FOR_AGENT_TESTVAL,
        null,
        null,
        null,
        null,
        SPECIMEN_HOST_TESTVAL,
        SPECIMEN_HOST_NAME_TESTVAL,
        null,
        null, null, NORMALISED_PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL, null, null, null, null, null,
        null, null, null, null, null, null, null,
        null, null));
    assertThat(e).hasMessage(
        "Request must contain exactly one of: [primarySpecimenObjectId, primarySpecimenObjectIdAbsenceReason]");
  }
}
