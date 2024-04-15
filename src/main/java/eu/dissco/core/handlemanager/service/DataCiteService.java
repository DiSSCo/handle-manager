package eu.dissco.core.handlemanager.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.dissco.core.handlemanager.Profiles;
import eu.dissco.core.handlemanager.domain.datacite.DataCiteEvent;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.specimen.ObjectType;
import eu.dissco.core.handlemanager.properties.KafkaPublisherProperties;
import java.util.Set;
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

  public void publishToDataCite(DataCiteEvent event, ObjectType objectType)
      throws JsonProcessingException {
    var topic =
        objectType.equals(ObjectType.DIGITAL_SPECIMEN) ? kafkaProperties.getDcSpecimenTopic()
            : kafkaProperties.getDcMediaTopic();
    var message = mapper.writeValueAsString(event);
    kafkaService.sendObjectToQueue(topic, message);
  }

  public void dlqDois(Set<String> dois) {
    log.info("DLQing DOIs: {}", dois);
    kafkaService.sendObjectToQueue(kafkaProperties.getDlqDoiTopic(), dois.toString());
  }

}
