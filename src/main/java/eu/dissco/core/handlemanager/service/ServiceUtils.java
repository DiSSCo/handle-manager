package eu.dissco.core.handlemanager.service;

import eu.dissco.core.handlemanager.domain.requests.attributes.DigitalSpecimenRequest;
import eu.dissco.core.handlemanager.domain.requests.attributes.MediaObjectRequest;
import eu.dissco.core.handlemanager.domain.requests.attributes.PhysicalIdType;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class ServiceUtils {

  private ServiceUtils(){}

  public static <T extends DigitalSpecimenRequest> String setUniquePhysicalIdentifierId(T request) {
    var physicalIdentifier = request.getPrimarySpecimenObjectId();
    if (request.getPrimarySpecimenObjectIdType().equals(PhysicalIdType.CETAF)) {
      return physicalIdentifier;
    }
    return concatIds(physicalIdentifier, request.getSpecimenHost());
  }

  public static String setUniquePhysicalIdentifierId(MediaObjectRequest request) {
    var physicalIdentifier = request.getSubjectPhysicalIdentifier();
    if (physicalIdentifier.physicalIdType().equals(PhysicalIdType.CETAF)) {
      return physicalIdentifier.physicalId();
    }
    return concatIds(physicalIdentifier.physicalId(), request.getSubjectSpecimenHostPid());
  }

  private static String concatIds(String physicalIdentifier, String specimenHostPid) {
    var hostIdArr = specimenHostPid.split("/");
    var hostId = hostIdArr[hostIdArr.length - 1];
    return (physicalIdentifier + ":" + hostId);
  }

  public static <T> Collector<T, ?, T> toSingleObject() {
    return Collectors.collectingAndThen(
        Collectors.toList(),
        list -> {
          if (list.size() != 1) {
            throw new IllegalStateException();
          }
          return list.get(0);
        }
    );
  }

}
