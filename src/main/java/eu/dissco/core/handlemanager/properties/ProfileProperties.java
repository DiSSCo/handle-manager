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

  private final Environment environment;
  @Getter
  private String domain;
  @Getter
  private String pidIssuer;
  @Getter
  private String pidIssuerName;
  @Getter
  private String issuedForAgent;
  @Getter
  private String issuedForAgentName;
  public static final String HANDLE_DOMAIN = "https://hdl.handle.net/";
  public static final String DOI_DOMAIN = "https://doi.org/";

  private static final String DATACITE_ROR = "https://ror.org/04wxnsj81";
  private static final String DISSCO_ROR = "https://ror.org/02wddde16";
  private static final String DATACITE_NAME = "DataCite";
  private static final String DISSCO_NAME = "Distributed System of Scientific Collections";


  @PostConstruct
  void setDomain() {
    if (environment.matchesProfiles(Profiles.DOI)) {
      domain = DOI_DOMAIN;
      pidIssuer = DATACITE_ROR;
      pidIssuerName = DATACITE_NAME;
    } else {
      domain = HANDLE_DOMAIN;
      pidIssuer = DISSCO_ROR;
      pidIssuerName = DISSCO_NAME;
    }
    issuedForAgent = DISSCO_ROR;
    issuedForAgentName = DISSCO_NAME;
  }

}
