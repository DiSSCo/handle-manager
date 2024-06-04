package eu.dissco.core.handlemanager.domain.repsitoryobjects;

import eu.dissco.core.handlemanager.domain.fdo.FdoProfile;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import lombok.Data;

@Data
public class HandleAttribute {

  private final int index;
  private final byte[] handle;
  private final String type;
  private final byte[] data;

  public HandleAttribute(int index, byte[] handle, String type, byte[] data) {
    this.index = index;
    this.handle = handle;
    this.type = type;
    this.data = data;
  }

  public HandleAttribute(FdoProfile fdoAttribute, byte[] handle, String data) {
    this.index = fdoAttribute.index();
    this.handle = handle;
    this.data = data.getBytes(StandardCharsets.UTF_8);
    this.type = fdoAttribute.get();
  }


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