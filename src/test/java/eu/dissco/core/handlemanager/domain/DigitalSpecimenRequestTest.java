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
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import eu.dissco.core.handlemanager.domain.requests.objects.DigitalSpecimenRequest;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.BaseTypeOfSpecimen;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.InformationArtefactType;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.MaterialSampleType;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.PhysicalIdType;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.TopicCategory;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.TopicDiscipline;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.TopicDomain;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.TopicOrigin;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

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
        LOC_TESTVAL,
        REFERENT_NAME_TESTVAL,
        PRIMARY_REFERENT_TYPE_TESTVAL,
        SPECIMEN_HOST_TESTVAL,
        SPECIMEN_HOST_NAME_TESTVAL,
        PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL,
        PhysicalIdType.LOCAL, null, null, null, null, null, discipline, category, null, null, null,
        null, null, null, null,
        SOURCE_SYSTEM_TESTVAL
    ));
  }

  @Test
  void testNormalisePhysIdNoSourceSystem() {
    assertThrows(InvalidRequestException.class, () -> new DigitalSpecimenRequest(
        FDO_PROFILE_TESTVAL,
        ISSUED_FOR_AGENT_TESTVAL,
        DIGITAL_OBJECT_TYPE_TESTVAL,
        PID_ISSUER_TESTVAL_OTHER,
        LOC_TESTVAL,
        REFERENT_NAME_TESTVAL,
        PRIMARY_REFERENT_TYPE_TESTVAL,
        SPECIMEN_HOST_TESTVAL,
        SPECIMEN_HOST_NAME_TESTVAL,
        PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL,
        PhysicalIdType.LOCAL, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null,
        null
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
        LOC_TESTVAL,
        REFERENT_NAME_TESTVAL,
        PRIMARY_REFERENT_TYPE_TESTVAL,
        SPECIMEN_HOST_TESTVAL,
        SPECIMEN_HOST_NAME_TESTVAL,
        PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL,
        PhysicalIdType.LOCAL, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null,
        SOURCE_SYSTEM_TESTVAL
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
        LOC_TESTVAL,
        REFERENT_NAME_TESTVAL,
        PRIMARY_REFERENT_TYPE_TESTVAL,
        SPECIMEN_HOST_TESTVAL,
        SPECIMEN_HOST_NAME_TESTVAL,
        PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL,
        PhysicalIdType.RESOLVABLE, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null,
        null
    );

    // Then
    assertThat(specimen.getNormalisedPrimarySpecimenObjectId()).isEqualTo(
        PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL);
  }

  @ParameterizedTest
  @MethodSource("correctMaterialSampleTypes")
  void testMaterialSampleType(MaterialSampleType materialSampleType,
      TopicDiscipline topicDiscipline, TopicDomain topicDomain, TopicOrigin topicOrigin) {
    assertDoesNotThrow(() -> new DigitalSpecimenRequest(
        FDO_PROFILE_TESTVAL,
        ISSUED_FOR_AGENT_TESTVAL,
        DIGITAL_OBJECT_TYPE_TESTVAL,
        PID_ISSUER_TESTVAL_OTHER,
        LOC_TESTVAL,
        REFERENT_NAME_TESTVAL,
        PRIMARY_REFERENT_TYPE_TESTVAL,
        SPECIMEN_HOST_TESTVAL,
        SPECIMEN_HOST_NAME_TESTVAL,
        PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL,
        PhysicalIdType.LOCAL, null, null, null, topicOrigin, topicDomain, topicDiscipline, null,
        null, null, null, materialSampleType, null,
        null, null, SOURCE_SYSTEM_TESTVAL));

  }

  @Test
  void testMaterialSampleTypeFails() {
    assertThrows(InvalidRequestException.class, () -> new DigitalSpecimenRequest(
        FDO_PROFILE_TESTVAL,
        ISSUED_FOR_AGENT_TESTVAL,
        DIGITAL_OBJECT_TYPE_TESTVAL,
        PID_ISSUER_TESTVAL_OTHER,
        LOC_TESTVAL,
        REFERENT_NAME_TESTVAL,
        PRIMARY_REFERENT_TYPE_TESTVAL,
        SPECIMEN_HOST_TESTVAL,
        SPECIMEN_HOST_NAME_TESTVAL,
        PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL,
        PhysicalIdType.LOCAL, null, null, null, null, null, TopicDiscipline.ZOO, null,
        null, null, null, MaterialSampleType.OTHER_SOLID, null,
        null, null, SOURCE_SYSTEM_TESTVAL));
  }

  @Test
  void testInformationArtefactTypeFails() {
    assertThrows(InvalidRequestException.class, () -> new DigitalSpecimenRequest(
        FDO_PROFILE_TESTVAL,
        ISSUED_FOR_AGENT_TESTVAL,
        DIGITAL_OBJECT_TYPE_TESTVAL,
        PID_ISSUER_TESTVAL_OTHER,
        LOC_TESTVAL,
        REFERENT_NAME_TESTVAL,
        PRIMARY_REFERENT_TYPE_TESTVAL,
        SPECIMEN_HOST_TESTVAL,
        SPECIMEN_HOST_NAME_TESTVAL,
        PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL,
        PhysicalIdType.LOCAL, null, null, null, null, null, null, null,
        null, BaseTypeOfSpecimen.MATERIAL, InformationArtefactType.SOUND, null, null,
        null, null, SOURCE_SYSTEM_TESTVAL));
  }

  private static Stream<Arguments> correctMaterialSampleTypes() {
    return Stream.of(
        Arguments.of(null, null, null, null),
        Arguments.of(MaterialSampleType.WHOLE_ORG, null, null, null),
        Arguments.of(MaterialSampleType.WHOLE_ORG, TopicDiscipline.BOTANY, null, null),
        Arguments.of(MaterialSampleType.SLURRY_BIOME, TopicDiscipline.ZOO, TopicDomain.ENV, null),
        Arguments.of(MaterialSampleType.SLURRY_BIOME, null, TopicDomain.ENV, null),
        Arguments.of(MaterialSampleType.ANY_AGGR, null, null, TopicOrigin.MIXED)
    );
  }


}
