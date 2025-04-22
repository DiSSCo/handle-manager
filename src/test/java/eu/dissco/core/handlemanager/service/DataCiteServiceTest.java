package eu.dissco.core.handlemanager.service;

import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE_ALT;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.MAPPER;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenHasRelatedPid;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;

import eu.dissco.core.handlemanager.domain.datacite.DataCiteEvent;
import eu.dissco.core.handlemanager.domain.datacite.DataCiteTombstoneEvent;
import eu.dissco.core.handlemanager.domain.datacite.EventType;
import eu.dissco.core.handlemanager.domain.fdo.FdoType;
import eu.dissco.core.handlemanager.properties.RabbitMqProperties;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DataCiteServiceTest {

  private static final String MEDIA_ROUTING_KEY = "media-doi-routing-key";
  private static final String SPECIMEN_DOI_ROUTING_KEY = "specimen-doi-routing-key";
  private static final String TOMBSTONE_DOI_ROUTING_KEY = "tombstone-doi-routing-key";
  @Mock
  private RabbitMqPublisherService rabbitMqPublisherService;
  private DataCiteService dataCiteService;

  @BeforeEach
  void setup() {
    dataCiteService = new DataCiteService(rabbitMqPublisherService, new RabbitMqProperties(), MAPPER);
  }

  @Test
  void testPublishDigitalMedia() throws Exception {
    // Given
    var event = new DataCiteEvent(MAPPER.createObjectNode(), EventType.CREATE);

    // When
    dataCiteService.publishToDataCite(event, FdoType.DIGITAL_MEDIA);

    // Then
    then(rabbitMqPublisherService).should().sendObjectToQueue(eq(MEDIA_ROUTING_KEY), any());
  }

  @Test
  void testPublishSpecimen() throws Exception {
    // Given
    var event = new DataCiteEvent(MAPPER.createObjectNode(), EventType.CREATE);

    // When
    dataCiteService.publishToDataCite(event, FdoType.DIGITAL_SPECIMEN);

    // Then
    then(rabbitMqPublisherService).should().sendObjectToQueue(eq(SPECIMEN_DOI_ROUTING_KEY), any());
  }

  @Test
  void testTombstoneRecord() throws Exception {
    // Given
    var event = new DataCiteTombstoneEvent(HANDLE, List.of(MAPPER.createObjectNode()
        .put("relationType", "HasMetadata")
        .put("relatedIdentifier", HANDLE_ALT)));
    var expected = MAPPER.writeValueAsString(event);

    // When
    dataCiteService.tombstoneDataCite(HANDLE, List.of(givenHasRelatedPid()));

    // Then
    then(rabbitMqPublisherService).should().sendObjectToQueue(TOMBSTONE_DOI_ROUTING_KEY, expected);
  }

  @Test
  void testTombstoneRecordNullRelatedPids() throws Exception {
    // Given
    var event = new DataCiteTombstoneEvent(HANDLE, List.of());
    var expected = MAPPER.writeValueAsString(event);

    // When
    dataCiteService.tombstoneDataCite(HANDLE, null);

    // Then
    then(rabbitMqPublisherService).should().sendObjectToQueue(TOMBSTONE_DOI_ROUTING_KEY, expected);
  }

}
