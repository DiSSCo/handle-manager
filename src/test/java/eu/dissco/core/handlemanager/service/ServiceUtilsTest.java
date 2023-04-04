package eu.dissco.core.handlemanager.service;

import static eu.dissco.core.handlemanager.service.ServiceUtils.setUniquePhysicalIdentifierId;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.DIGITAL_OBJECT_SUBTYPE_PID;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.DIGITAL_OBJECT_TYPE_PID;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.DIGITAL_OR_PHYSICAL_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.IN_COLLECTION_FACILITY_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.LOC_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.MEDIA_HASH_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.MEDIA_URL_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PHYSICAL_IDENTIFIER_CETAF;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PID_ISSUER_PID;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PREFIX;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.REFERENT_DOI_NAME_PID;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.SPECIMEN_HOST_PID;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genDigitalSpecimenRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genMediaRequestObject;
import static org.assertj.core.api.Assertions.assertThat;

import eu.dissco.core.handlemanager.domain.requests.attributes.DigitalSpecimenRequest;
import eu.dissco.core.handlemanager.domain.requests.attributes.MediaObjectRequest;
import eu.dissco.core.handlemanager.domain.requests.attributes.PhysicalIdType;
import eu.dissco.core.handlemanager.domain.requests.attributes.PhysicalIdentifier;
import eu.dissco.core.handlemanager.testUtils.TestUtils;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

 class ServiceUtilsTest {

  @Test
  void testSetUniquePhysicalIdentifier(){
    // Given
   var request = genDigitalSpecimenRequestObject();
   var expected = request.getPhysicalIdentifier().physicalId().getBytes(StandardCharsets.UTF_8);

   // When
   var result = setUniquePhysicalIdentifierId(request);

   // Then
   assertThat(result).isEqualTo(expected);
  }

  @Test
  void testSetUniquePhysicalIdentifierCombined(){
   // Given
   var request = givenCombinedTypeDSRecord();
   var suffix = request.getSpecimenHostPid().replace(PREFIX + "/","");
   var expected = (request.getPhysicalIdentifier().physicalId() + ":" + suffix).getBytes(StandardCharsets.UTF_8);

   // When
   var result = setUniquePhysicalIdentifierId(request);
   // Then
   assertThat(result).isEqualTo(expected);
  }

  @Test
  void testSetUniquePhysicalIdentifierMedia(){
   // Given
   var request = genMediaRequestObject();
   var expected = request.getSubjectPhysicalIdentifier().physicalId();

   // When
   var result =new String(setUniquePhysicalIdentifierId(request), StandardCharsets.UTF_8);

   // Then
   assertThat(result).isEqualTo(expected);
  }

  @Test
  void testSetUniquePhysicalIdentifierMediaCombined(){
   // Given
   var request = givenCombinedMediaRequest();
   var suffix = request.getSubjectSpecimenHostPid().replace(PREFIX + "/","");
   var expected = request.getSubjectPhysicalIdentifier().physicalId() + ":" + suffix;

   // When
   var result = new String(setUniquePhysicalIdentifierId(request), StandardCharsets.UTF_8);

   // Then
   assertThat(result).isEqualTo(expected);
  }

  private DigitalSpecimenRequest givenCombinedTypeDSRecord(){
   return new DigitalSpecimenRequest(
       PID_ISSUER_PID,
       DIGITAL_OBJECT_TYPE_PID,
       DIGITAL_OBJECT_SUBTYPE_PID,
       LOC_TESTVAL,
       REFERENT_DOI_NAME_PID,
       DIGITAL_OR_PHYSICAL_TESTVAL,
       SPECIMEN_HOST_PID,
       IN_COLLECTION_FACILITY_TESTVAL,
       new PhysicalIdentifier("id", PhysicalIdType.COMBINED)
   );
  }

  private MediaObjectRequest givenCombinedMediaRequest(){
   return new MediaObjectRequest(
       PID_ISSUER_PID,
       DIGITAL_OBJECT_TYPE_PID,
       DIGITAL_OBJECT_SUBTYPE_PID,
       LOC_TESTVAL,
       REFERENT_DOI_NAME_PID,
       MEDIA_HASH_TESTVAL,
       MEDIA_URL_TESTVAL,
       SPECIMEN_HOST_PID,
       new PhysicalIdentifier("id", PhysicalIdType.COMBINED)
   );
  }

}
