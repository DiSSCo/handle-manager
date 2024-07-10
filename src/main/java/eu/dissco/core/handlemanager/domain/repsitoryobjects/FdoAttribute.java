package eu.dissco.core.handlemanager.domain.repsitoryobjects;

import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.HS_ADMIN;

import com.fasterxml.jackson.annotation.JsonIgnore;
import eu.dissco.core.handlemanager.domain.fdo.FdoProfile;
import java.time.Instant;
import lombok.Value;

@Value
public class FdoAttribute {

  int index;
  String type;
  HandleData data;
  int ttl = 86400;
  Instant timestamp;

  public FdoAttribute(FdoProfile fdoAttribute, Instant timestamp, String value) {
    this.index = fdoAttribute.index();
    this.type = fdoAttribute.get();
    this.timestamp = timestamp;
    this.data = new StringHandleData(value);
  }

  public FdoAttribute(Instant timestamp, String prefix) {
    this.index = HS_ADMIN.index();
    this.type = HS_ADMIN.get();
    this.data = new AdminHandleData(prefix);
    this.timestamp = timestamp;
  }

  @JsonIgnore
  public String getValue() {
    if (data instanceof StringHandleData) {
      return ((StringHandleData) data).getValue();
    } else {
      return "HS_ADMIN";
    }
  }

}
