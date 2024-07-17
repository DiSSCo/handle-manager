package eu.dissco.core.handlemanager.domain.repsitoryobjects;

import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.HS_ADMIN;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import eu.dissco.core.handlemanager.domain.fdo.FdoProfile;
import java.time.Instant;
import java.util.LinkedHashMap;
import lombok.Value;
import org.bson.Document;

@Value
public class FdoAttribute {

  int index;
  String type;
  HandleData data;
  int ttl;
  Instant timestamp;

  public FdoAttribute(FdoProfile fdoAttribute, Instant timestamp, String value) {
    this.index = fdoAttribute.index();
    this.type = fdoAttribute.get();
    this.timestamp = timestamp;
    this.data = new StringHandleData(value);
    this.ttl = 86400;
  }

  public FdoAttribute(Instant timestamp, String prefix) {
    this.index = HS_ADMIN.index();
    this.type = HS_ADMIN.get();
    this.data = new AdminHandleData(prefix);
    this.timestamp = timestamp;
    this.ttl = 86400;
  }

  @JsonCreator
  public FdoAttribute(Integer index, String type, Integer ttl, Double timestamp, Document data) {
    this.index = index;
    this.type = type;
    var value = data.get("value", Object.class);
    if (value instanceof String valueString) {
      this.data = new StringHandleData(valueString);
    } else if (value == null) {
      this.data = new StringHandleData(null);
    } else {
      var map = data.get("value", LinkedHashMap.class);
      var prefix = map.get("handle").toString().replace("0.NA/", "");
      this.data = new AdminHandleData(prefix);
    }
    this.ttl = ttl;
    this.timestamp = Instant.ofEpochSecond(timestamp.longValue());
  }

  @JsonIgnore
  public String getValue() {
    if (data instanceof StringHandleData d) {
      return (d.getValue());
    } else {
      return "HS_ADMIN";
    }
  }

}
