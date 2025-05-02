package eu.dissco.core.handlemanager.testUtils;

import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.ANNOTATION_HASH;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.CATALOG_NUMBER;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.DIGITAL_OBJECT_NAME;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.DIGITAL_OBJECT_TYPE;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.FDO_PROFILE;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.FDO_RECORD_LICENSE_ID;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.FDO_RECORD_LICENSE_NAME;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.HAS_RELATED_PID;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.HS_ADMIN;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.ISSUED_FOR_AGENT;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.ISSUED_FOR_AGENT_NAME;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.LICENSE_NAME;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.LICENSE_URL;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.LIVING_OR_PRESERVED;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.LOC;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.MARKED_AS_TYPE;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.MAS_NAME;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.MATERIAL_SAMPLE_TYPE;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.MEDIA_HOST;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.MEDIA_HOST_NAME;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.MEDIA_TYPE;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.MIME_TYPE;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.NORMALISED_SPECIMEN_OBJECT_ID;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.ORGANISATION_ID;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.ORGANISATION_ID_TYPE;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.ORGANISATION_NAME;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.OTHER_SPECIMEN_IDS;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.PID;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.PID_ISSUER;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.PID_ISSUER_NAME;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.PID_RECORD_ISSUE_DATE;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.PID_RECORD_ISSUE_NUMBER;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.PID_STATUS;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.PRIMARY_MEDIA_ID;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.PRIMARY_MEDIA_ID_NAME;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.PRIMARY_MEDIA_ID_TYPE;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.REFERENT_NAME;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.RIGHTS_HOLDER_NAME;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.RIGHTS_HOLDER_PID;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.SOURCE_DATA_STANDARD;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.SOURCE_SYSTEM_NAME;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.SPECIMEN_HOST;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.SPECIMEN_HOST_NAME;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.TARGET_PID;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.TARGET_TYPE;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.TARGET_TYPE_NAME;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.TOMBSTONED_DATE;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.TOMBSTONED_TEXT;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.TOPIC_CATEGORY;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.TOPIC_DISCIPLINE;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.TOPIC_DOMAIN;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.TOPIC_ORIGIN;
import static eu.dissco.core.handlemanager.domain.fdo.FdoType.ANNOTATION;
import static eu.dissco.core.handlemanager.domain.fdo.FdoType.DIGITAL_MEDIA;
import static eu.dissco.core.handlemanager.domain.fdo.FdoType.DIGITAL_SPECIMEN;
import static eu.dissco.core.handlemanager.domain.fdo.FdoType.TOMBSTONE;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.dissco.core.handlemanager.configuration.InstantDeserializer;
import eu.dissco.core.handlemanager.configuration.InstantSerializer;
import eu.dissco.core.handlemanager.domain.fdo.FdoProfile;
import eu.dissco.core.handlemanager.domain.fdo.FdoType;
import eu.dissco.core.handlemanager.domain.fdo.PidStatus;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.FdoAttribute;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.FdoRecord;
import eu.dissco.core.handlemanager.domain.requests.PatchRequest;
import eu.dissco.core.handlemanager.domain.requests.PatchRequestData;
import eu.dissco.core.handlemanager.domain.requests.PostRequest;
import eu.dissco.core.handlemanager.domain.requests.PostRequestData;
import eu.dissco.core.handlemanager.domain.responses.JsonApiDataLinks;
import eu.dissco.core.handlemanager.domain.responses.JsonApiLinks;
import eu.dissco.core.handlemanager.domain.responses.JsonApiWrapperRead;
import eu.dissco.core.handlemanager.domain.responses.JsonApiWrapperWrite;
import eu.dissco.core.handlemanager.schema.AnnotationRequestAttributes;
import eu.dissco.core.handlemanager.schema.DataMappingRequestAttributes;
import eu.dissco.core.handlemanager.schema.DigitalMediaRequestAttributes;
import eu.dissco.core.handlemanager.schema.DigitalSpecimenRequestAttributes;
import eu.dissco.core.handlemanager.schema.DoiKernelRequestAttributes;
import eu.dissco.core.handlemanager.schema.HandleRequestAttributes;
import eu.dissco.core.handlemanager.schema.HasRelatedPid;
import eu.dissco.core.handlemanager.schema.MachineAnnotationServiceRequestAttributes;
import eu.dissco.core.handlemanager.schema.OrganisationRequestAttributes;
import eu.dissco.core.handlemanager.schema.SourceSystemRequestAttributes;
import eu.dissco.core.handlemanager.schema.TombstoneRequestAttributes;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.core.io.ClassPathResource;

@Slf4j
public class TestUtils {

  public static final String ISSUE_DATE_TESTVAL = "2022-11-01T09:59:24.000Z";
  public static final String UPDATE_DATE_TESTVAL = "2023-11-01T09:59:24.000Z";
  public static final Instant CREATED = Instant.parse(ISSUE_DATE_TESTVAL);
  public static final Instant UPDATED = Instant.parse(UPDATE_DATE_TESTVAL);
  public static final String PREFIX = "20.5000.1025";
  public static final String HANDLE = PREFIX + "/QRS-321-ABC";
  public static final String SUFFIX = "QRS-321-ABC";
  public static final String HANDLE_ALT = PREFIX + "/ABC-123-QRS";

  // Handles
  public static final String HANDLE_DOMAIN = "https://hdl.handle.net/";
  public static final String DOI_DOMAIN = "https://doi.org/";
  public static final String ROR_DOMAIN = "https://ror.org/";
  public static final String ISSUED_FOR_AGENT_TESTVAL = "https://ror.org/02wddde16"; // DiSSCo
  public static final String ISSUED_FOR_AGENT_NAME_TESTVAL = "Distributed System of Scientific Collections";
  public static final String UPDATED_VALUE = "_v2";
  public static final String LOC_TESTVAL = "https://dissco.eu";
  public static final String LOC_XML = "<locations>"
      + "<location href=\"" + LOC_TESTVAL + "\" id=\"0\" weight=\"0\"/>"
      + "</locations>";

  // DOI Request Attributes
  public static final String REFERENT_NAME_TESTVAL = "Bird nest";
  public static final String PRIMARY_REFERENT_TYPE_TESTVAL = "materialSample";

  // Generated Attributes
  public static final String PID_STATUS_TESTVAL = PidStatus.ACTIVE.name();

  //Digital Specimens
  public static final String ROR_IDENTIFIER = "0x123";
  public static final String SPECIMEN_HOST_TESTVAL = ROR_DOMAIN + ROR_IDENTIFIER;
  public static final String SPECIMEN_HOST_NAME_TESTVAL = "Naturalis";
  // Annotations
  public static final String TARGET_DOI_TESTVAL = HANDLE_DOMAIN + PREFIX + "/111";
  public static final UUID ANNOTATION_HASH_TESTVAL = UUID.fromString(
      "550e8400-e29b-41d4-a716-446655440000");
  // Media Objects
  public static final String MEDIA_HOST_TESTVAL = SPECIMEN_HOST_TESTVAL;
  public static final String MEDIA_HOST_NAME_TESTVAL = SPECIMEN_HOST_NAME_TESTVAL;
  public static final String LINKED_DO_PID_TESTVAL = HANDLE;
  public static final String PRIMARY_MEDIA_ID_TESTVAL = "https://images.com/ABC";
  // Mappings
  public static final String SOURCE_DATA_STANDARD_TESTVAL = "dwc";
  // MAS
  public static final String MAS_NAME_TESTVAL = "Plant Organ detection";
  // Tombstone Record vals
  public static final String TOMBSTONE_TEXT_TESTVAL = "pid was deleted";
  // Misc
  public static final String API_URL = "https://sandbox.dissco.tech";
  public static final String UI_URL = "https://sandbox.dissco.tech";
  public static final String PATH = UI_URL + HANDLE;
  public static final String ORCHESTRATION_URL = "https://orchestration.dissco.tech/";
  public static final String PTR_TYPE_DOI = "doi";
  public static final String CATALOG_ID_TEST = "https://botanical.nl/-qrs-123";
  public static final String PRIMARY_SPECIMEN_ID_ALT = "AVES.123";
  public static final String NORMALISED_PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL =
      CATALOG_ID_TEST + ":" + ROR_IDENTIFIER;
  public static final String EXTERNAL_PID = "21.T11148/d8de0819e144e4096645";
  public static final TransformerFactory TRANSFORMER_FACTORY = TransformerFactory.newInstance();
  public static final DocumentBuilderFactory DOC_BUILDER_FACTORY = DocumentBuilderFactory.newInstance();
  public static final ObjectMapper MAPPER;
  public static final Set<FdoProfile> GENERATED_KEYS;
  public static final String LICENSE = "https://spdx.org/licenses/CC0-1.0.json";

  static {
    GENERATED_KEYS = Set.of(FDO_PROFILE, FDO_RECORD_LICENSE_ID,
        FDO_RECORD_LICENSE_NAME, PID_ISSUER, PID_ISSUER_NAME,
        ISSUED_FOR_AGENT, ISSUED_FOR_AGENT_NAME, DIGITAL_OBJECT_TYPE,
        DIGITAL_OBJECT_NAME, PID, PID_RECORD_ISSUE_DATE,
        HS_ADMIN);
  }

  static {
    var mapper = new ObjectMapper().findAndRegisterModules();
    SimpleModule dateModule = new SimpleModule();
    dateModule.addSerializer(Instant.class, new InstantSerializer());
    dateModule.addDeserializer(Instant.class, new InstantDeserializer());
    mapper.registerModule(dateModule);
    MAPPER = mapper;
  }

  private TestUtils() {
    throw new IllegalStateException("Utility class");
  }

  public static TombstoneRequestAttributes givenTombstoneRecordRequestObject() {
    return new TombstoneRequestAttributes()
        .withTombstoneText(TOMBSTONE_TEXT_TESTVAL)
        .withHasRelatedPid(List.of(givenHasRelatedPid()));
  }

  public static HasRelatedPid givenHasRelatedPid() {
    return new HasRelatedPid().withPid(HANDLE_ALT).withRelationshipType("Media ID");
  }

  public static FdoRecord givenHandleFdoRecord(String handle) throws Exception {
    var attributes = genHandleRecordAttributes(handle, CREATED, FdoType.HANDLE);
    return new FdoRecord(handle, FdoType.HANDLE, attributes, null, attributes.values());
  }

  public static Map<FdoProfile, FdoAttribute> genHandleRecordAttributes(String handle,
      Instant timestamp,
      FdoType fdoType) throws Exception {
    var fdoAttributes = new EnumMap<FdoProfile, FdoAttribute>(FdoProfile.class);
    var loc = setLocations(handle, fdoType);
    fdoAttributes.put(LOC, new FdoAttribute(LOC, timestamp, loc));
    // 1: FDO Profile
    fdoAttributes.put(FDO_PROFILE,
        new FdoAttribute(FDO_PROFILE, timestamp, fdoType.getFdoProfile()));
    // 2: FDO Record License
    fdoAttributes.put(FDO_RECORD_LICENSE_ID, new FdoAttribute(FDO_RECORD_LICENSE_ID, timestamp,
        LICENSE));
    // 3: Fdo Record License Name
    fdoAttributes.put(FDO_RECORD_LICENSE_NAME,
        new FdoAttribute(FDO_RECORD_LICENSE_NAME, timestamp, "CC0 1.0 Universal"));
    // 4: DigitalObjectType
    fdoAttributes.put(DIGITAL_OBJECT_TYPE,
        new FdoAttribute(DIGITAL_OBJECT_TYPE, timestamp, fdoType.getDigitalObjectType()));
    // 5: DigitalObjectName
    fdoAttributes.put(DIGITAL_OBJECT_NAME,
        new FdoAttribute(DIGITAL_OBJECT_NAME, timestamp, fdoType.getDigitalObjectName()));
    // 6: Pid
    fdoAttributes.put(PID,
        new FdoAttribute(PID, timestamp, fdoType.getDomain() + handle));
    // 7: PidIssuer
    fdoAttributes.put(PID_ISSUER,
        new FdoAttribute(PID_ISSUER, timestamp, ISSUED_FOR_AGENT_TESTVAL));
    // 8: pidIssuerName
    fdoAttributes.put(PID_ISSUER_NAME,
        new FdoAttribute(PID_ISSUER_NAME, timestamp, ISSUED_FOR_AGENT_NAME_TESTVAL));
    // 9: pidRecordIssueDate
    fdoAttributes.put(PID_RECORD_ISSUE_DATE,
        new FdoAttribute(PID_RECORD_ISSUE_DATE, timestamp, ISSUE_DATE_TESTVAL));
    // 10: pidRecordIssueNumber
    fdoAttributes.put(PID_RECORD_ISSUE_NUMBER,
        new FdoAttribute(PID_RECORD_ISSUE_NUMBER, timestamp, "1"));
    // 11: PidStatus
    fdoAttributes.put(PID_STATUS, new FdoAttribute(PID_STATUS, timestamp, PID_STATUS_TESTVAL));
    // 100 ADMIN
    fdoAttributes.put(HS_ADMIN, new FdoAttribute(timestamp, PREFIX));

    return fdoAttributes;
  }

  public static FdoRecord givenDraftFdoRecord(FdoType fdoType, String primaryLocalId,
      String userLocations)
      throws Exception {
    var attributes = genAttributes(fdoType, HANDLE);
    attributes.replace(PID_STATUS, new FdoAttribute(PID_STATUS, CREATED, PidStatus.DRAFT));
    attributes.replace(LOC, new FdoAttribute(LOC, CREATED,
        Objects.requireNonNullElse(userLocations, "<locations></locations>")));
    return new FdoRecord(HANDLE, fdoType, attributes, primaryLocalId, attributes.values());
  }

  public static FdoRecord givenUpdatedFdoRecord(FdoType fdoType, String primaryLocalId)
      throws Exception {
    var attributes = new EnumMap<>(genAttributes(fdoType, HANDLE, CREATED));
    var updatedAttribute = getUpdatedAttribute(fdoType);
    var result = attributes.entrySet().stream()
        .map(attribute -> {
          if (GENERATED_KEYS.contains(attribute.getKey())) {
            return Map.entry(attribute.getKey(), attribute.getValue());
          }
          if (attribute.getKey().equals(updatedAttribute)) {
            if (updatedAttribute.equals(LOC)) {
              var updatedLoc = LOC_XML.replace(LOC_TESTVAL, UPDATED_VALUE);
              return Map.entry(LOC, new FdoAttribute(updatedAttribute, UPDATED, updatedLoc));
            }
            return Map.entry(updatedAttribute,
                new FdoAttribute(updatedAttribute, UPDATED, UPDATED_VALUE));
          }
          if (attribute.getKey().equals(PID_RECORD_ISSUE_NUMBER)) {
            return Map.entry(PID_RECORD_ISSUE_NUMBER,
                new FdoAttribute(PID_RECORD_ISSUE_NUMBER, UPDATED, "2"));
          }
          return Map.entry(attribute.getKey(),
              new FdoAttribute(attribute.getKey(), UPDATED, attribute.getValue().getValue()));
        })
        .collect(Collectors.toMap(
            Map.Entry::getKey,
            Map.Entry::getValue
        ));

    return new FdoRecord(HANDLE, fdoType, result, primaryLocalId, result.values());
  }

  private static FdoProfile getUpdatedAttribute(FdoType fdoType) {
    switch (fdoType) {
      case HANDLE -> {
        return LOC;
      }
      case DOI, DIGITAL_MEDIA, DIGITAL_SPECIMEN, ORGANISATION -> {
        return REFERENT_NAME;
      }
      case ANNOTATION -> {
        return TARGET_PID;
      }
      case DATA_MAPPING -> {
        return SOURCE_DATA_STANDARD;
      }
      case MAS -> {
        return MAS_NAME;
      }
      case SOURCE_SYSTEM -> {
        return SOURCE_SYSTEM_NAME;
      }
      default -> throw new IllegalStateException();
    }
  }

  public static FdoRecord givenDoiFdoRecord(String handle) throws Exception {
    var attributes = genDoiRecordAttributes(handle, CREATED, FdoType.DOI);
    return new FdoRecord(handle, FdoType.DOI, attributes, null, attributes.values());
  }

  public static Map<FdoProfile, FdoAttribute> genDoiRecordAttributes(String handle,
      Instant timestamp,
      FdoType type) throws Exception {
    var fdoRecord = genHandleRecordAttributes(handle, timestamp, type);
    // 40: issuedForAgent
    fdoRecord.put(ISSUED_FOR_AGENT,
        new FdoAttribute(ISSUED_FOR_AGENT, timestamp, ISSUED_FOR_AGENT_TESTVAL));
    // 41: issuedForAgentName
    fdoRecord.put(ISSUED_FOR_AGENT_NAME,
        new FdoAttribute(ISSUED_FOR_AGENT_NAME, timestamp, ISSUED_FOR_AGENT_NAME_TESTVAL));
    // 42: referentName
    fdoRecord.put(REFERENT_NAME, new FdoAttribute(REFERENT_NAME, timestamp, REFERENT_NAME_TESTVAL));
    return fdoRecord;
  }

  public static FdoRecord givenDigitalSpecimenFdoRecord(String handle) throws Exception {
    var attributes = genDigitalSpecimenAttributes(handle, CREATED);
    return new FdoRecord(handle, FdoType.DIGITAL_SPECIMEN, attributes,
        NORMALISED_PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL, attributes.values());
  }

  public static Map<FdoProfile, FdoAttribute> genDigitalSpecimenAttributes(String handle,
      Instant timestamp)
      throws Exception {
    var fdoRecord = genHandleRecordAttributes(handle, timestamp,
        FdoType.DIGITAL_SPECIMEN);
    fdoRecord.put(ISSUED_FOR_AGENT,
        new FdoAttribute(ISSUED_FOR_AGENT, timestamp, ISSUED_FOR_AGENT_TESTVAL));
    fdoRecord.put(ISSUED_FOR_AGENT_NAME,
        new FdoAttribute(ISSUED_FOR_AGENT_NAME, timestamp, ISSUED_FOR_AGENT_NAME_TESTVAL));
    fdoRecord.put(REFERENT_NAME, new FdoAttribute(REFERENT_NAME, timestamp, REFERENT_NAME_TESTVAL));
    fdoRecord.put(SPECIMEN_HOST, new FdoAttribute(SPECIMEN_HOST, timestamp, SPECIMEN_HOST_TESTVAL));
    fdoRecord.put(SPECIMEN_HOST_NAME,
        new FdoAttribute(SPECIMEN_HOST_NAME, timestamp, SPECIMEN_HOST_NAME_TESTVAL));
    fdoRecord.put(NORMALISED_SPECIMEN_OBJECT_ID,
        new FdoAttribute(NORMALISED_SPECIMEN_OBJECT_ID, timestamp,
            NORMALISED_PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL));
    fdoRecord.put(OTHER_SPECIMEN_IDS, new FdoAttribute(OTHER_SPECIMEN_IDS, timestamp, null));
    fdoRecord.put(TOPIC_ORIGIN, new FdoAttribute(TOPIC_ORIGIN, timestamp, null));
    fdoRecord.put(TOPIC_DOMAIN, new FdoAttribute(TOPIC_DOMAIN, timestamp, null));
    fdoRecord.put(TOPIC_DISCIPLINE,
        new FdoAttribute(TOPIC_DISCIPLINE, timestamp, null));
    fdoRecord.put(TOPIC_CATEGORY,
        new FdoAttribute(TOPIC_CATEGORY, timestamp, null));
    fdoRecord.put(LIVING_OR_PRESERVED, new FdoAttribute(LIVING_OR_PRESERVED, timestamp, null));
    fdoRecord.put(MATERIAL_SAMPLE_TYPE, new FdoAttribute(MATERIAL_SAMPLE_TYPE, timestamp, null));
    fdoRecord.put(MARKED_AS_TYPE,
        new FdoAttribute(MARKED_AS_TYPE, timestamp, null));
    fdoRecord.put(CATALOG_NUMBER,
        new FdoAttribute(CATALOG_NUMBER, timestamp, CATALOG_ID_TEST));
    return fdoRecord;
  }

  public static FdoRecord givenMongoResponse(String handle, FdoType fdoType, String primaryLocalId)
      throws Exception {
    var attributes = genAttributes(fdoType, handle);
    return new FdoRecord(handle, fdoType, Collections.emptyMap(), primaryLocalId,
        new ArrayList<>(attributes.values()));
  }


  public static FdoRecord givenDigitalMediaFdoRecord(String handle) throws Exception {
    var attributes = genDigitalMediaAttributes(handle, CREATED);
    return new FdoRecord(handle, DIGITAL_MEDIA, attributes, PRIMARY_MEDIA_ID_TESTVAL,
        attributes.values());
  }

  public static Map<FdoProfile, FdoAttribute> genDigitalMediaAttributes(String handle,
      Instant timestamp) throws Exception {
    var fdoRecord = genHandleRecordAttributes(handle, timestamp, FdoType.DIGITAL_MEDIA);
    // 40: issuedForAgent
    fdoRecord.put(ISSUED_FOR_AGENT,
        new FdoAttribute(ISSUED_FOR_AGENT, timestamp, ISSUED_FOR_AGENT_TESTVAL));
    // 41: issuedForAgentName
    fdoRecord.put(ISSUED_FOR_AGENT_NAME,
        new FdoAttribute(ISSUED_FOR_AGENT_NAME, timestamp, ISSUED_FOR_AGENT_NAME_TESTVAL));
    fdoRecord.put(REFERENT_NAME, new FdoAttribute(REFERENT_NAME, timestamp, REFERENT_NAME_TESTVAL));
    // 43: primaryReferentType
    fdoRecord.put(MEDIA_HOST, new FdoAttribute(MEDIA_HOST, timestamp, MEDIA_HOST_TESTVAL));
    fdoRecord.put(MEDIA_HOST_NAME,
        new FdoAttribute(MEDIA_HOST_NAME, timestamp, MEDIA_HOST_NAME_TESTVAL));
    fdoRecord.put(PRIMARY_MEDIA_ID,
        new FdoAttribute(PRIMARY_MEDIA_ID, timestamp, PRIMARY_MEDIA_ID_TESTVAL));
    fdoRecord.put(PRIMARY_MEDIA_ID_TYPE,
        new FdoAttribute(PRIMARY_MEDIA_ID_TYPE, timestamp, null));
    fdoRecord.put(PRIMARY_MEDIA_ID_NAME,
        new FdoAttribute(PRIMARY_MEDIA_ID_NAME, timestamp, null));
    fdoRecord.put(MEDIA_TYPE, new FdoAttribute(MEDIA_TYPE, timestamp, null));
    fdoRecord.put(MIME_TYPE, new FdoAttribute(MIME_TYPE, timestamp, null));
    fdoRecord.put(LICENSE_NAME, new FdoAttribute(LICENSE_NAME, timestamp, null));
    fdoRecord.put(LICENSE_URL, new FdoAttribute(LICENSE_URL, timestamp, LICENSE));
    fdoRecord.put(RIGHTS_HOLDER_NAME,
        new FdoAttribute(RIGHTS_HOLDER_NAME, timestamp, SPECIMEN_HOST_NAME_TESTVAL));
    fdoRecord.put(RIGHTS_HOLDER_PID,
        new FdoAttribute(RIGHTS_HOLDER_PID, timestamp, SPECIMEN_HOST_TESTVAL));
    return fdoRecord;
  }

  public static FdoRecord givenAnnotationFdoRecord(String handle, boolean includeHash)
      throws Exception {
    var attributes = genAnnotationAttributes(handle, includeHash);
    var localId = includeHash ? ANNOTATION_HASH_TESTVAL.toString() : null;
    return new FdoRecord(handle, FdoType.ANNOTATION, attributes, localId, attributes.values());
  }

  public static FdoRecord givenMasFdoRecord(String handle) throws Exception {
    var attributes = genMasAttributes(handle, CREATED);
    return new FdoRecord(handle, FdoType.MAS, attributes, null, attributes.values());
  }

  public static FdoRecord givenSourceSystemFdoRecord(String handle) throws Exception {
    var attributes = genSourceSystemAttributes(handle, CREATED);
    return new FdoRecord(handle, FdoType.SOURCE_SYSTEM, attributes, null, attributes.values());
  }

  public static FdoRecord givenOrganisationFdoRecord(String handle) throws Exception {
    var attributes = genOrganisationAttributes(handle, CREATED);
    return new FdoRecord(handle, FdoType.ORGANISATION, attributes, null, attributes.values());
  }

  public static FdoRecord givenDataMappingFdoRecord(String handle) throws Exception {
    var attributes = genMappingAttributes(handle, CREATED);
    return new FdoRecord(handle, FdoType.DATA_MAPPING, attributes, null, attributes.values());
  }

  public static FdoRecord givenTombstoneFdoRecord() throws Exception {
    var attributes = genTombstoneAttributes(givenTombstoneRecordRequestObject());
    return new FdoRecord(HANDLE, FdoType.HANDLE, attributes, null, attributes.values());
  }

  public static Map<FdoProfile, FdoAttribute> genAnnotationAttributes(String handle,
      boolean includeHash)
      throws Exception {
    return genAnnotationAttributes(handle, CREATED, includeHash);
  }

  public static Map<FdoProfile, FdoAttribute> genAnnotationAttributes(String handle,
      Instant timestamp,
      boolean includeHash) throws Exception {
    var fdoRecord = genHandleRecordAttributes(handle, timestamp, FdoType.ANNOTATION);
    // 500 TargetPid
    fdoRecord.put(TARGET_PID, new FdoAttribute(TARGET_PID, timestamp, TARGET_DOI_TESTVAL));
    // 501 TargetType
    fdoRecord.put(TARGET_TYPE,
        new FdoAttribute(TARGET_TYPE, timestamp, DIGITAL_SPECIMEN.getDigitalObjectType()));
    // 502 motivation
    fdoRecord.put(TARGET_TYPE_NAME,
        new FdoAttribute(TARGET_TYPE_NAME, timestamp, DIGITAL_SPECIMEN.getDigitalObjectName()));
    // 503 AnnotationHash
    if (includeHash) {
      fdoRecord.put(ANNOTATION_HASH,
          new FdoAttribute(ANNOTATION_HASH, timestamp, ANNOTATION_HASH_TESTVAL));
    } else {
      fdoRecord.put(ANNOTATION_HASH, new FdoAttribute(ANNOTATION_HASH, timestamp, null));
    }
    return fdoRecord;
  }

  public static Map<FdoProfile, FdoAttribute> genMasAttributes(String handle, Instant timestamp)
      throws Exception {
    var fdoRecord = genHandleRecordAttributes(handle, timestamp, FdoType.MAS);
    fdoRecord.put(MAS_NAME, new FdoAttribute(MAS_NAME, timestamp, MAS_NAME_TESTVAL));
    return fdoRecord;
  }

  public static Map<FdoProfile, FdoAttribute> genMappingAttributes(String handle, Instant timestamp)
      throws Exception {
    var fdoRecord = genHandleRecordAttributes(handle, timestamp, FdoType.DATA_MAPPING);
    // 500 subjectDigitalObjectId
    fdoRecord.put(SOURCE_DATA_STANDARD,
        new FdoAttribute(SOURCE_DATA_STANDARD, timestamp, SOURCE_DATA_STANDARD_TESTVAL));
    return fdoRecord;
  }

  public static Map<FdoProfile, FdoAttribute> genSourceSystemAttributes(String handle,
      Instant timestamp)
      throws Exception {
    var fdoRecord = genHandleRecordAttributes(handle, timestamp, FdoType.SOURCE_SYSTEM);
    // 600 hostInstitution
    fdoRecord.put(SOURCE_SYSTEM_NAME,
        new FdoAttribute(SOURCE_SYSTEM_NAME, timestamp, SPECIMEN_HOST_TESTVAL));
    return fdoRecord;
  }

  public static Map<FdoProfile, FdoAttribute> genOrganisationAttributes(String handle,
      Instant timestamp)
      throws Exception {
    var fdoRecord = genHandleRecordAttributes(handle, timestamp, FdoType.ORGANISATION);
    // 40: issuedForAgent
    fdoRecord.put(ISSUED_FOR_AGENT,
        new FdoAttribute(ISSUED_FOR_AGENT, timestamp, ISSUED_FOR_AGENT_TESTVAL));
    // 41: issuedForAgentName
    fdoRecord.put(ISSUED_FOR_AGENT_NAME,
        new FdoAttribute(ISSUED_FOR_AGENT_NAME, timestamp, ISSUED_FOR_AGENT_NAME_TESTVAL));
    // 42: referentName
    fdoRecord.put(REFERENT_NAME, new FdoAttribute(REFERENT_NAME, timestamp, REFERENT_NAME_TESTVAL));
    // 800 OrganisationIdentifier
    fdoRecord.put(ORGANISATION_ID,
        new FdoAttribute(ORGANISATION_ID, timestamp, SPECIMEN_HOST_TESTVAL));
    // 801 OrganisationIdentifier
    fdoRecord.put(ORGANISATION_ID_TYPE,
        new FdoAttribute(ORGANISATION_ID_TYPE, timestamp, PTR_TYPE_DOI));
    // 802 OrganisationName
    fdoRecord.put(ORGANISATION_NAME,
        new FdoAttribute(ORGANISATION_NAME, timestamp, SPECIMEN_HOST_NAME_TESTVAL));
    return fdoRecord;
  }

  public static Map<FdoProfile, FdoAttribute> genTombstoneAttributes(
      TombstoneRequestAttributes request)
      throws Exception {
    var fdoRecord = genHandleRecordAttributes(HANDLE, CREATED, FdoType.HANDLE);
    fdoRecord.put(TOMBSTONED_TEXT,
        new FdoAttribute(TOMBSTONED_TEXT, UPDATED, request.getTombstoneText()));
    fdoRecord.replace(PID_RECORD_ISSUE_NUMBER,
        new FdoAttribute(PID_RECORD_ISSUE_NUMBER, UPDATED, "2"));
    fdoRecord.replace(PID_STATUS, new FdoAttribute(PID_STATUS, UPDATED, PidStatus.TOMBSTONED));
    if (request.getHasRelatedPid() != null && !request.getHasRelatedPid().isEmpty()) {
      fdoRecord.put(HAS_RELATED_PID, new FdoAttribute(HAS_RELATED_PID, UPDATED,
          MAPPER.writeValueAsString(request.getHasRelatedPid())));
    } else {
      fdoRecord.put(HAS_RELATED_PID, new FdoAttribute(HAS_RELATED_PID, UPDATED,
          MAPPER.writeValueAsString(Collections.emptyList())));
    }
    fdoRecord.put(TOMBSTONED_DATE, new FdoAttribute(TOMBSTONED_DATE, UPDATED, UPDATE_DATE_TESTVAL));
    return fdoRecord;
  }

  public static PostRequest givenPostRequest(Object request, FdoType fdoType) {
    return new PostRequest(
        new PostRequestData(
            fdoType,
            MAPPER.valueToTree(request)
        )
    );
  }

  public static PatchRequest givenPatchRequest(Object request, FdoType fdoType) {
    return new PatchRequest(
        new PatchRequestData(
            HANDLE,
            fdoType,
            MAPPER.valueToTree(request)
        )
    );
  }

  // Single Requests
  public static HandleRequestAttributes givenHandleKernel() {
    return new HandleRequestAttributes();
  }

  public static HandleRequestAttributes givenHandleKernelUpdated() {
    return givenHandleKernel()
        .withLocations(List.of(UPDATED_VALUE));
  }

  public static DoiKernelRequestAttributes givenDoiKernel() {
    return new DoiKernelRequestAttributes()
        .withReferentName(REFERENT_NAME_TESTVAL);
  }

  public static DoiKernelRequestAttributes givenDoiKernelUpdated() {
    return givenDoiKernel()
        .withReferentName(UPDATED_VALUE);
  }

  public static DigitalSpecimenRequestAttributes givenDigitalSpecimen() {
    return new DigitalSpecimenRequestAttributes()
        .withCatalogNumber(CATALOG_ID_TEST)
        .withReferentName(REFERENT_NAME_TESTVAL)
        .withSpecimenHost(SPECIMEN_HOST_TESTVAL)
        .withSpecimenHostName(SPECIMEN_HOST_NAME_TESTVAL)
        .withNormalisedPrimarySpecimenObjectId(NORMALISED_PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL);
  }

  public static DigitalSpecimenRequestAttributes givenDigitalSpecimenUpdated() {
    return givenDigitalSpecimen()
        .withReferentName(UPDATED_VALUE);
  }

  public static DigitalMediaRequestAttributes givenDigitalMedia() {
    return new DigitalMediaRequestAttributes()
        .withReferentName(REFERENT_NAME_TESTVAL)
        .withMediaHost(MEDIA_HOST_TESTVAL)
        .withMediaHostName(MEDIA_HOST_NAME_TESTVAL)
        .withRightsHolderPid(SPECIMEN_HOST_TESTVAL)
        .withPrimaryMediaId(PRIMARY_MEDIA_ID_TESTVAL)
        .withRightsHolder(SPECIMEN_HOST_NAME_TESTVAL)
        .withLicenseUrl(LICENSE);
  }

  public static DigitalMediaRequestAttributes givenDigitalMediaUpdated() {
    return givenDigitalMedia()
        .withReferentName(UPDATED_VALUE);
  }

  public static OrganisationRequestAttributes givenOrganisation() {
    return new OrganisationRequestAttributes()
        .withIssuedForAgent(ISSUED_FOR_AGENT_TESTVAL)
        .withReferentType(FdoType.ORGANISATION.getDigitalObjectName())
        .withReferentName(REFERENT_NAME_TESTVAL)
        .withPrimaryReferentType(PRIMARY_REFERENT_TYPE_TESTVAL)
        .withOrganisationIdentifier(SPECIMEN_HOST_TESTVAL)
        .withOrganisationIdentifierType(PTR_TYPE_DOI);
  }

  public static OrganisationRequestAttributes givenOrganisationUpdated() {
    return givenOrganisation()
        .withReferentName(UPDATED_VALUE);
  }

  public static MachineAnnotationServiceRequestAttributes givenMas() {
    return new MachineAnnotationServiceRequestAttributes()
        .withMachineAnnotationServiceName(MAS_NAME_TESTVAL);
  }

  public static MachineAnnotationServiceRequestAttributes givenMasUpdated() {
    return givenMas()
        .withMachineAnnotationServiceName(UPDATED_VALUE);
  }

  public static SourceSystemRequestAttributes givenSourceSystem() {
    return new SourceSystemRequestAttributes()
        .withSourceSystemName(SPECIMEN_HOST_TESTVAL);
  }

  public static SourceSystemRequestAttributes givenSourceSystemUpdated() {
    return givenSourceSystem()
        .withSourceSystemName(UPDATED_VALUE);
  }

  public static DataMappingRequestAttributes givenDataMapping() {
    return new DataMappingRequestAttributes()
        .withSourceDataStandard(SOURCE_DATA_STANDARD_TESTVAL);
  }

  public static DataMappingRequestAttributes givenDataMappingUpdated() {
    return givenDataMapping()
        .withSourceDataStandard(UPDATED_VALUE);
  }

  public static AnnotationRequestAttributes givenAnnotation(boolean includeHash) {
    var annotation = new AnnotationRequestAttributes()
        .withTargetPid(TARGET_DOI_TESTVAL)
        .withTargetType(DIGITAL_SPECIMEN.getDigitalObjectType());
    if (includeHash) {
      annotation.withAnnotationHash(ANNOTATION_HASH_TESTVAL);
    }
    return annotation;
  }

  public static AnnotationRequestAttributes givenAnnotationUpdated() {
    return givenAnnotation(false)
        .withTargetPid(UPDATED_VALUE);
  }

  // Misc
  public static FdoAttribute getField(List<FdoAttribute> fdoAttributes, FdoProfile targetField) {
    for (var attribute : fdoAttributes) {
      if (attribute.getIndex() == targetField.index()) {
        return attribute;
      }
    }
    log.error("Unable to find field {} in record {}", targetField, fdoAttributes);
    throw new IllegalStateException();
  }

  // Json api Responses

  public static JsonApiWrapperRead givenReadResponse(List<String> handles, String path,
      FdoType recordType, String domain) throws Exception {
    List<JsonApiDataLinks> dataNodes = new ArrayList<>();
    for (String handle : handles) {
      var testDbRecord = genAttributes(recordType, handle);
      JsonNode recordAttributes = jsonFormatFdoRecord(testDbRecord.values());
      var pidLink = new JsonApiLinks(domain + handle);
      dataNodes.add(
          new JsonApiDataLinks(handle, recordType.getDigitalObjectType(), recordAttributes,
              pidLink));
    }
    var responseLink = new JsonApiLinks(path);
    return new JsonApiWrapperRead(responseLink, dataNodes);
  }

  public static JsonApiWrapperWrite givenWriteResponseFull(List<String> handles,
      FdoType fdoType) throws Exception {
    List<JsonApiDataLinks> dataNodes = new ArrayList<>();
    for (var handle : handles) {
      var testDbRecord = genAttributes(fdoType, handle);
      JsonNode recordAttributes = jsonFormatFdoRecord(testDbRecord.values());
      var pidLink = new JsonApiLinks(HANDLE_DOMAIN + handle);
      fdoType = fdoType == TOMBSTONE ? FdoType.HANDLE : fdoType;
      dataNodes.add(
          new JsonApiDataLinks(handle, fdoType.getDigitalObjectType(), recordAttributes,
              pidLink));
    }
    return new JsonApiWrapperWrite(dataNodes);
  }

  public static JsonApiWrapperWrite givenWriteResponseFull(
      FdoRecord fdoRecord) {
    JsonNode recordAttributes = jsonFormatFdoRecord(fdoRecord.values());
    var pidLink = new JsonApiLinks(HANDLE_DOMAIN + HANDLE);
    var dataNodes = List.of(
        new JsonApiDataLinks(HANDLE, fdoRecord.fdoType().getDigitalObjectType(), recordAttributes,
            pidLink));
    return new JsonApiWrapperWrite(dataNodes);
  }

  public static JsonApiWrapperWrite givenWriteResponseIdsOnly(
      List<FdoRecord> fdoRecords, FdoType fdoType, String domain) {
    List<JsonApiDataLinks> dataNodes = new ArrayList<>();
    Collection<FdoAttribute> fdoSublist;
    for (var fdoRecord : fdoRecords) {
      var pidLink = new JsonApiLinks(domain + fdoRecord.handle());
      JsonNode recordAttributes;
      switch (fdoType) {
        case ANNOTATION -> {
          if (fdoRecord.primaryLocalId() == null) {
            fdoSublist = List.of(fdoRecord.attributes().get(TARGET_PID));
            recordAttributes = jsonFormatFdoRecord(fdoSublist);
          } else {
            fdoSublist = List.of(fdoRecord.attributes().get(ANNOTATION_HASH));
            recordAttributes = jsonFormatFdoRecord(fdoSublist);
          }
        }
        case DIGITAL_SPECIMEN -> {
          fdoSublist = List.of(fdoRecord.attributes().get(NORMALISED_SPECIMEN_OBJECT_ID));
          recordAttributes = jsonFormatFdoRecord(fdoSublist);
        }
        case DIGITAL_MEDIA -> {
          recordAttributes = buildMediaWriteResponseJsonNode(fdoRecord);
        }
        default -> {
          fdoSublist = fdoRecord.values();
          recordAttributes = jsonFormatFdoRecord(fdoSublist);
        }
      }
      dataNodes.add(
          new JsonApiDataLinks(fdoRecord.handle(), fdoType.getDigitalObjectType(), recordAttributes,
              pidLink));
    }
    return new JsonApiWrapperWrite(dataNodes);
  }

  public static JsonNode buildMediaWriteResponseJsonNode(FdoRecord fdoRecord) {
    return MAPPER.createObjectNode()
        .put(PRIMARY_MEDIA_ID.getAttribute(), fdoRecord.attributes().get(PRIMARY_MEDIA_ID).getValue());
  }

  public static Map<FdoProfile, FdoAttribute> genAttributes(FdoType fdoType, String handle)
      throws Exception {
    return genAttributes(fdoType, handle, CREATED);
  }

  public static Map<FdoProfile, FdoAttribute> genAttributes(FdoType fdoType, String handle,
      Instant timestamp)
      throws Exception {
    switch (fdoType) {
      case DOI -> {
        return genDoiRecordAttributes(handle, timestamp, fdoType);
      }
      case DIGITAL_SPECIMEN -> {
        return genDigitalSpecimenAttributes(handle, timestamp);
      }
      case DIGITAL_MEDIA -> {
        return genDigitalMediaAttributes(handle, timestamp);
      }
      case ANNOTATION -> {
        return genAnnotationAttributes(handle, timestamp, false);
      }
      case DATA_MAPPING -> {
        return genMappingAttributes(handle, timestamp);
      }
      case SOURCE_SYSTEM -> {
        return genSourceSystemAttributes(handle, timestamp);
      }
      case ORGANISATION -> {
        return genOrganisationAttributes(handle, timestamp);
      }
      case MAS -> {
        return genMasAttributes(handle, timestamp);
      }
      case TOMBSTONE -> {
        return genTombstoneAttributes(givenTombstoneRecordRequestObject());
      }
      default -> {
        log.warn("Default type");
        return genHandleRecordAttributes(handle, timestamp, FdoType.HANDLE);
      }
    }
  }

  public static List<PatchRequest> givenUpdateRequest() {
    return givenUpdateRequest(List.of(HANDLE), FdoType.HANDLE,
        MAPPER.valueToTree(givenHandleKernelUpdated()));
  }

  public static List<PatchRequest> givenUpdateRequest(List<String> handles, FdoType type,
      JsonNode requestAttributes) {
    return handles.stream().map(handle -> new PatchRequest(new PatchRequestData(
        handle,
        type,
        requestAttributes
    ))).toList();
  }

  public static PatchRequest givenTombstoneRequest() {
    return new PatchRequest(
        new PatchRequestData(
            HANDLE,
            FdoType.HANDLE,
            MAPPER.valueToTree(givenTombstoneRecordRequestObject())
        )
    );
  }

  // Handle Attributes as ObjectNode
  public static JsonNode jsonFormatFdoRecord(Collection<FdoAttribute> dbRecord) {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode rootNode = mapper.createObjectNode();
    for (var row : dbRecord) {
      if (row.getIndex() != HS_ADMIN.index()) {
        var rowData = row.getValue();
        if (row.getValue() == null) {
          rootNode.set(row.getType(), mapper.nullNode());
        } else {
          try {
            var nodeData = mapper.readTree(rowData);
            rootNode.set(row.getType(), nodeData);
          } catch (JsonProcessingException ignored) {
            rootNode.put(row.getType(), rowData);
          }
        }
      }
    }
    return rootNode;
  }

  // Other Functions
  public static Document givenMongoDocument(FdoRecord fdoRecord) throws Exception {
    var doc = org.bson.Document.parse(MAPPER.writeValueAsString(fdoRecord))
        .append("_id", fdoRecord.handle());
    if (DIGITAL_SPECIMEN.equals(fdoRecord.fdoType())) {
      doc.append(NORMALISED_SPECIMEN_OBJECT_ID.get(), fdoRecord.primaryLocalId());
    } else if (DIGITAL_MEDIA.equals(fdoRecord.fdoType())) {
      doc.append(PRIMARY_MEDIA_ID.get(), fdoRecord.primaryLocalId());
    } else if (ANNOTATION.equals(fdoRecord.fdoType()) && fdoRecord.primaryLocalId() != null) {
      doc.append(ANNOTATION_HASH.get(), fdoRecord.primaryLocalId());
    }
    return doc;
  }

  public static String setLocations(String handle, FdoType type, boolean addKeyLoc)
      throws Exception {
    DOC_BUILDER_FACTORY.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);

    DocumentBuilder documentBuilder = DOC_BUILDER_FACTORY.newDocumentBuilder();

    var doc = documentBuilder.newDocument();
    var locations = doc.createElement("locations");
    doc.appendChild(locations);
    var xmlElements = defaultLocations(handle, type, addKeyLoc);
    if (xmlElements.isEmpty()) {
      return "<locations></locations>";
    }
    xmlElements.forEach(xmlLoc -> {
      var locs = doc.createElement("location");
      locs.setAttribute("id", String.valueOf(xmlLoc.id));
      locs.setAttribute("href", xmlLoc.loc);
      locs.setAttribute("weight", xmlLoc.weight);
      if (xmlLoc.view != null) {
        locs.setAttribute("view", xmlLoc.view);
      }
      locations.appendChild(locs);
    });
    return documentToString(doc);
  }

  public static String setLocations(String handle, FdoType type)
      throws Exception {
    return setLocations(handle, type, true);
  }

  private static List<XmlElement> defaultLocations(String handle, FdoType type, boolean addKeyLoc) {
    var locations = new ArrayList<XmlElement>();
    var i = new AtomicInteger();
    switch (type) {
      case DIGITAL_SPECIMEN -> {
        locations.add(new XmlElement(i.getAndIncrement(), "1", UI_URL + "/ds/" + handle, "HTML"));
        locations.add(
            new XmlElement(i.getAndIncrement(), "0", API_URL + "/digital-specimen/v1/" + handle,
                "JSON"));
        if (addKeyLoc) {
          locations.add(new XmlElement(i.getAndIncrement(), "0", CATALOG_ID_TEST, "CATALOG"));
        }
      }
      case DATA_MAPPING -> {
        locations.add(
            new XmlElement(i.getAndIncrement(), "1", ORCHESTRATION_URL + "/data-mapping/" + handle,
                "HTML"));
        locations.add(new XmlElement(i.getAndIncrement(), "0",
            ORCHESTRATION_URL + "/data-mapping/v1/" + handle,
            "JSON"));
      }
      case SOURCE_SYSTEM -> {
        locations.add(
            new XmlElement(i.getAndIncrement(), "1", ORCHESTRATION_URL + "/source-system/" + handle,
                "HTML"));
        locations.add(
            new XmlElement(i.getAndIncrement(), "0",
                ORCHESTRATION_URL + "/source-system/v1/" + handle, "JSON"));
      }
      case DIGITAL_MEDIA -> {
        locations.add(
            new XmlElement(i.getAndIncrement(), "1", UI_URL + "/dm/" + handle, "HTML"));
        locations.add(
            new XmlElement(i.getAndIncrement(), "0", API_URL + "/digital-media/v1/" + handle,
                "JSON"));
        if (addKeyLoc) {
          locations.add(
              new XmlElement(i.getAndIncrement(), "0", PRIMARY_MEDIA_ID_TESTVAL, "MEDIA"));
        }
      }
      case ANNOTATION -> {
        locations.add(
            new XmlElement(i.getAndIncrement(), "1", API_URL + "/annotations/v1/" + handle,
                "JSON"));
      }
      case ORGANISATION -> {
        if (addKeyLoc) {
          locations.add(new XmlElement(i.getAndIncrement(), "1", SPECIMEN_HOST_TESTVAL, "ROR"));
        }
      }
      case MAS -> {
        locations.add(
            new XmlElement(i.getAndIncrement(), "1", ORCHESTRATION_URL + "/mas/" + handle, "HTML"));
        locations.add(
            new XmlElement(i.getAndIncrement(), "0", ORCHESTRATION_URL + "/mas/v1/" + handle,
                "JSON"));
      }
      default -> {
        // Handle, DOI, OrganisationRequestAttributes (organisation handled separately)
      }
    }
    return locations;
  }

  private static String documentToString(org.w3c.dom.Document document)
      throws TransformerException {
    TRANSFORMER_FACTORY.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
    TRANSFORMER_FACTORY.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
    TRANSFORMER_FACTORY.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");

    var transformer = TRANSFORMER_FACTORY.newTransformer();
    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
    StringWriter writer = new StringWriter();
    transformer.transform(new DOMSource(document), new StreamResult(writer));
    return writer.getBuffer().toString();
  }

  public static String loadResourceFile(String fileName) throws IOException {
    return new String(new ClassPathResource(fileName).getInputStream().readAllBytes(),
        StandardCharsets.UTF_8);
  }

  private record XmlElement(
      int id,
      String weight,
      String loc,
      String view) {

  }


}
