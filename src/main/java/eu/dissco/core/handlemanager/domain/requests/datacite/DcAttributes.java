package eu.dissco.core.handlemanager.domain.requests.datacite;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record DcAttributes(
    String prefix,
    String suffix,
    List<DcCreator> creators,
    List<DcTitle> titles,
    int publicationYear,
    DcResourceType resourceType,
    DcSubjet subject,
    List<DcCreator> contributors,
    List<DcDate> dates,
    List<DcAlternateIdentifier> alternateIdentifiers,
    List<DcNameIdentifiers> nameIdentifiers
) {
  @JsonProperty("event")
  private static final String EVENT = "publish";

  @JsonProperty("publisher")
  private static final String PUBLISHER = "Distributed System of Scientific Collections";

}
