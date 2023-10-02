package eu.dissco.core.handlemanager.properties;

import eu.dissco.core.handlemanager.Profiles;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProfileProperties {

  private final Environment env;

  @Getter
  private String domain;

  @PostConstruct
  void setDomain() {
    if (env.matchesProfiles(Profiles.DOI)) {
      domain = "https://doi.org/";
    } else {
      domain = "https://hdl.handle.net/";
    }
  }

}
