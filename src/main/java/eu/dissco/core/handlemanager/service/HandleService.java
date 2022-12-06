package eu.dissco.core.handlemanager.service;

import static eu.dissco.core.handlemanager.domain.PidRecords.DIGITAL_OBJECT_SUBTYPE;
import static eu.dissco.core.handlemanager.domain.PidRecords.DIGITAL_OBJECT_TYPE;
import static eu.dissco.core.handlemanager.domain.PidRecords.DIGITAL_OR_PHYSICAL;
import static eu.dissco.core.handlemanager.domain.PidRecords.FIELD_IDX;
import static eu.dissco.core.handlemanager.domain.PidRecords.HS_ADMIN;
import static eu.dissco.core.handlemanager.domain.PidRecords.IN_COLLECTION_FACILITY;
import static eu.dissco.core.handlemanager.domain.PidRecords.ISSUE_DATE;
import static eu.dissco.core.handlemanager.domain.PidRecords.ISSUE_NUMBER;
import static eu.dissco.core.handlemanager.domain.PidRecords.LOC;
import static eu.dissco.core.handlemanager.domain.PidRecords.OBJECT_TYPE;
import static eu.dissco.core.handlemanager.domain.PidRecords.PID;
import static eu.dissco.core.handlemanager.domain.PidRecords.PID_ISSUER;
import static eu.dissco.core.handlemanager.domain.PidRecords.PID_KERNEL_METADATA_LICENSE;
import static eu.dissco.core.handlemanager.domain.PidRecords.PID_STATUS;
import static eu.dissco.core.handlemanager.domain.PidRecords.PRESERVED_OR_LIVING;
import static eu.dissco.core.handlemanager.domain.PidRecords.REFERENT;
import static eu.dissco.core.handlemanager.domain.PidRecords.REFERENT_DOI_NAME;
import static eu.dissco.core.handlemanager.domain.PidRecords.SPECIMEN_HOST;
import static eu.dissco.core.handlemanager.domain.PidRecords.TOMBSTONE_PIDS;
import static eu.dissco.core.handlemanager.domain.PidRecords.TOMBSTONE_TEXT;
import static eu.dissco.core.handlemanager.utils.Resources.genAdminHandle;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiData;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiLinks;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiWrapper;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.HandleAttribute;
import eu.dissco.core.handlemanager.domain.requests.DigitalSpecimenBotanyRequest;
import eu.dissco.core.handlemanager.domain.requests.DigitalSpecimenRequest;
import eu.dissco.core.handlemanager.domain.requests.DoiRecordRequest;
import eu.dissco.core.handlemanager.domain.requests.HandleRecordRequest;
import eu.dissco.core.handlemanager.domain.requests.TombstoneRecordRequest;
import eu.dissco.core.handlemanager.domain.responses.DigitalSpecimenBotanyResponse;
import eu.dissco.core.handlemanager.domain.responses.DigitalSpecimenResponse;
import eu.dissco.core.handlemanager.domain.responses.DoiRecordResponse;
import eu.dissco.core.handlemanager.domain.responses.HandleRecordResponse;
import eu.dissco.core.handlemanager.exceptions.PidCreationException;
import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
import eu.dissco.core.handlemanager.repository.HandleRepository;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class HandleService {

  private final HandleRepository handleRep;
  private final PidTypeService pidTypeService;
  private final HandleGeneratorService hf;
  private final DocumentBuilderFactory dbf;
  private final ObjectMapper mapper;
  private final TransformerFactory tf;

  public JsonApiWrapper resolveSingleRecord(byte[] handle)
      throws PidResolutionException, JsonProcessingException {
    ObjectNode recordAttributes = handleRep.resolveSingleRecord(handle);
    JsonApiData jsonData = new JsonApiData(new String(handle), "PID", recordAttributes);
    JsonApiLinks links = new JsonApiLinks(mapper.writeValueAsString(recordAttributes.get("pid")));
    return new JsonApiWrapper(links, jsonData);
  }

  public List<JsonApiWrapper> resolveBatchRecord(List<byte[]> handles)
      throws JsonProcessingException {
    JsonApiData jsonData;
    JsonApiLinks links;
    List<JsonApiWrapper> wrapperList = new ArrayList<>();
    var recordAttributeList = handleRep.resolveBatchRecord(handles);

    for (ObjectNode recordAttributes : recordAttributeList){
      String pid = mapper.writeValueAsString(recordAttributes.get("pid"));
      jsonData = new JsonApiData(pid.substring(pid.length()-25), "PID", recordAttributes);
      links = new JsonApiLinks(pid);

      wrapperList.add(new JsonApiWrapper(links, jsonData));
    }
    return wrapperList;
  }

  //  Batch
  public List<HandleRecordResponse> createHandleRecordBatch(List<HandleRecordRequest> requests)
      throws PidResolutionException, ParserConfigurationException, JsonProcessingException, TransformerException, PidCreationException {
    List<byte[]> handles = hf.genHandleList(requests.size());
    List<HandleAttribute> handleAttributes = new ArrayList<>();

    for (int i = 0; i < requests.size(); i++) {
      handleAttributes.addAll(prepareHandleRecordAttributes(requests.get(i), handles.get(i)));
    }
    var recordTimestamp = Instant.now();

    return handleRep.createHandleRecordBatch(handles, recordTimestamp, handleAttributes);
  }

  public List<DoiRecordResponse> createDoiRecordBatch(List<DoiRecordRequest> requests)
      throws PidResolutionException, ParserConfigurationException, JsonProcessingException, TransformerException, PidCreationException {
    List<byte[]> handles = hf.genHandleList(requests.size());

    List<HandleAttribute> handleAttributes = new ArrayList<>();

    for (int i = 0; i < requests.size(); i++) {
      handleAttributes.addAll(prepareDoiRecordAttributes(requests.get(i), handles.get(i)));
    }
    var recordTimestamp = Instant.now();
    return handleRep.createDoiRecordBatch(handles, recordTimestamp, handleAttributes);
  }

  public List<DigitalSpecimenResponse> createDigitalSpecimenBatch(
      List<DigitalSpecimenRequest> requests)
      throws PidResolutionException, ParserConfigurationException, JsonProcessingException, TransformerException, PidCreationException {
    List<byte[]> handles = hf.genHandleList(requests.size());
    List<HandleAttribute> handleAttributes = new ArrayList<>();

    for (int i = 0; i < requests.size(); i++) {
      handleAttributes.addAll(
          prepareDigitalSpecimenRecordAttributes(requests.get(i), handles.get(i)));
    }
    var recordTimestamp = Instant.now();
    return handleRep.createDigitalSpecimenBatch(handles, recordTimestamp, handleAttributes);
  }

  public List<DigitalSpecimenBotanyResponse> createDigitalSpecimenBotanyBatch(
      List<DigitalSpecimenBotanyRequest> requests)
      throws PidResolutionException, ParserConfigurationException, JsonProcessingException, TransformerException, PidCreationException {
    List<byte[]> handles = hf.genHandleList(requests.size());

    List<HandleAttribute> handleAttributes = new ArrayList<>();

    for (int i = 0; i < requests.size(); i++) {
      handleAttributes.addAll(
          prepareDigitalSpecimenBotanyRecordAttributes(requests.get(i), handles.get(i)));
    }

    var recordTimestamp = Instant.now();
    return handleRep.createDigitalSpecimenBotanyBatch(handles, recordTimestamp, handleAttributes);

  }

  // Create Single Record
  public HandleRecordResponse createHandleRecord(HandleRecordRequest request)
      throws PidResolutionException, ParserConfigurationException, JsonProcessingException, TransformerException, PidCreationException {
    byte[] handle = hf.genHandleList(1).get(0);
    List<HandleAttribute> handleRecord = prepareHandleRecordAttributes(request, handle);
    var recordTimestamp = Instant.now();
    return handleRep.createHandle(handle, recordTimestamp, handleRecord);
  }

  public DoiRecordResponse createDoiRecord(DoiRecordRequest request)
      throws PidResolutionException, ParserConfigurationException, JsonProcessingException, TransformerException, PidCreationException {
    byte[] handle = hf.genHandleList(1).get(0);
    List<HandleAttribute> handleRecord = prepareDoiRecordAttributes(request, handle);
    var recordTimestamp = Instant.now();
    return handleRep.<DoiRecordResponse>createDoi(handle, recordTimestamp, handleRecord);
  }

  public DigitalSpecimenResponse createDigitalSpecimen(DigitalSpecimenRequest request)
      throws PidResolutionException, ParserConfigurationException, JsonProcessingException, TransformerException, PidCreationException {
    byte[] handle = hf.genHandleList(1).get(0);
    List<HandleAttribute> handleRecord = prepareDigitalSpecimenRecordAttributes(request, handle);
    var recordTimestamp = Instant.now();
    return handleRep.createDigitalSpecimen(handle, recordTimestamp, handleRecord);
  }

  public DigitalSpecimenBotanyResponse createDigitalSpecimenBotany(
      DigitalSpecimenBotanyRequest request)
      throws PidResolutionException, ParserConfigurationException, JsonProcessingException, TransformerException, PidCreationException {
    byte[] handle = hf.genHandleList(1).get(0);
    List<HandleAttribute> handleRecord = prepareDigitalSpecimenBotanyRecordAttributes(request,
        handle);
    var recordTimestamp = Instant.now();
    return handleRep.createDigitalSpecimenBotany(handle, recordTimestamp, handleRecord);
  }

  // Getters

  public List<String> getHandlesPaged(String pidStatus, int pageNum, int pageSize) {
    return handleRep.getAllHandles(pidStatus.getBytes(), pageNum, pageSize);
  }

  public List<String> getHandlesPaged(int pageNum, int pageSize) {
    return handleRep.getAllHandles(pageNum, pageSize);
  }

  // Prepare Attribute lists

  private List<HandleAttribute> prepareHandleRecordAttributes(HandleRecordRequest request,
      byte[] handle)
      throws PidResolutionException, JsonProcessingException, ParserConfigurationException, TransformerException {
    List<HandleAttribute> handleRecord = new ArrayList<>();

    // 100: Admin Handle
    handleRecord.add(new HandleAttribute(FIELD_IDX.get(HS_ADMIN), handle, HS_ADMIN, genAdminHandle()));

    // 1: Pid
    byte[] pid = ("https://hdl.handle.net/" + new String(handle)).getBytes();
    handleRecord.add(new HandleAttribute(FIELD_IDX.get(PID), handle, PID, pid));

    // 2: PidIssuer
    String pidIssuer = pidTypeService.resolveTypePid(request.getPidIssuerPid());
    handleRecord.add(new HandleAttribute(FIELD_IDX.get(PID_ISSUER), handle, PID_ISSUER, pidIssuer.getBytes()));

    // 3: Digital Object Type
    String digitalObjectType = pidTypeService.resolveTypePid(request.getDigitalObjectTypePid());
    handleRecord.add(
        new HandleAttribute(FIELD_IDX.get(DIGITAL_OBJECT_TYPE), handle, DIGITAL_OBJECT_TYPE, digitalObjectType.getBytes()));

    // 4: Digital Object Subtype
    String digitalObjectSubtype = pidTypeService.resolveTypePid(
        request.getDigitalObjectSubtypePid());
    handleRecord.add(
        new HandleAttribute(FIELD_IDX.get(DIGITAL_OBJECT_SUBTYPE), handle, DIGITAL_OBJECT_SUBTYPE, digitalObjectSubtype.getBytes()));

    // 5: 10320/loc
    byte[] loc = setLocations(request.getLocations());
    handleRecord.add(new HandleAttribute(FIELD_IDX.get(LOC), handle, LOC, loc));

    // 6: Issue Date
    handleRecord.add(new HandleAttribute(FIELD_IDX.get(ISSUE_DATE), handle, ISSUE_DATE, getDate().getBytes()));

    // 7: Issue number
    handleRecord.add(new HandleAttribute(FIELD_IDX.get(ISSUE_NUMBER), handle, ISSUE_NUMBER, "1".getBytes()));

    // 8: PidStatus
    handleRecord.add(new HandleAttribute(FIELD_IDX.get(PID_STATUS), handle, PID_STATUS, "TEST".getBytes()));

    // 9, 10: tombstone text, tombstone pids -> Skip

    // 11: PidKernelMetadataLicense:
    byte[] pidKernelMetadataLicense = "https://creativecommons.org/publicdomain/zero/1.0/".getBytes();
    handleRecord.add(
        new HandleAttribute(FIELD_IDX.get(PID_KERNEL_METADATA_LICENSE), handle, PID_KERNEL_METADATA_LICENSE, pidKernelMetadataLicense));

    return handleRecord;
  }


  private List<HandleAttribute> prepareDoiRecordAttributes(DoiRecordRequest request, byte[] handle)
      throws PidResolutionException, JsonProcessingException, ParserConfigurationException, TransformerException {
    List<HandleAttribute> handleRecord = prepareHandleRecordAttributes(request, handle);

    // 12: Referent DOI Name
    String referentDoiName = pidTypeService.resolveTypePid(request.getReferentDoiNamePid());

    handleRecord.add(
        new HandleAttribute(FIELD_IDX.get(REFERENT_DOI_NAME), handle, REFERENT_DOI_NAME, referentDoiName.getBytes()));

    // 13: Referent -> NOTE: Referent is blank currently until we have a model
    handleRecord.add(new HandleAttribute(FIELD_IDX.get(REFERENT), handle, REFERENT, request.getReferent().getBytes()));
    return handleRecord;
  }

  private List<HandleAttribute> prepareDigitalSpecimenRecordAttributes(
      DigitalSpecimenRequest request, byte[] handle)
      throws PidResolutionException, JsonProcessingException, ParserConfigurationException, TransformerException {
    List<HandleAttribute> handleRecord = prepareDoiRecordAttributes(request, handle);

    handleRecord.add(
        new HandleAttribute(FIELD_IDX.get(DIGITAL_OR_PHYSICAL), handle, DIGITAL_OR_PHYSICAL,
            request.getDigitalOrPhysical().getBytes()));

    // 15: specimenHost
    String specimenHost = pidTypeService.resolveTypePid(request.getSpecimenHostPid());
    handleRecord.add(new HandleAttribute(FIELD_IDX.get(SPECIMEN_HOST), handle, SPECIMEN_HOST, specimenHost.getBytes()));

    // 16: In collectionFacility
    String inCollectionFacility = pidTypeService.resolveTypePid(
        request.getInCollectionFacilityPid());
    handleRecord.add(
        new HandleAttribute(FIELD_IDX.get(IN_COLLECTION_FACILITY), handle, IN_COLLECTION_FACILITY, inCollectionFacility.getBytes()));

    return handleRecord;
  }

  private List<HandleAttribute> prepareDigitalSpecimenBotanyRecordAttributes(
      DigitalSpecimenBotanyRequest request,
      byte[] handle)
      throws PidResolutionException, JsonProcessingException, ParserConfigurationException, TransformerException {
    List<HandleAttribute> handleRecord = prepareDigitalSpecimenRecordAttributes(request, handle);

    // 17: ObjectType
    handleRecord.add(
        new HandleAttribute(FIELD_IDX.get(OBJECT_TYPE), handle, OBJECT_TYPE, request.getObjectType().getBytes()));

    // 18: preservedOrLiving
    handleRecord.add(
        new HandleAttribute(FIELD_IDX.get(PRESERVED_OR_LIVING), handle, PRESERVED_OR_LIVING,
            request.getPreservedOrLiving().getBytes()));

    return handleRecord;
  }

  private String getDate() {
    DateTimeFormatter dt = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH)
        .withZone(ZoneId.of("UTC"));
    Instant instant = Instant.now();
    return dt.format(instant);
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

  public void archiveHandleRecord(TombstoneRecordRequest request){
    byte[] handle = request.getHandle();
    List<HandleAttribute> record = new ArrayList<>();
    record.add(new HandleAttribute(FIELD_IDX.get(PID_STATUS), handle, PID_STATUS, "ARCHIVED".getBytes()));
    record.add(new HandleAttribute(FIELD_IDX.get(TOMBSTONE_TEXT), handle, TOMBSTONE_TEXT, request.getTombstoneText().getBytes()));

    if (request.getTombstonePids().length == 0) {
      record.add(new HandleAttribute(FIELD_IDX.get(TOMBSTONE_PIDS), handle, TOMBSTONE_PIDS, "".getBytes()));
    }



  }


}

