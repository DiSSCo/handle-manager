package eu.dissco.core.handlemanager.component;

import static eu.dissco.core.handlemanager.domain.PidRecords.DIGITAL_OBJECT_NAME;
import static eu.dissco.core.handlemanager.domain.PidRecords.DIGITAL_OBJECT_SUBTYPE;
import static eu.dissco.core.handlemanager.domain.PidRecords.DIGITAL_OBJECT_TYPE;
import static eu.dissco.core.handlemanager.domain.PidRecords.FDO_PROFILE;
import static eu.dissco.core.handlemanager.domain.PidRecords.FDO_RECORD_LICENSE;
import static eu.dissco.core.handlemanager.domain.PidRecords.ISSUED_FOR_AGENT;
import static eu.dissco.core.handlemanager.domain.PidRecords.ISSUED_FOR_AGENT_NAME;
import static eu.dissco.core.handlemanager.domain.PidRecords.MATERIAL_OR_DIGITAL_ENTITY;
import static eu.dissco.core.handlemanager.domain.PidRecords.FIELD_IDX;
import static eu.dissco.core.handlemanager.domain.PidRecords.HS_ADMIN;
import static eu.dissco.core.handlemanager.domain.PidRecords.IN_COLLECTION_FACILITY;
import static eu.dissco.core.handlemanager.domain.PidRecords.PID_ISSUER_NAME;
import static eu.dissco.core.handlemanager.domain.PidRecords.PID_RECORD_ISSUE_DATE;
import static eu.dissco.core.handlemanager.domain.PidRecords.PID_RECORD_ISSUE_NUMBER;
import static eu.dissco.core.handlemanager.domain.PidRecords.LOC;
import static eu.dissco.core.handlemanager.domain.PidRecords.LOC_REQ;
import static eu.dissco.core.handlemanager.domain.PidRecords.MEDIA_HASH;
import static eu.dissco.core.handlemanager.domain.PidRecords.MEDIA_URL;
import static eu.dissco.core.handlemanager.domain.PidRecords.NODE_ID;
import static eu.dissco.core.handlemanager.domain.PidRecords.OBJECT_TYPE;
import static eu.dissco.core.handlemanager.domain.PidRecords.PRIMARY_REFERENT_TYPE;
import static eu.dissco.core.handlemanager.domain.PidRecords.PRIMARY_SPECIMEN_OBJECT_ID;
import static eu.dissco.core.handlemanager.domain.PidRecords.PID;
import static eu.dissco.core.handlemanager.domain.PidRecords.PID_ISSUER;
import static eu.dissco.core.handlemanager.domain.PidRecords.PID_STATUS;
import static eu.dissco.core.handlemanager.domain.PidRecords.LIVING_OR_PRESERVED;
import static eu.dissco.core.handlemanager.domain.PidRecords.REFERENT;
import static eu.dissco.core.handlemanager.domain.PidRecords.REFERENT_DOI_NAME;
import static eu.dissco.core.handlemanager.domain.PidRecords.REFERENT_NAME;
import static eu.dissco.core.handlemanager.domain.PidRecords.REFERENT_TYPE;
import static eu.dissco.core.handlemanager.domain.PidRecords.SPECIMEN_HOST;
import static eu.dissco.core.handlemanager.domain.PidRecords.SUBJECT_PHYSICAL_IDENTIFIER;
import static eu.dissco.core.handlemanager.domain.PidRecords.SUBJECT_SPECIMEN_HOST;
import static eu.dissco.core.handlemanager.service.ServiceUtils.setUniquePhysicalIdentifierId;
import static eu.dissco.core.handlemanager.utils.AdminHandleGenerator.genAdminHandle;

import eu.dissco.core.handlemanager.domain.repsitoryobjects.HandleAttribute;
import eu.dissco.core.handlemanager.domain.requests.attributes.DigitalSpecimenBotanyRequest;
import eu.dissco.core.handlemanager.domain.requests.attributes.DigitalSpecimenRequest;
import eu.dissco.core.handlemanager.domain.requests.attributes.DoiRecordRequest;
import eu.dissco.core.handlemanager.domain.requests.attributes.HandleRecordRequest;
import eu.dissco.core.handlemanager.domain.requests.attributes.MediaObjectRequest;
import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
import eu.dissco.core.handlemanager.exceptions.PidServiceInternalError;
import eu.dissco.core.handlemanager.exceptions.UnprocessableEntityException;
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
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

@RequiredArgsConstructor
@Component
public class FdoRecordBuilder {

  private static final String DATACITE_ROR = "https://ror.org/04wxnsj81";
  private final TransformerFactory tf;
  private final DocumentBuilderFactory dbf;
  private final PidResolverComponent pidResolver;
  private final DateTimeFormatter dt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS",
      Locale.ENGLISH).withZone(ZoneId.of("UTC"));

  public List<HandleAttribute> prepareHandleRecordAttributes(HandleRecordRequest request,
      byte[] handle)
      throws PidServiceInternalError, UnprocessableEntityException, PidResolutionException {
    List<HandleAttribute> fdoRecord = new ArrayList<>();

    // 100: Admin Handle
    fdoRecord.add(
        new HandleAttribute(FIELD_IDX.get(HS_ADMIN), handle, HS_ADMIN, genAdminHandle()));

    byte[] loc = setLocations(request.getLocations(), new String(handle, StandardCharsets.UTF_8));
    fdoRecord.add(new HandleAttribute(FIELD_IDX.get(LOC), handle, LOC, loc));

    // 1: FDO Profile
    fdoRecord.add(new HandleAttribute(FIELD_IDX.get(FDO_PROFILE), handle, FDO_PROFILE,
        request.getFdoProfile().getBytes(StandardCharsets.UTF_8)));

    // 2: FDO Record License
    byte[] pidKernelMetadataLicense = "https://creativecommons.org/publicdomain/zero/1.0/".getBytes(
        StandardCharsets.UTF_8);
    fdoRecord.add(new HandleAttribute(FIELD_IDX.get(FDO_RECORD_LICENSE), handle,
        FDO_RECORD_LICENSE, pidKernelMetadataLicense));

    // 3: DigitalObjectType
    fdoRecord.add(
        new HandleAttribute(FIELD_IDX.get(DIGITAL_OBJECT_TYPE), handle, DIGITAL_OBJECT_TYPE,
            request.getDigitalObjectTypePid().getBytes(StandardCharsets.UTF_8)));

    // 4: DigitalObjectName
    var digitalObjectName = pidResolver.getObjectName(request.getDigitalObjectTypePid())
        .getBytes(StandardCharsets.UTF_8);
    fdoRecord.add(
        new HandleAttribute(FIELD_IDX.get(DIGITAL_OBJECT_NAME), handle, DIGITAL_OBJECT_NAME,
            digitalObjectName));

    // 5: Pid
    byte[] pid = ("https://hdl.handle.net/" + new String(handle, StandardCharsets.UTF_8)).getBytes(
        StandardCharsets.UTF_8);
    fdoRecord.add(new HandleAttribute(FIELD_IDX.get(PID), handle, PID, pid));

    // 6: PidIssuer
    var pidIssuer = setPidIssuer(request).getBytes(StandardCharsets.UTF_8);
    fdoRecord.add(new HandleAttribute(FIELD_IDX.get(PID_ISSUER), handle, PID_ISSUER,
        pidIssuer));

    // 7: pidIssuerName
    var pidIssuerName = setPidIssuerName(request).getBytes(StandardCharsets.UTF_8);
    fdoRecord.add(new HandleAttribute(FIELD_IDX.get(PID_ISSUER_NAME), handle, PID_ISSUER_NAME,
        pidIssuerName));

    // 8: issuedForAgent
    fdoRecord.add(new HandleAttribute(FIELD_IDX.get(ISSUED_FOR_AGENT), handle, ISSUED_FOR_AGENT,
        request.getIssuedForAgent().getBytes(StandardCharsets.UTF_8)));

    // 9: issuedForAgentName
    var issuedForAgentName = pidResolver.getObjectName(request.getIssuedForAgent())
        .getBytes(StandardCharsets.UTF_8);
    fdoRecord.add(
        new HandleAttribute(FIELD_IDX.get(ISSUED_FOR_AGENT_NAME), handle, ISSUED_FOR_AGENT_NAME,
            issuedForAgentName));

    // 10: pidRecordIssueDate
    fdoRecord.add(new HandleAttribute(FIELD_IDX.get(PID_RECORD_ISSUE_DATE), handle,
        PID_RECORD_ISSUE_DATE, getDate().getBytes(StandardCharsets.UTF_8)));

    // 11: pidRecordIssueNumber
    fdoRecord.add(new HandleAttribute(FIELD_IDX.get(PID_RECORD_ISSUE_NUMBER), handle,
        PID_RECORD_ISSUE_NUMBER, "1".getBytes(StandardCharsets.UTF_8)));

    // 12: structuralType
    fdoRecord.add(new HandleAttribute(FIELD_IDX.get(PID_RECORD_ISSUE_NUMBER), handle,
        PID_RECORD_ISSUE_NUMBER, "1".getBytes(StandardCharsets.UTF_8)));

    // 13: PidStatus
    fdoRecord.add(new HandleAttribute(FIELD_IDX.get(PID_STATUS), handle, PID_STATUS,
        "TEST".getBytes(StandardCharsets.UTF_8)));

    return fdoRecord;
  }

  private String setPidIssuer(HandleRecordRequest request) {
    return request.getPidIssuer() == null ? DATACITE_ROR : request.getPidIssuer();
  }

  private String setPidIssuerName(HandleRecordRequest request)
      throws UnprocessableEntityException, PidResolutionException {
    return request.getPidIssuer() == null ? DATACITE_ROR : pidResolver.getObjectName(
        request.getPidIssuer());
  }


  public List<HandleAttribute> prepareDoiRecordAttributes(DoiRecordRequest request, byte[] handle)
      throws PidServiceInternalError, UnprocessableEntityException, PidResolutionException {
    var fdoRecord = prepareHandleRecordAttributes(request, handle);

    // 40: referentType
    fdoRecord.add(
        new HandleAttribute(FIELD_IDX.get(REFERENT_TYPE), handle, REFERENT_TYPE, request.getReferentType().getBytes(StandardCharsets.UTF_8)));

    // 41: referentDoiName
    fdoRecord.add(
        new HandleAttribute(FIELD_IDX.get(REFERENT_DOI_NAME), handle, REFERENT_DOI_NAME, handle));

    // 42: referentName
    fdoRecord.add(
        new HandleAttribute(FIELD_IDX.get(REFERENT_NAME), handle, REFERENT_NAME, request.getReferentName().getBytes(StandardCharsets.UTF_8)));

    // 43: primaryReferentType
    fdoRecord.add(
        new HandleAttribute(FIELD_IDX.get(PRIMARY_REFERENT_TYPE), handle, PRIMARY_REFERENT_TYPE, request.getPrimaryReferentType().getBytes(StandardCharsets.UTF_8)));

    // 44: referent
    fdoRecord.add(new HandleAttribute(FIELD_IDX.get(REFERENT), handle, REFERENT,
        request.getReferent().getBytes(StandardCharsets.UTF_8)));

    return fdoRecord;
  }

  public List<HandleAttribute> prepareMediaObjectAttributes(MediaObjectRequest request,
      byte[] handle)
      throws PidServiceInternalError, UnprocessableEntityException, PidResolutionException {
    var fdoRecord = prepareDoiRecordAttributes(request, handle);

    // 14 Media Hash
    fdoRecord.add(new HandleAttribute(FIELD_IDX.get(MEDIA_HASH), handle, MEDIA_HASH,
        request.getMediaHash().getBytes(StandardCharsets.UTF_8)));

    // 15 Subject Specimen Host
    fdoRecord.add(
        new HandleAttribute(FIELD_IDX.get(SUBJECT_SPECIMEN_HOST), handle, SUBJECT_SPECIMEN_HOST,
            request.getSubjectSpecimenHostPid().getBytes(StandardCharsets.UTF_8)));

    // 16 Media Url
    fdoRecord.add(new HandleAttribute(FIELD_IDX.get(MEDIA_URL), handle, MEDIA_URL,
        request.getMediaUrl().getBytes(StandardCharsets.UTF_8)));

    // 17 : Subject Physical Identifier
    // Encoding here is UTF-8
    fdoRecord.add(new HandleAttribute(FIELD_IDX.get(SUBJECT_PHYSICAL_IDENTIFIER), handle,
        SUBJECT_PHYSICAL_IDENTIFIER, setUniquePhysicalIdentifierId(request)));
    return fdoRecord;
  }

  public List<HandleAttribute> prepareDigitalSpecimenRecordAttributes(
      DigitalSpecimenRequest request, byte[] handle)
      throws PidServiceInternalError, UnprocessableEntityException, PidResolutionException {
    var fdoRecord = prepareDoiRecordAttributes(request, handle);

    // 17 : Institutional Identifier
    // Encoding here is UTF-8
    fdoRecord.add(
        new HandleAttribute(FIELD_IDX.get(PRIMARY_SPECIMEN_OBJECT_ID), handle,
            PRIMARY_SPECIMEN_OBJECT_ID,
            setUniquePhysicalIdentifierId(request)));

    return fdoRecord;
  }

  public List<HandleAttribute> prepareDigitalSpecimenBotanyRecordAttributes(
      DigitalSpecimenBotanyRequest request, byte[] handle)
      throws PidServiceInternalError, UnprocessableEntityException, PidResolutionException {
    List<HandleAttribute> fdoRecord = prepareDigitalSpecimenRecordAttributes(request, handle);

    // 17: ObjectType
    fdoRecord.add(new HandleAttribute(FIELD_IDX.get(OBJECT_TYPE), handle, OBJECT_TYPE,
        request.getObjectType().getBytes(StandardCharsets.UTF_8)));

    // 18: preservedOrLiving
    fdoRecord.add(
        new HandleAttribute(FIELD_IDX.get(LIVING_OR_PRESERVED), handle, LIVING_OR_PRESERVED,
            request.getPreservedOrLiving().getBytes()));

    return fdoRecord;
  }

  private String getDate() {
    return dt.format(Instant.now());
  }

  public byte[] setLocations(String[] userLocations, String handle) throws PidServiceInternalError {

    DocumentBuilder documentBuilder;
    try {
      documentBuilder = dbf.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      throw new PidServiceInternalError(e.getMessage(), e);
    }

    var doc = documentBuilder.newDocument();
    var locations = doc.createElement(LOC_REQ);
    doc.appendChild(locations);
    String[] objectLocations = concatLocations(userLocations, handle);

    for (int i = 0; i < objectLocations.length; i++) {
      var locs = doc.createElement("location");
      locs.setAttribute(NODE_ID, String.valueOf(i));
      locs.setAttribute("href", objectLocations[i]);
      String weight = i < 1 ? "1" : "0";
      locs.setAttribute("weight", weight);
      locations.appendChild(locs);
    }
    try {
      return documentToString(doc).getBytes(StandardCharsets.UTF_8);
    } catch (TransformerException e) {
      throw new PidServiceInternalError("An internal error has occurred parsing location data", e);
    }
  }

  private String[] concatLocations(String[] userLocations, String handle) {
    ArrayList<String> objectLocations = new ArrayList<>(List.of(defaultLocations(handle)));
    if (userLocations != null) {
      objectLocations.addAll(List.of(userLocations));
    }
    return objectLocations.toArray(new String[0]);
  }

  private String[] defaultLocations(String handle) {
    String api = "https://sandbox.dissco.tech/api/v1/specimens/" + handle;
    String ui = "https://sandbox.dissco.tech/ds/" + handle;
    return new String[]{api, ui};
  }

  private String documentToString(Document document) throws TransformerException {
    var transformer = tf.newTransformer();
    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
    StringWriter writer = new StringWriter();
    transformer.transform(new DOMSource(document), new StreamResult(writer));
    return writer.getBuffer().toString();
  }
}
