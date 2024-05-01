package eu.dissco.core.handlemanager.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.dissco.core.handlemanager.Profiles;
import eu.dissco.core.handlemanager.domain.datacite.DataCiteEvent;
import eu.dissco.core.handlemanager.domain.FdoType;
import eu.dissco.core.handlemanager.properties.KafkaPublisherProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@Profile(Profiles.DOI)
public class DataCiteService {

  private final KafkaPublisherService kafkaService;
  private final KafkaPublisherProperties kafkaProperties;
  private final ObjectMapper mapper;

  public void publishToDataCite(DataCiteEvent event, FdoType objectType)
      throws JsonProcessingException {
    var topic =
        objectType.equals(FdoType.DIGITAL_SPECIMEN) ? kafkaProperties.getDcSpecimenTopic()
            : kafkaProperties.getDcMediaTopic();
    var message = mapper.writeValueAsString(event);
    kafkaService.sendObjectToQueue(topic, message);
  }

}
