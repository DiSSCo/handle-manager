package eu.dissco.core.handlemanager.domain.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.dissco.core.handlemanager.domain.fdo.HasRelatedPid;
import java.util.List;
import lombok.Getter;
import lombok.ToString;
import org.springframework.lang.Nullable;

@Getter
@ToString
public class TombstoneRequestAttributes {

  @JsonProperty(required = true, value = "tombstoneText")
  private final String tombstoneText;
  @Nullable
  @JsonProperty("hasRelatedPid")
  private final List<HasRelatedPid> hasRelatedPid;

  public TombstoneRequestAttributes(String tombstoneText, List<HasRelatedPid> hasRelatedPid) {
    this.tombstoneText = tombstoneText;
    this.hasRelatedPid = hasRelatedPid;
  }

}
