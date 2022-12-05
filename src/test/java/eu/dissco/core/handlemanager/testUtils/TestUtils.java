package eu.dissco.core.handlemanager.testUtils;

import static eu.dissco.core.handlemanager.domain.PidRecords.*;
import static eu.dissco.core.handlemanager.utils.Resources.genAdminHandle;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.HandleAttribute;
import eu.dissco.core.handlemanager.domain.requests.DigitalSpecimenBotanyRequest;
import eu.dissco.core.handlemanager.domain.requests.DigitalSpecimenRequest;
import eu.dissco.core.handlemanager.domain.requests.DoiRecordRequest;
import eu.dissco.core.handlemanager.domain.requests.HandleRecordRequest;
import eu.dissco.core.handlemanager.domain.responses.DigitalSpecimenBotanyResponse;
import eu.dissco.core.handlemanager.domain.responses.DigitalSpecimenResponse;
import eu.dissco.core.handlemanager.domain.responses.DoiRecordResponse;
import eu.dissco.core.handlemanager.domain.responses.HandleRecordResponse;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;

@Slf4j
public class TestUtils {

  public static final Instant CREATED = Instant.parse("2022-11-01T09:59:24.00Z");
  public static final String ISSUE_DATE_TESTVAL = "2022-11-01";

  public static String HANDLE = "20.5000.1025/QRS-321-ABC";
  public static String HANDLE_ALT = "20.5000.1025/ABC-123-QRS";

  // Request Vars
  // Handles

  public static String PID_ISSUER_PID = "20.5000.1025/PID-ISSUER";
  public static String DIGITAL_OBJECT_TYPE_PID = "20.5000.1025/DIGITAL-SPECIMEN";
  public static String DIGITAL_OBJECT_SUBTYPE_PID = "20.5000.1025/BOTANY-SPECIMEN";
  public static String[] LOC_TESTVAL = {"https://sandbox.dissco.tech/", "https://dissco.eu"};
  public static final String PID_STATUS_TESTVAL = "TEST";
  public static final String PID_KERNEL_METADATA_LICENSE_TESTVAL = "https://creativecommons.org/publicdomain/zero/1.0/";
  //DOIs
  public static String REFERENT_DOI_NAME_PID = "20.5000.1025/OTHER-TRIPLET";
  public static String REFERENT_TESTVAL = "";
  //Digital Specimens
  public static String DIGITAL_OR_PHYSICAL_TESTVAL = "physical";
  public static String SPECIMEN_HOST_PID = "20.5000.1025/OTHER-TRIPLET";
  public static String IN_COLLECTION_FACILITY_TESTVAL = "20.5000.1025/OTHER-TRIPLET";

  //Botany Specimens
  public static String OBJECT_TYPE_TESTVAL = "Herbarium Sheet";
  public static String PRESERVED_OR_LIVING_TESTVAL = "preserved";

  // Pid Type Record vars
  public static String PTR_PID = "http://hdl.handle.net/" + PID_ISSUER_PID;
  public static String PTR_TYPE = "handle";
  public static String PTR_PRIMARY_NAME = "DiSSCo";
  public static String PTR_PID_DOI = "http://doi.org/" + PID_ISSUER_PID;
  public static String PTR_TYPE_DOI = "doi";
  public static String PTR_REGISTRATION_DOI_NAME = "Registration Agency";
  public static String PTR_HANDLE_RECORD = initPtrHandleRecord(false);
  public static String PTR_DOI_RECORD = initPtrHandleRecord(true);

  private TestUtils() {
    throw new IllegalStateException("Utility class");
  }

  // Pid Type Records
  private static String initPtrHandleRecord(boolean isDoi) {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode objectNode = mapper.createObjectNode();
    if (isDoi) {
      objectNode.put("pid", PTR_PID_DOI);
      objectNode.put("pidType", PTR_TYPE_DOI);
      objectNode.put("primaryNamefromPid", PTR_PRIMARY_NAME);
      objectNode.put("registrationAgencyDoiName", PTR_REGISTRATION_DOI_NAME);
    } else {
      objectNode.put("pid", PTR_PID);
      objectNode.put("pidType", PTR_TYPE);
      objectNode.put("primaryNamefromPid", PTR_PRIMARY_NAME);
    }
    try {
      return mapper.writeValueAsString(objectNode);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      return "";
    }
  }

  // Handle Attribute Lists
  public static List<HandleAttribute> generateTestHandleAttributes(byte[] handle) {

    List<HandleAttribute> handleRecord = new ArrayList<>();
    byte[] ptr_record = PTR_HANDLE_RECORD.getBytes();

    // 100: Admin Handle
    handleRecord.add(new HandleAttribute(100, handle, HS_ADMIN, genAdminHandle()));

    // 1: Pid
    byte[] pid = ("https://hdl.handle.net/" + new String(handle)).getBytes();
    handleRecord.add(new HandleAttribute(1, handle, PID, pid));

    // 2: PidIssuer
    handleRecord.add(new HandleAttribute(2, handle, PID_ISSUER, ptr_record));

    // 3: Digital Object Type
    handleRecord.add(new HandleAttribute(3, handle, DIGITAL_OBJECT_TYPE, ptr_record));

    // 4: Digital Object Subtype
    handleRecord.add(new HandleAttribute(4, handle, DIGITAL_OBJECT_SUBTYPE, ptr_record));

    // 5: 10320/loc
    byte[] loc = "".getBytes();
    try {
      loc = setLocations(LOC_TESTVAL);
    } catch (TransformerException | ParserConfigurationException e) {
      e.printStackTrace();
    }
    handleRecord.add(new HandleAttribute(5, handle, LOC, loc));

    // 6: Issue Date
    handleRecord.add(new HandleAttribute(6, handle, ISSUE_DATE, ISSUE_DATE_TESTVAL.getBytes()));

    // 7: Issue number
    handleRecord.add(new HandleAttribute(7, handle, ISSUE_NUMBER, "1".getBytes()));

    // 8: PidStatus
    handleRecord.add(new HandleAttribute(8, handle, PID_STATUS, PID_STATUS_TESTVAL.getBytes()));

    // 9, 10: tombstone text, tombstone pids -> Skip

    // 11: PidKernelMetadataLicense:
    handleRecord.add(
        new HandleAttribute(11, handle, PID_KERNEL_METADATA_LICENSE, PID_KERNEL_METADATA_LICENSE_TESTVAL.getBytes()));

    return handleRecord;
  }

  public static List<HandleAttribute> generateTestDoiAttributes(byte[] handle) {
    List<HandleAttribute> handleRecord = generateTestHandleAttributes(handle);
    byte[] ptr_record = PTR_HANDLE_RECORD.getBytes();

    // 12: Referent DOI Name
    handleRecord.add(new HandleAttribute(12, handle, REFERENT_DOI_NAME, ptr_record));
    // 13: Referent
    handleRecord.add(new HandleAttribute(13, handle, REFERENT, REFERENT_TESTVAL.getBytes()));
    return handleRecord;
  }

  public static List<HandleAttribute> generateTestDigitalSpecimenAttributes(byte[] handle) {
    List<HandleAttribute> handleRecord = generateTestDoiAttributes(handle);
    byte[] ptr_record = PTR_HANDLE_RECORD.getBytes();

    // 14: digitalOrPhysical
    handleRecord.add(
        new HandleAttribute(14, handle, DIGITAL_OR_PHYSICAL, DIGITAL_OR_PHYSICAL_TESTVAL.getBytes()));

    // 15: specimenHost
    handleRecord.add(new HandleAttribute(15, handle, SPECIMEN_HOST, ptr_record));

    // 16: In collectionFacility
    handleRecord.add(
        new HandleAttribute(16, handle, IN_COLLECTION_FACILITY, ptr_record));
    return handleRecord;
  }

  public static List<HandleAttribute> generateTestDigitalSpecimenBotanyAttributes(byte[] handle) {
    List<HandleAttribute> handleRecord = generateTestDigitalSpecimenAttributes(handle);

    // 17: ObjectType
    handleRecord.add(new HandleAttribute(17, handle, OBJECT_TYPE, OBJECT_TYPE_TESTVAL.getBytes()));

    // 18: preservedOrLiving
    handleRecord.add(
        new HandleAttribute(18, handle, PRESERVED_OR_LIVING, PRESERVED_OR_LIVING_TESTVAL.getBytes()));
    return handleRecord;
  }

  // Requests

  public static HandleRecordRequest generateTestHandleRequest() {
    return new HandleRecordRequest(
        PID_ISSUER_PID,
        DIGITAL_OBJECT_TYPE_PID,
        DIGITAL_OBJECT_SUBTYPE_PID,
        LOC_TESTVAL);
  }


  public static DoiRecordRequest generateTestDoiRequest() {
    return new DoiRecordRequest(
        PID_ISSUER_PID,
        DIGITAL_OBJECT_TYPE_PID,
        DIGITAL_OBJECT_SUBTYPE_PID,
        LOC_TESTVAL,
        REFERENT_DOI_NAME_PID);
  }


  public static DigitalSpecimenRequest generateTestDigitalSpecimenRequest() {
    return new DigitalSpecimenRequest(
        PID_ISSUER_PID,
        DIGITAL_OBJECT_TYPE_PID,
        DIGITAL_OBJECT_SUBTYPE_PID,
        LOC_TESTVAL,
        REFERENT_DOI_NAME_PID,
        DIGITAL_OR_PHYSICAL_TESTVAL,
        SPECIMEN_HOST_PID,
        IN_COLLECTION_FACILITY_TESTVAL);
  }

  public static DigitalSpecimenBotanyRequest generateTestDigitalSpecimenBotanyRequest() {
    return new DigitalSpecimenBotanyRequest(PID_ISSUER_PID,
        DIGITAL_OBJECT_TYPE_PID,
        DIGITAL_OBJECT_SUBTYPE_PID,
        LOC_TESTVAL,
        REFERENT_DOI_NAME_PID,
        DIGITAL_OR_PHYSICAL_TESTVAL,
        SPECIMEN_HOST_PID,
        IN_COLLECTION_FACILITY_TESTVAL,
        OBJECT_TYPE_TESTVAL,
        PRESERVED_OR_LIVING_TESTVAL);
  }

  // Responses
  public static HandleRecordResponse generateTestHandleResponse(byte[] handle) {
    String pid = "https://hdl.handle.net/" + new String(handle);
    String locs = getLocString();

    String admin = new String(genAdminHandle());

    return new HandleRecordResponse(
        pid,                  // Pid
        PTR_HANDLE_RECORD,    // pidIssuer
        PTR_HANDLE_RECORD,    // digitalObjectType
        PTR_HANDLE_RECORD,    // digitalObjectSubtype
        locs,                 // 10320/loc
        ISSUE_DATE_TESTVAL,           // issueDate
        "1",                  // issueNumber
        PID_STATUS_TESTVAL,           // pidStatus
        PID_KERNEL_METADATA_LICENSE_TESTVAL,              // Pid Kernel Metadata License
        admin
    );
  }

  public static DoiRecordResponse generateTestDoiResponse(byte[] handle) {
    String pid = "https://hdl.handle.net/" + new String(handle);
    String locs = getLocString();

    String admin = new String(genAdminHandle());

    return new DoiRecordResponse(
        pid,                  // Pid
        PTR_HANDLE_RECORD,    // pidIssuer
        PTR_HANDLE_RECORD,    // digitalObjectType
        PTR_HANDLE_RECORD,    // digitalObjectSubtype
        locs,                 // 10320/loc
        ISSUE_DATE_TESTVAL,           // issueDate
        "1",                  // issueNumber
        PID_STATUS_TESTVAL,           // pidStatus
        PID_KERNEL_METADATA_LICENSE_TESTVAL,              // Pid Kernel Metadata License
        admin,
        PTR_HANDLE_RECORD,
        REFERENT_TESTVAL
    );
  }

  public static DigitalSpecimenResponse generateTestDigitalSpecimenResponse(byte[] handle) {
    String pid = "https://hdl.handle.net/" + new String(handle);
    String locs = getLocString();

    String admin = new String(genAdminHandle());

    return new DigitalSpecimenResponse(
        pid,                  // Pid
        PTR_HANDLE_RECORD,    // pidIssuer
        PTR_HANDLE_RECORD,    // digitalObjectType
        PTR_HANDLE_RECORD,    // digitalObjectSubtype
        locs,                 // 10320/loc
        ISSUE_DATE_TESTVAL,           // issueDate
        "1",                  // issueNumber
        PID_STATUS_TESTVAL,           // pidStatus
        PID_KERNEL_METADATA_LICENSE_TESTVAL,              // Pid Kernel Metadata License
        admin,
        PTR_HANDLE_RECORD,
        REFERENT_TESTVAL,
        DIGITAL_OR_PHYSICAL_TESTVAL,
        PTR_HANDLE_RECORD,
        PTR_HANDLE_RECORD
    );
  }

  public static DigitalSpecimenBotanyResponse generateTestDigitalSpecimenBotanyResponse(
      byte[] handle) {
    String pid = "https://hdl.handle.net/" + new String(handle);
    String locs = getLocString();

    String admin = new String(genAdminHandle());

    return new DigitalSpecimenBotanyResponse(
        pid,                  // Pid
        PTR_HANDLE_RECORD,    // pidIssuer
        PTR_HANDLE_RECORD,    // digitalObjectType
        PTR_HANDLE_RECORD,    // digitalObjectSubtype
        locs,                 // 10320/loc
        ISSUE_DATE_TESTVAL,           // issueDate
        "1",                  // issueNumber
        PID_STATUS_TESTVAL,           // pidStatus
        PID_KERNEL_METADATA_LICENSE_TESTVAL,              // Pid Kernel Metadata License
        admin,
        PTR_HANDLE_RECORD,
        REFERENT_TESTVAL,
        DIGITAL_OR_PHYSICAL_TESTVAL,
        PTR_HANDLE_RECORD,
        PTR_HANDLE_RECORD,
        OBJECT_TYPE_TESTVAL,
        PRESERVED_OR_LIVING_TESTVAL
    );
  }

  public static long initTime() {
    return CREATED.getEpochSecond();
  }

  private static String getLocString() {
    byte[] loc = "".getBytes();
    try {
      loc = setLocations(LOC_TESTVAL);
    } catch (TransformerException | ParserConfigurationException e) {
      e.printStackTrace();
    }
    return new String(loc);
  }

  public static byte[] setLocations(String[] objectLocations)
      throws TransformerException, ParserConfigurationException {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);

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

  private static String documentToString(Document document) throws TransformerException {
    TransformerFactory tf = TransformerFactory.newInstance();
    tf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
    tf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
    tf.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");

    var transformer = tf.newTransformer();
    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
    StringWriter writer = new StringWriter();
    transformer.transform(new DOMSource(document), new StreamResult(writer));
    return writer.getBuffer().toString();
  }

}
