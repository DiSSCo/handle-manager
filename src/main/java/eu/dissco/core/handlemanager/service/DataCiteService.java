package eu.dissco.core.handlemanager.service;

import static eu.dissco.core.handlemanager.service.ServiceUtils.mapResolvedRecords;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.dissco.core.handlemanager.domain.requests.datacite.DcRequest;
import eu.dissco.core.handlemanager.exceptions.DataCiteException;
import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
import eu.dissco.core.handlemanager.repository.HandleRepository;
import eu.dissco.core.handlemanager.web.DataCiteClient;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataCiteService {
  private final DataCiteClient dataCiteClient;
  private final HandleRepository handleRep;
  private final ObjectMapper mapper;

  public void registerDoi(List<String> handles)
      throws DataCiteException {
    var handleBytes = handles.stream().map(h -> h.getBytes(StandardCharsets.UTF_8)).toList();
    var resolvedRecordsFlatList = handleRep.resolveHandleAttributes(handleBytes);
    var pidMap = mapResolvedRecords(resolvedRecordsFlatList);
    allHandlesPresent(handles, pidMap.keySet());
    List<DcRequest> dcRequests = new ArrayList<>();
    for (var handleRecord : pidMap.entrySet()){
      dcRequests.add(new DcRequest(handleRecord.getValue()));
    }
    for (var dcRequest : dcRequests){
      var requestBody = mapper.valueToTree(dcRequest);
      dataCiteClient.sendDoiRequest(requestBody);
    }
  }

  private void allHandlesPresent(List<String> handles, Set<String> keys){
    var handleSet = new HashSet<>(handles);
    handleSet.removeAll(keys);
    if(!handleSet.isEmpty()){
      log.warn("Some handles were not found: {}", handleSet);
    }
  }
}
