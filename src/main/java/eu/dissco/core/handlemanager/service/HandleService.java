package eu.dissco.core.handlemanager.service;

import static eu.dissco.core.handlemanager.utils.Resources.genAdminHandle;

import com.fasterxml.jackson.core.JsonProcessingException;
import eu.dissco.core.handlemanager.domain.pidrecords.HandleAttribute;
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
import eu.dissco.core.handlemanager.jparepository.HandleRepository;
import eu.dissco.core.handlemanager.jparepository.Handles;
import eu.dissco.core.handlemanager.repository.JooqHandleRepository;
import eu.dissco.core.handlemanager.utils.Resources;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

// Postgres value in (value1, value2)..
// Generate all handles before posting...


@Service
@RequiredArgsConstructor
@Slf4j
public class HandleService {

  private final HandleRepository handleRep;
  private final JooqHandleRepository jooqHandleRep;

  private final PidTypeService pidTypeService;

  private final HandleGeneratorService hf;
  private final DocumentBuilderFactory dbf;

  private final TransformerFactory tf;

  public List<String> getHandlesPaged(String pidStatus, int pageNum, int pageSize) {
    return getStrList(handleRep.getHandles(pidStatus.getBytes(), pageNum, pageSize));
  }

  public List<String> getHandlesPaged(int pageNum, int pageSize) {
    return getStrList(handleRep.getHandles(pageNum, pageSize));
  }


  // Create Handle Record Batch
  public List<HandleRecordResponse> createHandleRecordBatch(List<HandleRecordRequest> requests)
      throws PidResolutionException, JsonProcessingException, ParserConfigurationException, TransformerException {
    List<byte[]> handles = hf.genHandleList(requests.size());

    long timestamp = Instant.now().getEpochSecond();
    List<Handles> handleRecord;
    List<Handles> handleRecordsAll = new ArrayList<>();
    List<HandleRecordResponse> response = new ArrayList<>();

    for (int i = 0; i < requests.size(); i++) {
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
      throws PidResolutionException, JsonProcessingException, ParserConfigurationException, TransformerException {
    List<byte[]> handles = hf.genHandleList(requests.size());
    long timestamp = Instant.now().getEpochSecond();
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
      throws PidResolutionException, JsonProcessingException, ParserConfigurationException, TransformerException {
    List<byte[]> handles = hf.genHandleList(requests.size());

    long timestamp = Instant.now().getEpochSecond();
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
      throws PidResolutionException, JsonProcessingException, ParserConfigurationException, TransformerException {
    List<byte[]> handles = hf.genHandleList(requests.size());
    long timestamp = Instant.now().getEpochSecond();
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

  // Jooq

  public HandleRecordResponse createHandleRecordJooq(HandleRecordRequest request)
      throws PidResolutionException, ParserConfigurationException, JsonProcessingException, TransformerException, PidCreationException {
    byte[] handle = hf.genHandleList(1).get(0);
    List<HandleAttribute> handleRecord = prepareHandleRecordAttributes(request, handle);
    var recordTimestamp = Instant.now();
    return jooqHandleRep.createHandle(handle, recordTimestamp, handleRecord);
  }

  public DoiRecordResponse createDoiRecordJooq(DoiRecordRequest request)
      throws PidResolutionException, ParserConfigurationException, JsonProcessingException, TransformerException, PidCreationException {
    byte[] handle = hf.genHandleList(1).get(0);
    List<HandleAttribute> handleRecord = prepareDoiRecordAttributes(request, handle);
    var recordTimestamp = Instant.now();
    return jooqHandleRep.createDoi(handle, recordTimestamp, handleRecord);
  }

  public DigitalSpecimenResponse createDigitalSpecimenJooq(DigitalSpecimenRequest request)
      throws PidResolutionException, ParserConfigurationException, JsonProcessingException, TransformerException, PidCreationException {
    byte[] handle = hf.genHandleList(1).get(0);
    List<HandleAttribute> handleRecord = prepareDigitalSpecimenRecordAttributes(request, handle);
    var recordTimestamp = Instant.now();
    return jooqHandleRep.createDigitalSpecimen(handle, recordTimestamp, handleRecord);
  }

  public DigitalSpecimenBotanyResponse createDigitalSpecimenBotanyJooq(DigitalSpecimenBotanyRequest request)
      throws PidResolutionException, ParserConfigurationException, JsonProcessingException, TransformerException, PidCreationException {
    byte[] handle = hf.genHandleList(1).get(0);
    List<HandleAttribute> handleRecord = prepareDigitalSpecimenBotanyRecordAttributes(request, handle);
    var recordTimestamp = Instant.now();
    return jooqHandleRep.createDigitalSpecimenBotany(handle, recordTimestamp, handleRecord);
  }

  // JPA

  public HandleRecordResponse createHandleRecord(HandleRecordRequest request)
      throws PidResolutionException, JsonProcessingException, ParserConfigurationException, TransformerException {
    byte[] handle = hf.genHandleList(1).get(0);

    long timestamp = Instant.now().getEpochSecond();
    List<Handles> handleRecord;
    handleRecord = prepareHandleRecord(request, handle, timestamp);
    List<Handles> posted = handleRep.saveAll(handleRecord);
    return new HandleRecordResponse(posted);
  }

  public DoiRecordResponse createDoiRecord(DoiRecordRequest request)
      throws PidResolutionException, JsonProcessingException, ParserConfigurationException, TransformerException {
    byte[] handle = hf.genHandleList(1).get(0);
    long timestamp = Instant.now().getEpochSecond();
    List<Handles> handleRecord;
    handleRecord = prepareDoiRecord(request, handle, timestamp);
    List<Handles> posted = handleRep.saveAll(handleRecord);
    return new DoiRecordResponse(posted);
  }

  public DigitalSpecimenResponse createDigitalSpecimenRecord(DigitalSpecimenRequest request)
      throws PidResolutionException, JsonProcessingException, ParserConfigurationException, TransformerException {
    byte[] handle = hf.genHandleList(1).get(0);
    long timestamp = Instant.now().getEpochSecond();
    List<Handles> handleRecord;
    handleRecord = prepareDigitalSpecimenRecord(request, handle, timestamp);
    List<Handles> posted = handleRep.saveAll(handleRecord);
    return new DigitalSpecimenResponse(posted);
  }

  public DigitalSpecimenBotanyResponse createDigitalSpecimenBotanyRecord(
      DigitalSpecimenBotanyRequest request)
      throws PidResolutionException, JsonProcessingException, ParserConfigurationException, TransformerException {
    byte[] handle = hf.genHandleList(1).get(0);
    long timestamp = Instant.now().getEpochSecond();
    List<Handles> handleRecord;
    handleRecord = prepareDigitalSpecimenBotanyRecord(request, handle, timestamp);
    List<Handles> posted = handleRep.saveAll(handleRecord);
    return new DigitalSpecimenBotanyResponse(posted);
  }

  // Prepare Attribute lists

  private List<HandleAttribute> prepareHandleRecordAttributes(HandleRecordRequest request,
      byte[] handle)
      throws PidResolutionException, JsonProcessingException, ParserConfigurationException, TransformerException {
    List<HandleAttribute> handleRecord = new ArrayList<>();

    // 100: Admin Handle
    handleRecord.add(new HandleAttribute(100, "HS_ADMIN", genAdminHandle()));

    // 1: Pid
    byte[] pid = ("https://hdl.handle.net/" + new String(handle)).getBytes();
    handleRecord.add(new HandleAttribute(1, "pid", pid));

    // 2: PidIssuer
    String pidIssuer = pidTypeService.resolveTypePid(request.getPidIssuerPid());
    handleRecord.add(new HandleAttribute(2, "pidIssuer", pidIssuer.getBytes()));

    // 3: Digital Object Type
    String digitalObjectType = pidTypeService.resolveTypePid(request.getDigitalObjectTypePid());
    handleRecord.add(new HandleAttribute(3, "digitalObjectType", digitalObjectType.getBytes()));

    // 4: Digital Object Subtype
    String digitalObjectSubtype = pidTypeService.resolveTypePid(
        request.getDigitalObjectSubtypePid());
    handleRecord.add(
        new HandleAttribute(4, "digitalObjectSubtype", digitalObjectSubtype.getBytes()));

    // 5: 10320/loc
    byte[] loc = setLocations(request.getLocations());
    handleRecord.add(new HandleAttribute(5, "10320/loc", loc));

    // 6: Issue Date
    handleRecord.add(new HandleAttribute(6, "issueDate", getDate().getBytes()));

    // 7: Issue number
    handleRecord.add(new HandleAttribute(7, "issueNumber", "1".getBytes()));

    // 8: PidStatus
    handleRecord.add(new HandleAttribute(8, "pidStatus", "TEST".getBytes()));

    // 9, 10: tombstone text, tombstone pids -> Skip

    // 11: PidKernelMetadataLicense:
    byte[] pidKernelMetadataLicense = "https://creativecommons.org/publicdomain/zero/1.0/".getBytes();
    handleRecord.add(new HandleAttribute(11, "pidKernelMetadataLicense", pidKernelMetadataLicense));

    return handleRecord;
  }

  private List<HandleAttribute> prepareDoiRecordAttributes(DoiRecordRequest request, byte[] handle)
      throws PidResolutionException, JsonProcessingException, ParserConfigurationException, TransformerException {
    List<HandleAttribute> handleRecord = prepareHandleRecordAttributes(request, handle);

    // 12: Referent DOI Name
    String referentDoiName = pidTypeService.resolveTypePid(request.getReferentDoiName());
    handleRecord.add(new HandleAttribute(12, "referentDoiName", referentDoiName.getBytes()));

    // 13: Referent -> NOTE: Referent is blank currently until we have a model
    handleRecord.add(new HandleAttribute(13, "referent", request.getReferent().getBytes()));

    return handleRecord;
  }

  private List<HandleAttribute> prepareDigitalSpecimenRecordAttributes(
      DigitalSpecimenRequest request, byte[] handle)
      throws PidResolutionException, JsonProcessingException, ParserConfigurationException, TransformerException {
    List<HandleAttribute> handleRecord = prepareDoiRecordAttributes(request, handle);

    handleRecord.add(
        new HandleAttribute(14, "digitalOrPhysical", request.getDigitalOrPhysical().getBytes()));

    // 15: specimenHost
    String specimenHost = pidTypeService.resolveTypePid(request.getSpecimenHostPid());
    handleRecord.add(new HandleAttribute(15, "specimenHost", specimenHost.getBytes()));

    // 16: In collectionFacility
    String inCollectionFacility = pidTypeService.resolveTypePid(
        request.getInCollectionFacilityPid());
    handleRecord.add(
        new HandleAttribute(16, "inCollectionFacility", inCollectionFacility.getBytes()));

    return handleRecord;
  }
  private List<HandleAttribute> prepareDigitalSpecimenBotanyRecordAttributes(
      DigitalSpecimenBotanyRequest request,
      byte[] handle)
      throws PidResolutionException, JsonProcessingException, ParserConfigurationException, TransformerException {
    List<HandleAttribute> handleRecord = prepareDigitalSpecimenRecordAttributes(request, handle);

    // 17: ObjectType
    handleRecord.add(new HandleAttribute(17, "objectType", request.getObjectType().getBytes()));

    // 18: preservedOrLiving
    handleRecord.add(
        new HandleAttribute(18, "preservedOrLiving", request.getPreservedOrLiving().getBytes()));

    return handleRecord;
  }

  // Prepare Record Lists

  private List<Handles> prepareHandleRecord(HandleRecordRequest request, byte[] handle,
      long timestamp)
      throws PidResolutionException, JsonProcessingException, ParserConfigurationException, TransformerException {
    List<Handles> handleRecord = new ArrayList<>();

    // 100: Admin Handle
    handleRecord.add(Resources.genAdminHandle(handle, timestamp));

    // 1: Pid
    byte[] pid = ("https://hdl.handle.net/" + new String(handle)).getBytes();
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
    byte[] loc = setLocations(request.getLocations());
    handleRecord.add(new Handles(handle, 5, "10320/loc", loc, timestamp));

    // 6: Issue Date
    handleRecord.add((new Handles(handle, 6, "issueDate", getDate(), timestamp)));

    // 7: Issue number
    handleRecord.add(
        (new Handles(handle, 7, "issueNumber", "1", timestamp))); // Will every created handle

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
      throws PidResolutionException, JsonProcessingException, ParserConfigurationException, TransformerException {
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
      long timestamp)
      throws PidResolutionException, JsonProcessingException, ParserConfigurationException, TransformerException {
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
      long timestamp)
      throws PidResolutionException, JsonProcessingException, ParserConfigurationException, TransformerException {
    List<Handles> handleRecord = prepareDigitalSpecimenRecord(request, handle, timestamp);

    // 17: ObjectType
    handleRecord.add(new Handles(handle, 17, "objectType", request.getObjectType(), timestamp));

    // 18: preservedOrLiving
    handleRecord.add(
        new Handles(handle, 18, "preservedOrLiving", request.getPreservedOrLiving(), timestamp));

    return handleRecord;
  }



  private String getDate() {
    DateTimeFormatter dt = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH)
        .withZone(ZoneId.of("UTC"));
    Instant instant = Instant.now();
    return dt.format(instant);
  }

  private List<String> getStrList(List<byte[]> byteList) {
    int l = byteList.size();
    List<String> strList = new ArrayList<>(l);
    String s;
    for (byte[] b : byteList) {
      s = byteToString(b);
      strList.add(s);
    }
    return strList;
  }

  public byte[] setLocations(String[] objectLocations)
      throws TransformerException, ParserConfigurationException {

    DocumentBuilder documentBuilder = dbf.newDocumentBuilder();

    var doc = documentBuilder.newDocument();
    var locations = doc.createElement("locations");
    doc.appendChild(locations);
    for (int i = 0; i < objectLocations.length; i++) {

      var locs = doc.createElement("location");
      locs.setAttribute("id", String.valueOf(i));
      locs.setAttribute("href", objectLocations[i]);
      locs.setAttribute("weight", "0");
      locations.appendChild(locs);
    }
    return documentToString(doc).getBytes(StandardCharsets.UTF_8);
  }

  private String documentToString(Document document) throws TransformerException {
    var transformer = tf.newTransformer();
    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
    StringWriter writer = new StringWriter();
    transformer.transform(new DOMSource(document), new StreamResult(writer));
    return writer.getBuffer().toString();
  }

  private String byteToString(byte[] b) {
    return new String(b, StandardCharsets.UTF_8);
  }

}

