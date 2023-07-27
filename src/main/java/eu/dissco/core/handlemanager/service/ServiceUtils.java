package eu.dissco.core.handlemanager.service;

import eu.dissco.core.handlemanager.domain.repsitoryobjects.HandleAttribute;
import eu.dissco.core.handlemanager.domain.requests.objects.DigitalSpecimenRequest;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.PhysicalIdType;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ServiceUtils {

  private ServiceUtils(){}

  public static <T extends DigitalSpecimenRequest> String setUniquePhysicalIdentifierId(T request) {
    var physicalIdentifier = request.getPrimarySpecimenObjectId();
    if (request.getPrimarySpecimenObjectIdType().equals(PhysicalIdType.CETAF)) {
      return physicalIdentifier;
    }
    return concatIds(physicalIdentifier, request.getSpecimenHost());
  }

  private static String concatIds(String physicalIdentifier, String specimenHostPid) {
    var hostIdArr = specimenHostPid.split("/");
    var hostId = hostIdArr[hostIdArr.length - 1];
    return (physicalIdentifier + ":" + hostId);
  }

  protected static HashMap<String, List<HandleAttribute>> mapResolvedRecords(List<HandleAttribute> flatList) {

    HashMap<String, List<HandleAttribute>> handleMap = new HashMap<>();

    for (HandleAttribute row : flatList) {
      String handle = new String(row.handle(), StandardCharsets.UTF_8);
      if (handleMap.containsKey(handle)) {
        List<HandleAttribute> tmpList = new ArrayList<>(handleMap.get(handle));
        tmpList.add(row);
        handleMap.replace(handle, tmpList);
      } else {
        handleMap.put(handle, List.of(row));
      }
    }
    return handleMap;
  }

}
