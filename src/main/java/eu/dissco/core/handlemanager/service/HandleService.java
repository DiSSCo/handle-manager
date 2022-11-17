package eu.dissco.core.handlemanager.service;

import static eu.dissco.core.handlemanager.utils.Resources.setLocations;

import com.fasterxml.jackson.core.JsonProcessingException;
import eu.dissco.core.handlemanager.domain.requests.DigitalSpecimenBotanyRequest;
import eu.dissco.core.handlemanager.domain.requests.DigitalSpecimenRequest;
import eu.dissco.core.handlemanager.domain.requests.DoiRecordRequest;
import eu.dissco.core.handlemanager.domain.requests.HandleRecordRequest;
import eu.dissco.core.handlemanager.domain.responses.DigitalSpecimenBotanyResponse;
import eu.dissco.core.handlemanager.domain.responses.DigitalSpecimenResponse;
import eu.dissco.core.handlemanager.domain.responses.DoiRecordResponse;
import eu.dissco.core.handlemanager.domain.responses.HandleRecordResponse;
import eu.dissco.core.handlemanager.exceptions.PidCreationException;
import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
import eu.dissco.core.handlemanager.repository.HandleRepository;
import eu.dissco.core.handlemanager.repositoryobjects.Handles;
import eu.dissco.core.handlemanager.utils.Resources;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// Postgres value in (value1, value2)..
// Generate all handles before posting...


@Service
@RequiredArgsConstructor
@Slf4j
public class HandleService {

  @Autowired
  public HandleRepository handleRep;

  @Autowired
  PidTypeService pidTypeService;

  @Autowired
  private Clock clock;
  @Autowired
  private HandleGeneratorService hf;

  public List<String> getHandlesPaged(String pidStatus, int pageNum, int pageSize) {
    return getStrList(handleRep.getHandles(pidStatus.getBytes(), pageNum, pageSize));
  }

  public List<String> getHandlesPaged(int pageNum, int pageSize) {
    return getStrList(handleRep.getHandles(pageNum, pageSize));
  }


  // Create Handle Record Batch
  public List<HandleRecordResponse> createHandleRecordBatch(List<HandleRecordRequest> requests)
      throws PidResolutionException, JsonProcessingException {
    List<byte[]> handles = hf.genHandleList(requests.size());


    long timestamp = clock.instant().getEpochSecond();
    List<Handles> handleRecord;
    List<Handles> handleRecordsAll = new ArrayList<>();
    List<HandleRecordResponse> response = new ArrayList<>();

    log.info("handles to mint: " +requests.size());

    for (int i = 0; i < requests.size(); i++) {
      log.info("index = " + i);
      // Prepare handle record as list of Handles
      handleRecord = prepareHandleRecord(requests.get(i), handles.get(i), timestamp);

      // Add list of handle entries (i.e. collection of single rows in db) to list to
      // be posted at the end
      handleRecordsAll.addAll(handleRecord);

      // Add new handleRecordResponse to our response list
      response.add(new HandleRecordResponse(handleRecord));

      // Clear our list
      handleRecord.clear();
    }

    // Save all records
    handleRep.saveAll(handleRecordsAll);
    return response;
  }

  public List<DoiRecordResponse> createDoiRecordBatch(List<DoiRecordRequest> requests)
      throws PidResolutionException, JsonProcessingException {
    List<byte[]> handles = hf.genHandleList(requests.size());
    long timestamp = clock.instant().getEpochSecond();
    List<Handles> doiRecord;
    List<Handles> doiRecordsAll = new ArrayList<>();
    List<DoiRecordResponse> response = new ArrayList<>();
    for (int i = 0; i < requests.size(); i++) {

      // Prepare record as list of Handles
      doiRecord = prepareDoiRecord(requests.get(i), handles.get(i), timestamp);

      // Add list of Handle entries (i.e. collection of single rows in db) to list to
      // be posted at the end
      doiRecordsAll.addAll(doiRecord);

      // Add new doiRecordResponse to our response list
      response.add(new DoiRecordResponse(doiRecord));

      // Clear our list
      doiRecord.clear();
    }

    // Save all records
    handleRep.saveAll(doiRecordsAll);
    return response;
  }

  public List<DigitalSpecimenResponse> createDigitalSpecimenBatch(
      List<DigitalSpecimenRequest> requests)
      throws PidResolutionException, JsonProcessingException {
    List<byte[]> handles = hf.genHandleList(requests.size());
    log.info("Handles to generate: "+requests.size());
    log.info("first handle: " + new String(handles.get(0)));

    long timestamp = clock.instant().getEpochSecond();
    List<Handles> digitalSpecimenRecord;
    List<Handles> digitalSpecimenRecordsAll = new ArrayList<>();
    List<DigitalSpecimenResponse> response = new ArrayList<>();
    for (int i = 0; i < requests.size(); i++) {

      // Prepare record as list of Handles
      digitalSpecimenRecord = prepareDigitalSpecimenRecord(requests.get(i), handles.get(i),
          timestamp);

      // Add list of Handle entries (i.e. collection of single rows in db) to list to
      // be posted at the end
      digitalSpecimenRecordsAll.addAll(digitalSpecimenRecord);

      // Add new DigitalSpecimenResponse to our response list
      response.add(new DigitalSpecimenResponse(digitalSpecimenRecord));

      // Clear our list
      digitalSpecimenRecord.clear();
    }
    // Save all records
    handleRep.saveAll(digitalSpecimenRecordsAll);
    return response;
  }

  public List<DigitalSpecimenBotanyResponse> createDigitalSpecimenBotanyBatch(
      List<DigitalSpecimenBotanyRequest> requests)
      throws PidResolutionException, JsonProcessingException {
    List<byte[]> handles = hf.genHandleList(requests.size());
    long timestamp = clock.instant().getEpochSecond();
    List<Handles> digitalSpecimenBotanyRecord;
    List<Handles> digitalSpecimenRecordsAll = new ArrayList<>();
    List<DigitalSpecimenBotanyResponse> response = new ArrayList<>();
    for (int i = 0; i < requests.size(); i++) {

      // Prepare record as list of Handles
      digitalSpecimenBotanyRecord = prepareDigitalSpecimenBotanyRecord(requests.get(i),
          handles.get(i),
          timestamp);

      // Add list of Handle entries (i.e. collection of single rows in db) to list to
      // be posted at the end
      digitalSpecimenRecordsAll.addAll(digitalSpecimenBotanyRecord);

      // Add new doiRecordResponse to our response list
      response.add(new DigitalSpecimenBotanyResponse(digitalSpecimenBotanyRecord));

      // Clear our list
      digitalSpecimenBotanyRecord.clear();
    }
    // Save all records
    handleRep.saveAll(digitalSpecimenRecordsAll);
    return response;
  }

  // Todo Minimize branch points, break this up, lower cognitive complexity
  public HandleRecordResponse createHandleRecord(HandleRecordRequest request)
      throws PidResolutionException, JsonProcessingException {
    byte[] handle = hf.genHandleList(1).get(0);
    log.info("handle generated: " + new String(handle));

    long timestamp = clock.instant().getEpochSecond();
    List<Handles> handleRecord;
    handleRecord = prepareHandleRecord(request, handle, timestamp);
    List<Handles> posted = handleRep.saveAll(handleRecord);
    return new HandleRecordResponse(posted);
  }

  public DoiRecordResponse createDoiRecord(DoiRecordRequest request)
      throws PidResolutionException, JsonProcessingException {
    byte[] handle = hf.genHandleList(1).get(0);
    long timestamp = clock.instant().getEpochSecond();
    List<Handles> handleRecord;
    handleRecord = prepareDoiRecord(request, handle, timestamp);
    List<Handles> posted = handleRep.saveAll(handleRecord);
    return new DoiRecordResponse(posted);
  }

  public DigitalSpecimenResponse createDigitalSpecimenRecord(DigitalSpecimenRequest request)
      throws PidResolutionException, JsonProcessingException {
    byte[] handle = hf.genHandleList(1).get(0);
    long timestamp = clock.instant().getEpochSecond();
    List<Handles> handleRecord;
    handleRecord = prepareDigitalSpecimenRecord(request, handle, timestamp);
    List<Handles> posted = handleRep.saveAll(handleRecord);
    return new DigitalSpecimenResponse(posted);
  }

  public DigitalSpecimenBotanyResponse createDigitalSpecimenBotanyRecord(DigitalSpecimenBotanyRequest request)
      throws PidResolutionException, JsonProcessingException {
    byte[] handle = hf.genHandleList(1).get(0);
    long timestamp = clock.instant().getEpochSecond();
    List<Handles> handleRecord;
    handleRecord = prepareDigitalSpecimenBotanyRecord(request, handle, timestamp);
    List<Handles> posted = handleRep.saveAll(handleRecord);
    return new DigitalSpecimenBotanyResponse(posted);
  }

  // Prepare Record Lists

  private List<Handles> prepareHandleRecord(HandleRecordRequest request, byte[] handle,
      long timestamp) throws PidResolutionException, JsonProcessingException {
    List<Handles> handleRecord = new ArrayList<>();

    // 100: Admin Handle
    handleRecord.add(Resources.genAdminHandle(handle, timestamp));

    //Todo hardcode index values
    // 1: Pid
    byte[] pid = concatBytes("https://hdl.handle.net/".getBytes(),
        handle); // Maybe this should check if it's a DOI?
    handleRecord.add(new Handles(handle, 1, "pid", pid, timestamp));

    // 2: PidIssuer
    String pidIssuer = pidTypeService.resolveTypePid(request.getPidIssuerPid());
    handleRecord.add(new Handles(handle, 2, "pidIssuer", pidIssuer, timestamp));

    // 3: Digital Object Type
    String digitalObjectType = pidTypeService.resolveTypePid(request.getDigitalObjectTypePid());
    handleRecord.add(new Handles(handle, 3, "digitalObjectType", digitalObjectType, timestamp));

    // 4: Digital Object Subtype
    String digitalObjectSubtype = pidTypeService.resolveTypePid(
        request.getDigitalObjectSubtypePid());
    handleRecord.add(
        new Handles(handle, 4, "digitalObjectSubtype", digitalObjectSubtype, timestamp));

    // 5: 10320/loc
    byte[] loc = "".getBytes();
    try {
      loc = setLocations(request.getLocations());
    } catch (TransformerException | ParserConfigurationException e) {
      e.printStackTrace();
    }
    handleRecord.add(new Handles(handle, 5, "10320/loc", loc, timestamp));

    // 6: Issue Date
    handleRecord.add((new Handles(handle, 6, "issueDate", getDate(), timestamp)));

    // 7: Issue number
    handleRecord.add(
        (new Handles(handle, 7, "issueNumber", "1", timestamp))); // Will every created handle
    // have a 1 for the issue date?

    // 8: PidStatus
    handleRecord.add(
        (new Handles(handle, 8, "pidStatus", "DRAFT", timestamp))); // Can I keep this as TEST?

    // 9, 10: tombstone text, tombstone pids -> Skip

    // 11: PidKernelMetadataLicense:
    // https://creativecommons.org/publicdomain/zero/1.0/
    handleRecord.add((new Handles(handle, 11, "pidKernelMetadataLicense",
        "https://creativecommons.org/publicdomain/zero/1.0/", timestamp)));

    return handleRecord;
  }

  private List<Handles> prepareDoiRecord(DoiRecordRequest request, byte[] handle, long timestamp)
      throws PidResolutionException, JsonProcessingException {
    List<Handles> handleRecord = prepareHandleRecord(request, handle, timestamp);

    // 12: Referent DOI Name
    String referentDoiName = pidTypeService.resolveTypePid(request.getReferentDoiName());
    handleRecord.add(new Handles(handle, 12, "referentDoiName", referentDoiName, timestamp));

    // 13: Referent -> NOTE: Referent is blank currently until we have a model for
    // it
    handleRecord.add(new Handles(handle, 13, "referent", request.getReferent(), timestamp));

    return handleRecord;
  }

  private List<Handles> prepareDigitalSpecimenRecord(DigitalSpecimenRequest request, byte[] handle,
      long timestamp) throws PidResolutionException, JsonProcessingException {
    List<Handles> handleRecord = prepareDoiRecord(request, handle, timestamp);

    // 14: digitalOrPhysical
    handleRecord.add(
        new Handles(handle, 14, "digitalOrPhysical", request.getDigitalOrPhysical(), timestamp));

    // 15: specimenHost
    String specimenHost = pidTypeService.resolveTypePid(request.getSpecimenHostPid());
    handleRecord.add(new Handles(handle, 15, "specimenHost", specimenHost, timestamp));

    // 16: In collectionFacility
    String inCollectionFacility = pidTypeService.resolveTypePid(
        request.getInCollectionFacilityPid());
    handleRecord.add(
        new Handles(handle, 16, "inCollectionFacility", inCollectionFacility, timestamp));

    return handleRecord;
  }

  private List<Handles> prepareDigitalSpecimenBotanyRecord(DigitalSpecimenBotanyRequest request,
      byte[] handle,
      long timestamp) throws PidResolutionException, JsonProcessingException {
    List<Handles> handleRecord = prepareDigitalSpecimenRecord(request, handle, timestamp);



    // 17: ObjectType
    handleRecord.add(new Handles(handle, 17, "objectType", request.getObjectType(), timestamp));

    // 18: preservedOrLiving
    handleRecord.add(
        new Handles(handle, 17, "preservedOrLiving", request.getPreservedOrLiving(), timestamp));

    return handleRecord;
  }

  private byte[] concatBytes(byte[] a, byte[] b) {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    try {
      outputStream.write(a);
      outputStream.write(b);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return outputStream.toByteArray();
  }


  private String getDate() {
    DateTimeFormatter dt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    LocalDateTime now = LocalDateTime.now(clock);
    return dt.format(now);
  }

  // Given a list of Handles (of unknown pidStatus), return HandleRecord

  // Minting Handles

  // Mint a list of handles

  private List<String> getStrList(List<byte[]> byteList) {
    // It's crazy that ArrayList doesn't have a native type converter (especially
    // from byte[] to str?
    // If there's a better way to do this that would be cool

    int l = byteList.size();
    List<String> strList = new ArrayList<>(l);
    String s;
    for (byte[] b : byteList) {
      s = byteToString(b);
      strList.add(s);
    }
    return strList;
  }
  private String byteToString(byte[] b) {
    return new String(b, StandardCharsets.UTF_8);
  }

}

