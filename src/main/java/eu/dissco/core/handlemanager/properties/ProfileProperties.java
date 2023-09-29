package eu.dissco.core.handlemanager.properties;

import eu.dissco.core.handlemanager.Profiles;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@Component
@ConfigurationProperties("spring.profiles.active")
public class ProfileProperties {

  private String env;
  private String domain;

  @PostConstruct
  void setDomain() {
    if (env.equals(Profiles.DOI)) {
      domain = "https://doi.org/";
    } else {
      domain = "https://hdl.handle.net/";
    }
  }

}
