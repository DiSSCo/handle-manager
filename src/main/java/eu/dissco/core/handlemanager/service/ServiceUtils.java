package eu.dissco.core.handlemanager.service;

import eu.dissco.core.handlemanager.domain.requests.objects.DigitalSpecimenRequest;
import eu.dissco.core.handlemanager.domain.requests.objects.MediaObjectRequest;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.PhysicalIdType;

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
    var physicalIdentifier = request.getSubjectIdentifier();
    if (physicalIdentifier.physicalIdType().equals(PhysicalIdType.CETAF)) {
      return physicalIdentifier.physicalId();
    }
    return concatIds(physicalIdentifier.physicalId(), request.getSubjectSpecimenHost());
  }

  private static String concatIds(String physicalIdentifier, String specimenHostPid) {
    var hostIdArr = specimenHostPid.split("/");
    var hostId = hostIdArr[hostIdArr.length - 1];
    return (physicalIdentifier + ":" + hostId);
  }

}
