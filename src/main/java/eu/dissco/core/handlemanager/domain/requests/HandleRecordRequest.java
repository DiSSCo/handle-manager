package eu.dissco.core.handlemanager.domain.requests;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
public class HandleRecordRequest {

  @NonNull
  private final String pidIssuerPid;
  @NonNull
  private final String digitalObjectTypePid;
  @NonNull
  private final String digitalObjectSubtypePid;
  @NonNull
  private final String[] locations;

}
