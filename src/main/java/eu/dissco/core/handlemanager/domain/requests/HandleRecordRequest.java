package eu.dissco.core.handlemanager.domain.requests;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
public class HandleRecordRequest {

  private final String pidIssuerPid;
  private final String digitalObjectTypePid;
  private final String digitalObjectSubtypePid;
  private final String[] locations;

}
