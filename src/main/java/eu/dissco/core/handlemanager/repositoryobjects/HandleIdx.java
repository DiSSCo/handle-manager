package eu.dissco.core.handlemanager.repositoryobjects;

import java.io.Serializable;
import java.util.Base64;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Lob;
import org.hibernate.annotations.Type;

// Composite Primary Key for Handles table


@Embeddable
public class HandleIdx implements Serializable {

  private static final long serialVersionUID = 1L;

  @Column(name = "handle")
  @Lob
  @Type(type = "org.hibernate.type.BinaryType")
  private byte[] handle;

  @Column(name = "idx")
  private int idx;

  public HandleIdx() {

  }

  public HandleIdx(byte[] h, int i) {
    handle = h;
    idx = i;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    HandleIdx handleIdx = (HandleIdx) o;
    return ((handle == handleIdx.handle) && (idx == handleIdx.idx));
  }


  @Override
  public int hashCode() {
    return Objects.hash(handle, idx);
  }

  public String toString() {
    return Base64.getEncoder().encodeToString(handle) + " | " + idx;
  }

  // Getters
  public int getIdx() {
    return idx;
  }

  public byte[] getHandle() {
    return handle;
  }

  public String getHandleStr() {
    return new String(handle);
  }

  public String getIdxStr() {
    return String.valueOf(idx);
  }

  // Setters

  public void setIdx(int idx) {
    this.idx = idx;
  }

  public void setHandle(byte[] handle) {
    this.handle = handle;
  }

}
