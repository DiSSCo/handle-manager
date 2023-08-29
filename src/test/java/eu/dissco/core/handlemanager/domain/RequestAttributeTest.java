package eu.dissco.core.handlemanager.domain;

import static eu.dissco.core.handlemanager.domain.requests.vocabulary.PhysicalIdType.LOCAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.DIGITAL_OBJECT_TYPE_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.FDO_PROFILE_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.ISSUED_FOR_AGENT_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.SOURCE_SYSTEM_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.SPECIMEN_HOST_NAME_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.SPECIMEN_HOST_TESTVAL;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import eu.dissco.core.handlemanager.domain.requests.objects.DigitalSpecimenRequest;
import eu.dissco.core.handlemanager.domain.requests.objects.DoiRecordRequest;
import eu.dissco.core.handlemanager.domain.requests.objects.HandleRecordRequest;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import org.junit.jupiter.api.Test;

class RequestAttributeTest {

  @Test
  void testHandleRecordRequestDefaults() {
    // Given
    var request = new HandleRecordRequest(
        FDO_PROFILE_TESTVAL,
        ISSUED_FOR_AGENT_TESTVAL,
        DIGITAL_OBJECT_TYPE_TESTVAL,
        null,
        null,
        null
    );
    // Then
    assertThat(request.getPidIssuer()).isEqualTo("https://ror.org/04wxnsj81");
    assertThat(request.getStructuralType()).isEqualTo("digital");
  }

  @Test
  void testDoiRecordRequestDefaults() {
    // Given
    var request = new DoiRecordRequest(
        FDO_PROFILE_TESTVAL,
        ISSUED_FOR_AGENT_TESTVAL,
        DIGITAL_OBJECT_TYPE_TESTVAL,
        null,
        null,
        null,
        null,
        "Digital Specimen",
        null
    );

    // Then
    assertThat(request.getPrimaryReferentType()).isEqualTo("creation");
  }

  @Test
  void testDigitalSpecimenRequestDefaults() throws Exception {
    // Given
    var request = new DigitalSpecimenRequest(
        FDO_PROFILE_TESTVAL,
        ISSUED_FOR_AGENT_TESTVAL,
        DIGITAL_OBJECT_TYPE_TESTVAL,
        null,
        null,
        null,
        null,
        null,
        SPECIMEN_HOST_TESTVAL,
        SPECIMEN_HOST_NAME_TESTVAL,
        PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL,
        null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
        SOURCE_SYSTEM_TESTVAL
    );

    // Then
    assertThat(request.getPrimarySpecimenObjectIdType()).isEqualTo(LOCAL);
  }

  @Test
  void testMutuallyExclusiveElements() {
    // Then
    var e = assertThrows(InvalidRequestException.class, () -> new DigitalSpecimenRequest(
        FDO_PROFILE_TESTVAL,
        ISSUED_FOR_AGENT_TESTVAL,
        DIGITAL_OBJECT_TYPE_TESTVAL,
        null,
        null,
        null,
        null,
        null,
        SPECIMEN_HOST_TESTVAL,
        SPECIMEN_HOST_NAME_TESTVAL,
        PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL,
        null, null, "haha", null, null, null, null, null, null, null, null, null, null, null, null,
        SOURCE_SYSTEM_TESTVAL
    ));
    assertThat(e).hasMessage(
        "Request must contain exactly one of: [primarySpecimenObjectId, primarySpecimenObjectIdAbsenceReason]");
  }
}
