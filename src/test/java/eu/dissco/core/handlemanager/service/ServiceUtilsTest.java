package eu.dissco.core.handlemanager.service;

import static eu.dissco.core.handlemanager.service.ServiceUtils.setUniquePhysicalIdentifierId;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.DIGITAL_OBJECT_TYPE_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.FDO_PROFILE_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.ISSUED_FOR_AGENT_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.LOC_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.MEDIA_HASH_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.MEDIA_URL_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PID_ISSUER_TESTVAL_OTHER;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PRIMARY_REFERENT_TYPE_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.REFERENT_NAME_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.ROR_DOMAIN;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.SPECIMEN_HOST_NAME_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.SPECIMEN_HOST_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.STRUCTURAL_TYPE_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genMediaRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDigitalSpecimenRequestObjectNullOptionals;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import eu.dissco.core.handlemanager.domain.requests.attributes.DigitalSpecimenRequest;
import eu.dissco.core.handlemanager.domain.requests.attributes.MediaObjectRequest;
import eu.dissco.core.handlemanager.domain.requests.attributes.PhysicalIdType;
import eu.dissco.core.handlemanager.domain.requests.attributes.PhysicalIdentifier;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

 class ServiceUtilsTest {

  @Test
  void testSetUniquePhysicalIdentifier(){
    // Given
   var request = givenDigitalSpecimenRequestObjectNullOptionals();
   var suffix = request.getSpecimenHost().replace(ROR_DOMAIN,"");
   var expected = request.getPrimarySpecimenObjectId() + ":" + suffix;

   // When
   var result = setUniquePhysicalIdentifierId(request);

   // Then
   assertThat(result).isEqualTo(expected);
  }

  @Test
  void testSetUniquePhysicalIdentifierCombined(){
   // Given
   var request = givenCetafTypeDSRecord();
   var expected = request.getPrimarySpecimenObjectId();

   // When
   var result = setUniquePhysicalIdentifierId(request);

   // Then
   assertThat(result).isEqualTo(expected);
  }

  @Test
  void testSetUniquePhysicalIdentifierMedia(){
   // Given
   var request = genMediaRequestObject();
   String expected = request.getSubjectPhysicalIdentifier().physicalId();

   // When
   String result = setUniquePhysicalIdentifierId(request);

   // Then
   assertThat(result).isEqualTo(expected);
  }

  @Test
  void testSetUniquePhysicalIdentifierMediaCombined(){
   // Given
   var request = givenCombinedMediaRequest();
   var suffix = request.getSubjectSpecimenHostPid().replace(ROR_DOMAIN,"");
   String expected = request.getSubjectPhysicalIdentifier().physicalId() + ":" + suffix;

   // When
   String result = setUniquePhysicalIdentifierId(request);

   // Then
   assertThat(result).isEqualTo(expected);
  }

  @Test
  void testCollectToSingletonException(){
   // Given
   var duplicates = Stream.of("a", "a");

   // Then
   assertThrowsExactly(IllegalStateException.class, () ->
       duplicates.collect(ServiceUtils.toSingleton()));
  }

  private DigitalSpecimenRequest givenCetafTypeDSRecord(){
   try {
    return new DigitalSpecimenRequest(
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
        PhysicalIdType.CETAF,null, null, null, null, null, null, null, null, null, null,null, null, null, null

    );
   } catch (InvalidRequestException e) {
    return null;
   }
  }

  private MediaObjectRequest givenCombinedMediaRequest(){
   return new MediaObjectRequest(
       FDO_PROFILE_TESTVAL,
       ISSUED_FOR_AGENT_TESTVAL,
       DIGITAL_OBJECT_TYPE_TESTVAL,
       PID_ISSUER_TESTVAL_OTHER,
       STRUCTURAL_TYPE_TESTVAL,
       LOC_TESTVAL,
       REFERENT_NAME_TESTVAL,
       PRIMARY_REFERENT_TYPE_TESTVAL,
       MEDIA_HASH_TESTVAL,
       MEDIA_URL_TESTVAL,
       SPECIMEN_HOST_TESTVAL,
       new PhysicalIdentifier("id", PhysicalIdType.COMBINED)
   );
  }

}
