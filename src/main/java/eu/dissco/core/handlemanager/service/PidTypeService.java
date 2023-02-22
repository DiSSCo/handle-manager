package eu.dissco.core.handlemanager.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.HandleAttribute;
import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
import eu.dissco.core.handlemanager.repository.HandleRepository;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PidTypeService {

  private final HandleRepository handleRep;
  private final ObjectMapper mapper;

  @Cacheable(value = "cache")
  public String resolveTypePid(String typePid)
      throws PidResolutionException {
    if (typePid == null) {
      throw new PidResolutionException("Missing a PID field in request body.");
    }
    List<HandleAttribute> typeRecord = handleRep.resolveHandleAttributes(typePid.getBytes(
        StandardCharsets.UTF_8));
    if (typeRecord.isEmpty()) {
      log.info(typePid);
      throw new PidResolutionException(
          "Unable to resolve PID: " + typePid + ", Reason: Record is empty");
    }
    String pid = getDataFromType("pid", typeRecord);
    String primaryNameFromPid = getDataFromType("primaryNameFromPid", typeRecord);
    String pidType = "";
    String registrationAgencyDoiName = "";
    ObjectNode objectNode = mapper.createObjectNode();

    if (pid.contains("doi")) {
      pidType = "doi";
      registrationAgencyDoiName = getDataFromType("registrationAgencyDoiName", typeRecord);

    } else if (pid.contains("handle")) {
      pidType = "handle";
    } else {
      throw new PidResolutionException(
          "One of the type PIDs provided resolves to an invalid record. Reason: neither \"handle\" nor \"doi\". Check handle "
              + typePid
              + " and try again");
    }
    if (primaryNameFromPid.equals("")) { // If this is not resolvableof these were not resolvable
      throw new PidResolutionException(
          "One of the type PIDs provided resolves to an invalid record. reason: pid type and/or primaryNameFromPid are empty. Check PID "
              + typePid
              + " and try again");
    }

    objectNode.put("pid", pid);
    objectNode.put("pidType", pidType);
    objectNode.put("primaryNameFromPid", primaryNameFromPid);

    if (pidType.equals("doi")) {
      objectNode.put("registrationAgencyDoiName", registrationAgencyDoiName);
    }
    try {
      return mapper.writeValueAsString(objectNode);
    } catch (JsonProcessingException e) {
      throw new PidResolutionException("A JSON processing error has occurred. " + e.getMessage());
    }

  }

  private String getDataFromType(String type, List<HandleAttribute> hList) {
    for (HandleAttribute h : hList) {
      if (h.type().equals(type)) {
        return new String(h.data());
      }
    }
    log.warn("Type {} is not set in the handle record", type);
    return "";
  }

}
