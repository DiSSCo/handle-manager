package eu.dissco.core.handlemanager.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@Component
@ConfigurationProperties("application")
public class ApplicationProperties {

  @NotBlank
  private String apiUrl;

  @NotBlank
  private String uiUrl;

  @NotBlank
  private String orchestrationUi;

  @NotBlank
  private String orchestrationApi;

  @NotNull
  private Integer maxHandles;

  @NotBlank
  private String prefix;

}
