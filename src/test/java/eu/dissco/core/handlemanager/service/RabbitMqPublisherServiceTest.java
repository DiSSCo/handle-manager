package eu.dissco.core.handlemanager.service;

import static org.assertj.core.api.Assertions.assertThat;

import eu.dissco.core.handlemanager.properties.RabbitMqProperties;
import java.io.IOException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@ExtendWith(MockitoExtension.class)
class RabbitMqPublisherServiceTest {

  private static RabbitMQContainer container;
  private static RabbitTemplate rabbitTemplate;
  private RabbitMqPublisherService rabbitMqPublisherService;

  @BeforeAll
  static void setupContainer() throws IOException, InterruptedException {
    container = new RabbitMQContainer("rabbitmq:4.0.8-management-alpine");
    container.start();
    // Declare auto annotation exchange, queue and binding
    container.execInContainer("rabbitmqadmin", "declare", "exchange", "name=doi-exchange",
        "type=direct", "durable=true");
    declareRabbitResources("doi-exchange", "specimen-doi-queue", "specimen-doi-routing-key");
    declareRabbitResources("doi-exchange", "media-doi-queue", "media-doi-routing-key");
    declareRabbitResources("doi-exchange", "tombstone-doi-queue", "tombstone-doi-routing-key");

    CachingConnectionFactory factory = new CachingConnectionFactory(container.getHost());
    factory.setPort(container.getAmqpPort());
    factory.setUsername(container.getAdminUsername());
    factory.setPassword(container.getAdminPassword());
    rabbitTemplate = new RabbitTemplate(factory);
    rabbitTemplate.setReceiveTimeout(100L);
  }


  private static void declareRabbitResources(String exchangeName, String queueName,
      String routingKey)
      throws IOException, InterruptedException {
    container.execInContainer("rabbitmqadmin", "declare", "queue", "name=" + queueName,
        "queue_type=quorum", "durable=true");
    container.execInContainer("rabbitmqadmin", "declare", "binding", "source=" + exchangeName,
        "destination_type=queue", "destination=" + queueName, "routing_key=" + routingKey);
  }

  @AfterAll
  static void shutdownContainer() {
    container.stop();
  }

  @BeforeEach
  void setup() {
    rabbitMqPublisherService = new RabbitMqPublisherService(rabbitTemplate,
        new RabbitMqProperties());
  }

  @ParameterizedTest
  @ValueSource(strings = {"specimen-doi", "media-doi", "tombstone-doi"})
  void testSendObjectToQueue(String type) {
    // Given
    var message = "message";

    // When
    rabbitMqPublisherService.sendObjectToQueue(type + "-routing-key", "message");

    // Then
    var result = rabbitTemplate.receiveAndConvert(type + "-queue");
    assertThat(result).isEqualTo(message);
  }

}
