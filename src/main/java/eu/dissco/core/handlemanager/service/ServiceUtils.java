package eu.dissco.core.handlemanager.service;

import eu.dissco.core.handlemanager.domain.requests.attributes.DigitalSpecimenRequest;
import eu.dissco.core.handlemanager.domain.requests.attributes.MediaObjectRequest;
import eu.dissco.core.handlemanager.domain.requests.attributes.PhysicalIdType;
import eu.dissco.core.handlemanager.domain.requests.attributes.PhysicalIdentifier;
import java.nio.charset.StandardCharsets;

public class ServiceUtils {

  private ServiceUtils(){}

  public static <T extends DigitalSpecimenRequest> byte[] setUniquePhysicalIdentifierId(T request) {
    var physicalIdentifier = request.getPhysicalIdentifier();
    if (physicalIdentifier.physicalIdType() == PhysicalIdType.CETAF) {
      return physicalIdentifier.physicalId().getBytes(StandardCharsets.UTF_8);
    }
    return concatIds(physicalIdentifier, request.getSpecimenHostPid());
  }

  public static byte[] setUniquePhysicalIdentifierId(MediaObjectRequest request) {
    var physicalIdentifier = request.getSubjectPhysicalIdentifier();
    if (physicalIdentifier.physicalIdType() == PhysicalIdType.CETAF) {
      return physicalIdentifier.physicalId().getBytes(StandardCharsets.UTF_8);
    }
    return concatIds(physicalIdentifier, request.getSubjectSpecimenHostPid());
  }

  private static byte[] concatIds(PhysicalIdentifier physicalIdentifier, String specimenHostPid) {
    var hostIdArr = specimenHostPid.split("/");
    var hostId = hostIdArr[hostIdArr.length - 1];
    return (physicalIdentifier.physicalId() + ":" + hostId).getBytes(StandardCharsets.UTF_8);
  }

}
