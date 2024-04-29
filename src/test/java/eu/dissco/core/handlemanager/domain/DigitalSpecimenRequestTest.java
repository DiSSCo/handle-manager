package eu.dissco.core.handlemanager.domain;

import static eu.dissco.core.handlemanager.testUtils.TestUtils.FDO_PROFILE_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.ISSUED_FOR_AGENT_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.LOC_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.NORMALISED_PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PID_ISSUER_TESTVAL_OTHER;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PRIMARY_REFERENT_TYPE_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.REFERENT_NAME_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.SPECIMEN_HOST_NAME_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.SPECIMEN_HOST_TESTVAL;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import eu.dissco.core.handlemanager.domain.requests.objects.DigitalSpecimenRequest;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.FdoType;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.specimen.BaseTypeOfSpecimen;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.specimen.InformationArtefactType;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.specimen.MaterialSampleType;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.specimen.PrimarySpecimenObjectIdType;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.specimen.TopicCategory;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.specimen.TopicDiscipline;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.specimen.TopicDomain;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.specimen.TopicOrigin;
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
        FdoType.DIGITAL_SPECIMEN,
        PID_ISSUER_TESTVAL_OTHER,
        LOC_TESTVAL,
        REFERENT_NAME_TESTVAL,
        PRIMARY_REFERENT_TYPE_TESTVAL,
        SPECIMEN_HOST_TESTVAL,
        SPECIMEN_HOST_NAME_TESTVAL,
        PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL,
        PrimarySpecimenObjectIdType.LOCAL,
        null,
        NORMALISED_PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL,
        null,
        null,
        null,
        null,
        discipline,
        category,
        null,
        null,
        null,
        null, null, null, null, null));
  }

  @ParameterizedTest
  @MethodSource("correctMaterialSampleTypes")
  void testMaterialSampleType(MaterialSampleType materialSampleType,
      TopicDiscipline topicDiscipline, TopicDomain topicDomain, TopicOrigin topicOrigin) {
    assertDoesNotThrow(() -> new DigitalSpecimenRequest(
        FDO_PROFILE_TESTVAL,
        ISSUED_FOR_AGENT_TESTVAL,
        FdoType.DIGITAL_SPECIMEN,
        PID_ISSUER_TESTVAL_OTHER,
        LOC_TESTVAL,
        REFERENT_NAME_TESTVAL,
        PRIMARY_REFERENT_TYPE_TESTVAL,
        SPECIMEN_HOST_TESTVAL,
        SPECIMEN_HOST_NAME_TESTVAL,
        PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL,
        PrimarySpecimenObjectIdType.LOCAL, null,
        NORMALISED_PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL,
        null,
        null,
        topicOrigin,
        topicDomain,
        topicDiscipline,
        null,
        null,
        null,
        null,
        materialSampleType,
        null,
        null,
        null, null));

  }

  @Test
  void testMaterialSampleTypeFails() {
    assertThrows(InvalidRequestException.class, () -> new DigitalSpecimenRequest(
        FDO_PROFILE_TESTVAL,
        ISSUED_FOR_AGENT_TESTVAL,
        FdoType.DIGITAL_SPECIMEN,
        PID_ISSUER_TESTVAL_OTHER,
        LOC_TESTVAL,
        REFERENT_NAME_TESTVAL,
        PRIMARY_REFERENT_TYPE_TESTVAL,
        SPECIMEN_HOST_TESTVAL,
        SPECIMEN_HOST_NAME_TESTVAL,
        PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL,
        PrimarySpecimenObjectIdType.LOCAL,
        null,
        PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL + ":123",
        null,
        null,
        null,
        null,
        TopicDiscipline.ZOO,
        null,
        null,
        null,
        null,
        MaterialSampleType.OTHER_SOLID, null,
        null,
        null, null));
  }

  @Test
  void testInformationArtefactTypeFails() {
    assertThrows(InvalidRequestException.class, () -> new DigitalSpecimenRequest(
        FDO_PROFILE_TESTVAL,
        ISSUED_FOR_AGENT_TESTVAL,
        FdoType.DIGITAL_SPECIMEN,
        PID_ISSUER_TESTVAL_OTHER,
        LOC_TESTVAL,
        REFERENT_NAME_TESTVAL,
        PRIMARY_REFERENT_TYPE_TESTVAL,
        SPECIMEN_HOST_TESTVAL,
        SPECIMEN_HOST_NAME_TESTVAL,
        PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL,
        PrimarySpecimenObjectIdType.GLOBAL,
        null,
        PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        BaseTypeOfSpecimen.MATERIAL,
        InformationArtefactType.SOUND,
        null,
        null,
        null,
        null, null));
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
