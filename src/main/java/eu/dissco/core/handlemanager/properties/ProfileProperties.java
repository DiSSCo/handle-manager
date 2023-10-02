package eu.dissco.core.handlemanager.properties;

import eu.dissco.core.handlemanager.Profiles;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@Component
public class ProfileProperties {

  private final Environment environment;
  private String domain;

  @PostConstruct
  void setDomain() {
    if (environment.matchesProfiles(Profiles.DOI)) {
      domain = "https://doi.org/";
    } else {
      domain = "https://hdl.handle.net/";
    }
  }

}
