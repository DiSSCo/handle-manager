package eu.dissco.core.handlemanager.domain;

import static eu.dissco.core.handlemanager.testUtils.TestUtils.DIGITAL_OBJECT_TYPE_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.FDO_PROFILE_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.ISSUED_FOR_AGENT_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.LOC_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PID_ISSUER_TESTVAL_OTHER;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PRIMARY_REFERENT_TYPE_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.REFERENT_NAME_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.SOURCE_SYSTEM_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.SPECIMEN_HOST_NAME_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.SPECIMEN_HOST_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.STRUCTURAL_TYPE_TESTVAL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import eu.dissco.core.handlemanager.domain.requests.objects.DigitalSpecimenRequest;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.PhysicalIdType;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.TopicCategory;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.TopicDiscipline;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import org.junit.jupiter.api.Test;

class DigitalSpecimenRequestTest {

  @Test
  void testInvalidTopicCombination() {
    // Given
    var discipline = TopicDiscipline.OTHER_BIO;
    var category = TopicCategory.ANIMAL_GENE;

    // Then
    assertThrows(InvalidRequestException.class, () -> new DigitalSpecimenRequest(
        FDO_PROFILE_TESTVAL,
        ISSUED_FOR_AGENT_TESTVAL,
        DIGITAL_OBJECT_TYPE_TESTVAL,
        PID_ISSUER_TESTVAL_OTHER,
        STRUCTURAL_TYPE_TESTVAL,
        LOC_TESTVAL,
        REFERENT_NAME_TESTVAL,
        PRIMARY_REFERENT_TYPE_TESTVAL,
        SPECIMEN_HOST_TESTVAL,
        SPECIMEN_HOST_NAME_TESTVAL,
        PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL,
        PhysicalIdType.LOCAL, null, null, null, null, null, discipline, category, null, null, null,
        null, null, null, null,
        null, SOURCE_SYSTEM_TESTVAL
    ));
  }

  @Test
  void testNormalisePhysIdNoSourceSystem() {
    assertThrows(InvalidRequestException.class, () -> new DigitalSpecimenRequest(
        FDO_PROFILE_TESTVAL,
        ISSUED_FOR_AGENT_TESTVAL,
        DIGITAL_OBJECT_TYPE_TESTVAL,
        PID_ISSUER_TESTVAL_OTHER,
        STRUCTURAL_TYPE_TESTVAL,
        LOC_TESTVAL,
        REFERENT_NAME_TESTVAL,
        PRIMARY_REFERENT_TYPE_TESTVAL,
        SPECIMEN_HOST_TESTVAL,
        SPECIMEN_HOST_NAME_TESTVAL,
        PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL,
        PhysicalIdType.LOCAL, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null,
        null, null
    ));
  }

  @Test
  void testNormaliseLocalPhysId() throws Exception {
    // Given
    var expected = PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL + ":" + SOURCE_SYSTEM_TESTVAL;
    var specimen = new DigitalSpecimenRequest(
        FDO_PROFILE_TESTVAL,
        ISSUED_FOR_AGENT_TESTVAL,
        DIGITAL_OBJECT_TYPE_TESTVAL,
        PID_ISSUER_TESTVAL_OTHER,
        STRUCTURAL_TYPE_TESTVAL,
        LOC_TESTVAL,
        REFERENT_NAME_TESTVAL,
        PRIMARY_REFERENT_TYPE_TESTVAL,
        SPECIMEN_HOST_TESTVAL,
        SPECIMEN_HOST_NAME_TESTVAL,
        PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL,
        PhysicalIdType.LOCAL, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null,
        null, SOURCE_SYSTEM_TESTVAL
    );

    // Then
    assertThat(specimen.getNormalisedPrimarySpecimenObjectId()).isEqualTo(expected);
  }

  @Test
  void testNormaliseResolvablePhysId() throws Exception {
    // Given
    var specimen = new DigitalSpecimenRequest(
        FDO_PROFILE_TESTVAL,
        ISSUED_FOR_AGENT_TESTVAL,
        DIGITAL_OBJECT_TYPE_TESTVAL,
        PID_ISSUER_TESTVAL_OTHER,
        STRUCTURAL_TYPE_TESTVAL,
        LOC_TESTVAL,
        REFERENT_NAME_TESTVAL,
        PRIMARY_REFERENT_TYPE_TESTVAL,
        SPECIMEN_HOST_TESTVAL,
        SPECIMEN_HOST_NAME_TESTVAL,
        PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL,
        PhysicalIdType.RESOLVABLE, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null,
        null, null
    );

    // Then
    assertThat(specimen.getNormalisedPrimarySpecimenObjectId()).isEqualTo(
        PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL);
  }


}
