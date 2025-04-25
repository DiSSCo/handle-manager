package eu.dissco.core.handlemanager.properties;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties(prefix = "rabbitmq")
public class RabbitMqProperties {

  @NotBlank
  private String exchangeName = "doi-exchange";

  @NotBlank
  private String dcMediaRoutingKey = "media-doi-routing-key";

  @NotBlank
  private String dcSpecimenRoutingKey = "specimen-doi-routing-key";

  @NotBlank
  private String dcTombstoneRoutingKey = "tombstone-doi-routing-key";

}
