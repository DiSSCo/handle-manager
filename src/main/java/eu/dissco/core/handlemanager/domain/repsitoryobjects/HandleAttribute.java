package eu.dissco.core.handlemanager.domain.repsitoryobjects;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

public record HandleAttribute(int index, byte[] handle, String type, byte[] data) {

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    HandleAttribute that = (HandleAttribute) o;
    return index == that.index && Objects.equals(type, that.type)
        && Arrays.equals(data, that.data);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(index, type);
    result = 31 * result + Arrays.hashCode(data);
    return result;
  }

  @Override
  public String toString() {
    return "HandleAttribute{" +
        "handle=" + new String(handle, StandardCharsets.UTF_8) +
        ", index=" + index +
        ", type='" + type + '\'' +
        ", data=" + new String(data, StandardCharsets.UTF_8) +
        '}';
  }
}