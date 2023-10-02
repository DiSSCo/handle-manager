package eu.dissco.core.handlemanager.service;

import static eu.dissco.core.handlemanager.testUtils.TestUtils.DOI_DOMAIN;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.MAPPER;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.RECORD_TYPE_DOI;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.RECORD_TYPE_HANDLE;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genCreateRecordRequest;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genDoiRecordAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDoiRecordRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenRecordResponseWrite;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import eu.dissco.core.handlemanager.Profiles;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiDataLinks;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiLinks;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiWrapperWrite;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.HandleAttribute;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.ObjectType;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles(profiles = Profiles.DOI)
class DoiServiceTest extends PidServiceTest {

  @BeforeEach
  void initService() {
    service = new DoiService(handleRep, fdoRecordService, hgService, MAPPER, profileProperties);
  }

  @Test
  void testCreateDoiRecordDoiProfile() throws Exception {
    // Given
    byte[] handle = handles.get(0);
    var request = genCreateRecordRequest(givenDoiRecordRequestObject(), RECORD_TYPE_DOI);
    var templateDataLinks = givenRecordResponseWrite(List.of(handle), RECORD_TYPE_DOI).data()
        .get(0);
    var doiLinks = new JsonApiLinks(DOI_DOMAIN + new String(handle));
    var expectedData = new JsonApiDataLinks(
        templateDataLinks.id(), templateDataLinks.type(), templateDataLinks.attributes(), doiLinks);
    var responseExpected = new JsonApiWrapperWrite(List.of(expectedData));

    List<HandleAttribute> doiRecord = genDoiRecordAttributes(handle, ObjectType.HANDLE);

    given(hgService.genHandleList(1)).willReturn(new ArrayList<>(List.of(handle)));
    given(fdoRecordService.prepareDoiRecordAttributes(any(), any(), eq(ObjectType.DOI))).willReturn(
        doiRecord);
    given(profileProperties.getDomain()).willReturn(DOI_DOMAIN);

    // When
    var responseReceived = service.createRecords(List.of(request));

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testCreateInvalidType() {
    // Given
    var request = genCreateRecordRequest(givenDoiRecordRequestObject(), RECORD_TYPE_HANDLE);
    given(hgService.genHandleList(1)).willReturn(new ArrayList<>(List.of(handles.get(0))));

    // Then
    assertThrows(InvalidRequestException.class, () -> service.createRecords(List.of(request)));
  }

}
