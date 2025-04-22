package eu.dissco.core.handlemanager.service;

import eu.dissco.core.handlemanager.Profiles;
import eu.dissco.core.handlemanager.properties.RabbitMqProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@Profile(Profiles.DOI)
public class RabbitMqPublisherService {

  private final RabbitTemplate rabbitTemplate;
  private final RabbitMqProperties rabbitMqProperties;

  public void sendObjectToQueue(String topicName, String message) {
    log.debug("Sending to topic: {} with object: {}", topicName, message);
    rabbitTemplate.convertSendAndReceive(rabbitMqProperties.getExchangeName(),
        topicName, message);
  }

}
