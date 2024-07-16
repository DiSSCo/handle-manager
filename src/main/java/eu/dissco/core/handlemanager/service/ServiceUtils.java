package eu.dissco.core.handlemanager.service;

import eu.dissco.core.handlemanager.domain.fdo.FdoProfile;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.FdoAttribute;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServiceUtils {

  private ServiceUtils() {
  }

  public static FdoAttribute getField(List<FdoAttribute> fdoAttributes, FdoProfile targetField) {
    for (var attribute : fdoAttributes) {
      if (attribute.getIndex() == targetField.index()) {
        return attribute;
      }
    }
    log.error("Unable to find field {} in record {}", targetField, fdoAttributes);
    throw new IllegalStateException();
  }


}
