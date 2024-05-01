package eu.dissco.core.handlemanager.service;

import static eu.dissco.core.handlemanager.testUtils.TestUtils.MAPPER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import eu.dissco.core.handlemanager.domain.datacite.DataCiteEvent;
import eu.dissco.core.handlemanager.domain.datacite.EventType;
import eu.dissco.core.handlemanager.domain.FdoType;
import eu.dissco.core.handlemanager.properties.KafkaPublisherProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DataCiteServiceTest {

  @Mock
  private KafkaPublisherService kafkaService;
  @Mock
  private KafkaPublisherProperties kafkaProperties;
  private DataCiteService dataCiteService;

  private static final String MEDIA_TOPIC = "media-doi";
  private static final String SPECIMEN_TOPIC = "specimen-doi";

  @BeforeEach
  void setup() {
    dataCiteService = new DataCiteService(kafkaService, kafkaProperties, MAPPER);
  }

  @Test
  void testPublishMediaObject() throws Exception {
    // Given
    given(kafkaProperties.getDcMediaTopic()).willReturn(MEDIA_TOPIC);
    var event = new DataCiteEvent(MAPPER.createObjectNode(), EventType.CREATE);

    // When
    dataCiteService.publishToDataCite(event, FdoType.MEDIA_OBJECT);

    // Then
    then(kafkaService).should().sendObjectToQueue(eq(MEDIA_TOPIC), any());
  }

  @Test
  void testPublishSpecimen() throws Exception {
    // Given
    given(kafkaProperties.getDcSpecimenTopic()).willReturn(SPECIMEN_TOPIC);
    var event = new DataCiteEvent(MAPPER.createObjectNode(), EventType.CREATE);

    // When
    dataCiteService.publishToDataCite(event, FdoType.DIGITAL_SPECIMEN);

    // Then
    then(kafkaService).should().sendObjectToQueue(eq(SPECIMEN_TOPIC), any());
  }


}
