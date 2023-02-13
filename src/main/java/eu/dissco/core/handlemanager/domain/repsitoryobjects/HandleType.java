package eu.dissco.core.handlemanager.domain.repsitoryobjects;

import java.util.Arrays;
import java.util.Objects;

public record HandleType(String recordType, byte[] handle) {

  @Override
  public String toString() {
    return "HandleType{" +
        "recordType='" + recordType + '\'' +
        ", handle=" + Arrays.toString(handle) +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    HandleType that = (HandleType) o;
    return recordType.equals(that.recordType) && Arrays.equals(handle, that.handle);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(recordType);
    result = 31 * result + Arrays.hashCode(handle);
    return result;
  }
}
