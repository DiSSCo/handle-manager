package eu.dissco.core.handlemanager.service;

import static org.mockito.BDDMockito.then;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

@ExtendWith(MockitoExtension.class)
class KafkaPublisherServiceTest {

  @Mock
  private KafkaTemplate<String, String> kafkaTemplate;

  private KafkaPublisherService kafkaPublisherService;

  @BeforeEach
  void setup() {
    kafkaPublisherService = new KafkaPublisherService(kafkaTemplate);
  }

  @Test
  void testSendObjectToQueue() {
    // When
    kafkaPublisherService.sendObjectToQueue("topic", "message");

    // Then
    then(kafkaTemplate).should().send("topic", "message");
  }

}
