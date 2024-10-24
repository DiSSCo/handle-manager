package eu.dissco.core.handlemanager.service;

import eu.dissco.core.handlemanager.Profiles;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@Profile(Profiles.DOI)
public class KafkaPublisherService {

  private final KafkaTemplate<String, String> kafkaTemplate;

  public void sendObjectToQueue(String topicName, String message) {
    log.debug("Sending to topic: {} with object: {}", topicName, message);
    kafkaTemplate.send(topicName, message);
  }

}
