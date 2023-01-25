package eu.dissco.core.handlemanager.domain;

import eu.dissco.core.handlemanager.domain.repsitoryobjects.HandleAttribute;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public record HandleObjectWrapper(
    byte[] handle,
    List<HandleAttribute> attributeList,
    String type) {

  @Override
  public String toString() {
    return "HandleObjectWrapper{" +
        "handle=" + Arrays.toString(handle) +
        ", attributeList=" + attributeList +
        ", type='" + type + '\'' +
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
    HandleObjectWrapper that = (HandleObjectWrapper) o;
    return Arrays.equals(handle, that.handle) && attributeList.equals(that.attributeList)
        && type.equals(that.type);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(attributeList, type);
    result = 31 * result + Arrays.hashCode(handle);
    return result;
  }
}
