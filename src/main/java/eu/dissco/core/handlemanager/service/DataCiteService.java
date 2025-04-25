package eu.dissco.core.handlemanager.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.dissco.core.handlemanager.Profiles;
import eu.dissco.core.handlemanager.domain.datacite.DataCiteEvent;
import eu.dissco.core.handlemanager.domain.datacite.DataCiteTombstoneEvent;
import eu.dissco.core.handlemanager.domain.fdo.FdoType;
import eu.dissco.core.handlemanager.properties.RabbitMqProperties;
import eu.dissco.core.handlemanager.schema.HasRelatedPid;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@Profile(Profiles.DOI)
public class DataCiteService {

  private final RabbitMqPublisherService kafkaService;
  private final RabbitMqProperties rabbitMqProperties;
  private final ObjectMapper mapper;

  public void publishToDataCite(DataCiteEvent event, FdoType objectType)
      throws JsonProcessingException {
    var topic =
        objectType.equals(FdoType.DIGITAL_SPECIMEN) ? rabbitMqProperties.getDcSpecimenRoutingKey()
            : rabbitMqProperties.getDcMediaRoutingKey();
    var message = mapper.writeValueAsString(event);
    kafkaService.sendObjectToQueue(topic, message);
  }

  public void tombstoneDataCite(String handle, List<HasRelatedPid> relatedPids)
      throws JsonProcessingException {
    var dcRelatedIdentifiers = new ArrayList<JsonNode>();
    if (relatedPids != null) {
      relatedPids.forEach(relatedPid -> dcRelatedIdentifiers.add(mapper.createObjectNode()
          .put("relationType", "HasMetadata")
          .put("relatedIdentifier", relatedPid.getPid())));
    }
    var message = mapper.writeValueAsString(
        new DataCiteTombstoneEvent(handle, dcRelatedIdentifiers));
    kafkaService.sendObjectToQueue(rabbitMqProperties.getDcTombstoneRoutingKey(), message);
  }

}
