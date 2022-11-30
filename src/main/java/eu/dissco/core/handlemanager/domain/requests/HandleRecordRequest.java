package eu.dissco.core.handlemanager.domain.requests;

import java.util.Arrays;
import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public class HandleRecordRequest {

  protected final String pidIssuerPid;
  protected final String digitalObjectTypePid;
  protected final String digitalObjectSubtypePid;
  protected final String[] locations;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    HandleRecordRequest that = (HandleRecordRequest) o;
    return getPidIssuerPid().equals(that.getPidIssuerPid()) && getDigitalObjectTypePid().equals(
        that.getDigitalObjectTypePid()) && getDigitalObjectSubtypePid().equals(
        that.getDigitalObjectSubtypePid()) && Arrays.equals(getLocations(), that.getLocations());
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(getPidIssuerPid(), getDigitalObjectTypePid(),
        getDigitalObjectSubtypePid());
    result = 31 * result + Arrays.hashCode(getLocations());
    return result;
  }
}
