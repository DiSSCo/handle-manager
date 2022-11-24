package eu.dissco.core.handlemanager.testUtils;

import static eu.dissco.core.handlemanager.utils.Resources.genAdminHandle;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.dissco.core.handlemanager.domain.pidrecords.HandleAttribute;
import eu.dissco.core.handlemanager.domain.requests.DigitalSpecimenBotanyRequest;
import eu.dissco.core.handlemanager.domain.requests.DigitalSpecimenRequest;
import eu.dissco.core.handlemanager.domain.requests.DoiRecordRequest;
import eu.dissco.core.handlemanager.domain.requests.HandleRecordRequest;
import eu.dissco.core.handlemanager.domain.responses.DigitalSpecimenBotanyResponse;
import eu.dissco.core.handlemanager.domain.responses.DigitalSpecimenResponse;
import eu.dissco.core.handlemanager.domain.responses.DoiRecordResponse;
import eu.dissco.core.handlemanager.domain.responses.HandleRecordResponse;
import eu.dissco.core.handlemanager.jparepository.Handles;
import eu.dissco.core.handlemanager.utils.Resources;
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

  public static String HANDLE = "20.5000.1025/QRS-321-ABC";
  public static String HANDLE_ALT = "20.5000.1025/ABC-123-QRS";

  // Request Vars
  // Handles
  public static String PID_ISSUER_PID = "20.5000.1025/PID-ISSUER";
  public static String DIGITAL_OBJECT_TYPE_PID = "20.5000.1025/DIGITAL-SPECIMEN";
  public static String DIGITAL_OBJECT_SUBTYPE_PID = "20.5000.1025/BOTANY-SPECIMEN";
  public static String[] LOCATIONS = {"https://sandbox.dissco.tech/", "https://dissco.eu"};
  //DOIs
  public static String REFERENT_DOI_NAME_PID = "20.5000.1025/OTHER-TRIPLET";
  public static String REFERENT = "";
  //Digital Specimens
  public static String DIGITAL_OR_PHYSICAL = "physical";
  public static String SPECIMEN_HOST_PID = "20.5000.1025/OTHER-TRIPLET";
  public static String IN_COLLECTION_FACILITY = "20.5000.1025/OTHER-TRIPLET";
  //Botany Specimens
  public static String OBJECT_TYPE = "Herbarium Sheet";
  public static String PRESERVED_OR_LIVING = "preserved";

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

  // Jooq methods
  public static List<HandleAttribute> generateTestHandleAttributes(byte[] handle) {

    List<HandleAttribute> handleRecord= new ArrayList<>();
    byte [] ptr_record = PTR_HANDLE_RECORD.getBytes();

    // 100: Admin Handle
    handleRecord.add(new HandleAttribute(100, "HS_ADMIN", genAdminHandle()));

    // 1: Pid
    byte[] pid = ("https://hdl.handle.net/" + new String(handle)).getBytes();
    handleRecord.add(new HandleAttribute(1, "pid", pid));

    // 2: PidIssuer
    handleRecord.add(new HandleAttribute(2, "pidIssuer", ptr_record));

    // 3: Digital Object Type
    handleRecord.add(new HandleAttribute(3, "digitalObjectType", ptr_record));

    // 4: Digital Object Subtype
    handleRecord.add(new HandleAttribute(4, "digitalObjectSubtype",ptr_record));

    // 5: 10320/loc
    byte[] loc = "".getBytes();
    try {
      loc = setLocations(LOCATIONS);
    } catch (TransformerException | ParserConfigurationException e) {
      e.printStackTrace();
    }
    handleRecord.add(new HandleAttribute(5, "10320/loc", loc));

    // 6: Issue Date
    handleRecord.add(new HandleAttribute(6, "issueDate",  "2022-11-01".getBytes()));

    // 7: Issue number
    handleRecord.add(new HandleAttribute(7, "issueNumber",  "1".getBytes()));

    // 8: PidStatus
    handleRecord.add(new HandleAttribute(8, "pidStatus",  "TEST".getBytes()));

    // 9, 10: tombstone text, tombstone pids -> Skip

    // 11: PidKernelMetadataLicense:
    byte[] pidKernelMetadataLicense = "https://creativecommons.org/publicdomain/zero/1.0/".getBytes();
    handleRecord.add(new HandleAttribute(11, "pidKernelMetadataLicense",  pidKernelMetadataLicense));

    return handleRecord;
  }

  public static List<HandleAttribute> generateTestDoiAttributes(byte[] handle) {
    List<HandleAttribute> handleRecord = generateTestHandleAttributes(handle);
    byte[] ptr_record = PTR_HANDLE_RECORD.getBytes();

    // 12: Referent DOI Name
    handleRecord.add(new HandleAttribute(12, "referentDoiName", ptr_record));
    // 13: Referent
    // it
    handleRecord.add(new HandleAttribute(13, "referent", REFERENT.getBytes()));
    return handleRecord;
  }

  public static List<HandleAttribute> generateTestDigitalSpecimenAttributes(byte[] handle) {
    List<HandleAttribute> handleRecord = generateTestDoiAttributes(handle);
    byte[] ptr_record = PTR_HANDLE_RECORD.getBytes();

    // 14: digitalOrPhysical
    handleRecord.add(new HandleAttribute(14, "digitalOrPhysical", DIGITAL_OR_PHYSICAL.getBytes()));

    // 15: specimenHost
    handleRecord.add(new HandleAttribute(15, "specimenHost", ptr_record));

    // 16: In collectionFacility
    handleRecord.add(
        new HandleAttribute(16, "inCollectionFacility", ptr_record));
    return handleRecord;
  }

  public static List<HandleAttribute> generateTestDigitalSpecimenBotanyAttributes(byte[] handle) {
    List<HandleAttribute> handleRecord = generateTestDigitalSpecimenAttributes(handle);

    // 17: ObjectType
    handleRecord.add(new HandleAttribute(17, "objectType", OBJECT_TYPE.getBytes()));

    // 18: preservedOrLiving
    handleRecord.add(new HandleAttribute( 18, "preservedOrLiving", PRESERVED_OR_LIVING.getBytes()));
    return handleRecord;
  }

  // JPA Methods

  public static List<Handles> generateTestHandleRecord(byte[] handle) {

    List<Handles> handleRecord = new ArrayList<Handles>();
    long timestamp = initTime();

    // 100: Admin Handle
    handleRecord.add(Resources.genAdminHandle(handle, timestamp));

    // 1: Pid
    handleRecord.add(
        new Handles(handle, 1, "pid", ("https://hdl.handle.net/" + new String(handle)).getBytes(),
            timestamp));

    // 2: PidIssuer
    handleRecord.add(new Handles(handle, 2, "pidIssuer", PTR_HANDLE_RECORD, timestamp));

    // 3: Digital Object Type
    handleRecord.add(new Handles(handle, 3, "digitalObjectType", PTR_HANDLE_RECORD, timestamp));

    // 4: Digital Object Subtype
    handleRecord.add(
        new Handles(handle, 4, "digitalObjectSubtype", PTR_HANDLE_RECORD, timestamp));

    // 5: 10320/loc
    byte[] loc = "".getBytes();
    try {
      loc = setLocations(LOCATIONS);
    } catch (TransformerException | ParserConfigurationException e) {
      e.printStackTrace();
    }
    handleRecord.add(new Handles(handle, 5, "10320/loc", loc, timestamp));

    // 6: Issue Date
    handleRecord.add((new Handles(handle, 6, "issueDate", "2022-11-01", timestamp)));

    // 7: Issue number
    handleRecord.add((new Handles(handle, 7, "issueNumber", "1", timestamp)));
    // have a 1 for the issue date?

    // 8: PidStatus
    handleRecord.add((new Handles(handle, 8, "pidStatus", "TEST", timestamp)));

    // 9, 10: tombstone text, tombstone pids -> Skip

    // 11: PidKernelMetadataLicense:
    // https://creativecommons.org/publicdomain/zero/1.0/
    handleRecord.add((new Handles(handle, 11, "pidKernelMetadataLicense",
        "https://creativecommons.org/publicdomain/zero/1.0/", timestamp)));
    return handleRecord;
  }

  public static List<Handles> generateTestDoiRecord(byte[] handle) {
    List<Handles> handleRecord = generateTestHandleRecord(handle);
    long timestamp = initTime();

    // 12: Referent DOI Name
    handleRecord.add(new Handles(handle, 12, "referentDoiName", PTR_HANDLE_RECORD, timestamp));
    // 13: Referent
    // it
    handleRecord.add(new Handles(handle, 13, "referent", REFERENT, timestamp));
    return handleRecord;
  }

  public static List<Handles> generateTestDigitalSpecimenRecord(byte[] handle) {
    List<Handles> handleRecord = generateTestDoiRecord(handle);
    long timestamp = initTime();

    // 14: digitalOrPhysical
    handleRecord.add(new Handles(handle, 14, "digitalOrPhysical", DIGITAL_OR_PHYSICAL, timestamp));

    // 15: specimenHost
    handleRecord.add(new Handles(handle, 15, "specimenHost", PTR_HANDLE_RECORD, timestamp));

    // 16: In collectionFacility
    handleRecord.add(
        new Handles(handle, 16, "inCollectionFacility", PTR_HANDLE_RECORD, timestamp));
    return handleRecord;
  }


  public static List<Handles> generateTestDigitalSpecimenBotanyRecord(byte[] handle) {
    List<Handles> handleRecord = generateTestDigitalSpecimenRecord(handle);
    long timestamp = initTime();

    // 17: ObjectType
    handleRecord.add(new Handles(handle, 17, "objectType", OBJECT_TYPE, timestamp));

    // 18: preservedOrLiving
    handleRecord.add(new Handles(handle, 18, "preservedOrLiving", PRESERVED_OR_LIVING, timestamp));

    return handleRecord;
  }


  public static HandleRecordRequest generateTestHandleRequest() {
    return new HandleRecordRequest(
        PID_ISSUER_PID,
        DIGITAL_OBJECT_TYPE_PID,
        DIGITAL_OBJECT_SUBTYPE_PID,
        LOCATIONS);
  }

  public static HandleRecordResponse generateTestHandleResponse(byte[] handle) {

    return new HandleRecordResponse(generateTestHandleRecord(handle));
  }

  public static DoiRecordRequest generateTestDoiRequest() {
    return new DoiRecordRequest(
        PID_ISSUER_PID,
        DIGITAL_OBJECT_TYPE_PID,
        DIGITAL_OBJECT_SUBTYPE_PID,
        LOCATIONS,
        REFERENT_DOI_NAME_PID);
  }

  public static DoiRecordResponse generateTestDoiResponse(byte[] handle) {
    return new DoiRecordResponse(generateTestDoiRecord(handle));
  }

  public static DigitalSpecimenRequest generateTestDigitalSpecimenRequest() {
    return new DigitalSpecimenRequest(
        PID_ISSUER_PID,
        DIGITAL_OBJECT_TYPE_PID,
        DIGITAL_OBJECT_SUBTYPE_PID,
        LOCATIONS,
        REFERENT_DOI_NAME_PID,
        DIGITAL_OR_PHYSICAL,
        SPECIMEN_HOST_PID,
        IN_COLLECTION_FACILITY);
  }

  public static DigitalSpecimenResponse generateTestDigitalSpecimenResponse(byte[] handle) {
    return new DigitalSpecimenResponse(generateTestDigitalSpecimenRecord(handle));
  }


  public static DigitalSpecimenBotanyRequest generateTestDigitalSpecimenBotanyRequest() {
    return new DigitalSpecimenBotanyRequest(PID_ISSUER_PID,
        DIGITAL_OBJECT_TYPE_PID,
        DIGITAL_OBJECT_SUBTYPE_PID,
        LOCATIONS,
        REFERENT_DOI_NAME_PID,
        DIGITAL_OR_PHYSICAL,
        SPECIMEN_HOST_PID,
        IN_COLLECTION_FACILITY,
        OBJECT_TYPE,
        PRESERVED_OR_LIVING);
  }

  public static DigitalSpecimenBotanyResponse generateTestDigitalSpecimenBotanyResponse(
      byte[] handle) {
    return new DigitalSpecimenBotanyResponse(generateTestDigitalSpecimenBotanyRecord(handle));
  }

  public static List<byte[]> generateByteHandleList() {
    List<byte[]> handles = new ArrayList<>();
    handles.add(HANDLE.getBytes());
    handles.add(HANDLE_ALT.getBytes());

    return handles;
  }

  public static long initTime() {
    return CREATED.getEpochSecond();
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
