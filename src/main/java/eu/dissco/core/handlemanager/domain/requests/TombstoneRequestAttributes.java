package eu.dissco.core.handlemanager.domain.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.dissco.core.handlemanager.domain.fdo.vocabulary.tombstone.HasRelatedPid;
import java.util.List;
import lombok.Getter;
import lombok.ToString;
import org.springframework.lang.Nullable;

@Getter
@ToString
public class TombstoneRequestAttributes {

  @JsonProperty(required = true, value = "ods:tombstonedText")
  private final String tombstonedText;
  @Nullable
  @JsonProperty("ods:hasRelatedPID")
  private final List<HasRelatedPid> hasRelatedPID;

  public TombstoneRequestAttributes(String tombstoneText, List<HasRelatedPid> hasRelatedPID) {
    this.tombstonedText = tombstoneText;
    this.hasRelatedPID = hasRelatedPID;
  }

}
