package eu.dissco.core.handlemanager.properties;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties("datacite")
public class DataCiteProperties {

  @NotBlank
  private String repositoryId;

  @NotBlank
  private String password;

  @NotBlank
  private String endpoint;

}
