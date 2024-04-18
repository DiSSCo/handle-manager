package eu.dissco.core.handlemanager.properties;

import eu.dissco.core.handlemanager.Profiles;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties("kafka.publisher")
@Profile(Profiles.DOI)
public class KafkaPublisherProperties {

  @NotBlank
  private String host;

  @NotBlank
  private String dcMediaTopic = "media-doi";

  @NotBlank
  private String dcSpecimenTopic = "specimen-doi";

}
