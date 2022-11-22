package eu.dissco.core.handlemanager.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
import eu.dissco.core.handlemanager.repository.HandleRepository;
import eu.dissco.core.handlemanager.repositoryobjects.Handles;
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
      throws PidResolutionException, JsonProcessingException {
    if (typePid == null) {
      throw new PidResolutionException("Missing PID in request body.");
    }

    List<Handles> typeRecord = handleRep.resolveHandle(typePid.getBytes());

    if (typeRecord.isEmpty()) {
      throw new PidResolutionException("Unable to resolve type PID");
    }

    String pid = getDataFromType("pid", typeRecord);
    String primaryNameFromPid = getDataFromType("primaryNameFromPid", typeRecord);
    String pidType;
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

    if (pidType.equals("") || primaryNameFromPid.equals(
        "")) { // If one of these were not resolvable
      throw new PidResolutionException(
          "One of the type PIDs provided resolves to an invalid record. reason: pid type and/or primaryNameFromPid are empty. Check PID "
              + typePid
              + " and try again");
    }

    objectNode.put("pid", pid);
    objectNode.put("pidType", pidType);
    objectNode.put("primaryNamefromPid", primaryNameFromPid);
    if (pidType.equals("doi")) {
      objectNode.put("registrationAgencyDoiName", registrationAgencyDoiName);
    }
    return mapper.writeValueAsString(objectNode);
  }

  private String getDataFromType(String type, List<Handles> hList) {
    for (Handles h : hList) {
      if (h.getType().equals(type)) {
        return h.getData();
      }
    }
    return ""; // This should maybe return a warning?
  }

}
