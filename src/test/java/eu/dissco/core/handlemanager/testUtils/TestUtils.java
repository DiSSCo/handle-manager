package eu.dissco.core.handlemanager.testUtils;

import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.ANNOTATION_HASH;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.BASE_TYPE_OF_SPECIMEN;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.CATALOG_IDENTIFIER;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.DCTERMS_FORMAT;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.DCTERMS_SUBJECT;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.DCTERMS_TYPE;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.DC_TERMS_CONFORMS;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.DERIVED_FROM_ENTITY;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.DIGITAL_OBJECT_NAME;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.DIGITAL_OBJECT_TYPE;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.FDO_PROFILE;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.FDO_RECORD_LICENSE;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.HS_ADMIN;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.INFORMATION_ARTEFACT_TYPE;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.ISSUED_FOR_AGENT;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.ISSUED_FOR_AGENT_NAME;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.IS_DERIVED_FROM_SPECIMEN;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.LICENSE_NAME;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.LICENSE_URL;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.LINKED_ATTRIBUTE;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.LINKED_DO_PID;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.LINKED_DO_TYPE;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.LIVING_OR_PRESERVED;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.LOC;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.MARKED_AS_TYPE;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.MAS_NAME;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.MATERIAL_OR_DIGITAL_ENTITY;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.MATERIAL_SAMPLE_TYPE;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.MEDIA_HOST;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.MEDIA_HOST_NAME;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.MOTIVATION;
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
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.PRIMARY_MO_ID_NAME;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.PRIMARY_MO_ID_TYPE;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.PRIMARY_REFERENT_TYPE;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.PRIMARY_SPECIMEN_OBJECT_ID;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.PRIMARY_SPECIMEN_OBJECT_ID_NAME;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.PRIMARY_SPECIMEN_OBJECT_ID_TYPE;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.REFERENT_DOI_NAME;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.REFERENT_NAME;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.REFERENT_TYPE;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.RIGHTSHOLDER_NAME;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.RIGHTSHOLDER_PID;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.RIGHTSHOLDER_PID_TYPE;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.SOURCE_DATA_STANDARD;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.SOURCE_SYSTEM_NAME;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.SPECIMEN_HOST;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.SPECIMEN_HOST_NAME;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.SPECIMEN_OBJECT_ID_ABSENCE_REASON;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.STRUCTURAL_TYPE;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.TARGET_PID;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.TARGET_TYPE;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.TOMBSTONE_TEXT;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.TOPIC_CATEGORY;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.TOPIC_DISCIPLINE;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.TOPIC_DOMAIN;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.TOPIC_ORIGIN;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.WAS_DERIVED_FROM_ENTITY;
import static eu.dissco.core.handlemanager.domain.jsonapi.JsonApiFields.NODE_ATTRIBUTES;
import static eu.dissco.core.handlemanager.domain.jsonapi.JsonApiFields.NODE_DATA;
import static eu.dissco.core.handlemanager.domain.jsonapi.JsonApiFields.NODE_ID;
import static eu.dissco.core.handlemanager.domain.jsonapi.JsonApiFields.NODE_TYPE;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.dissco.core.handlemanager.domain.fdo.AnnotationRequest;
import eu.dissco.core.handlemanager.domain.fdo.DigitalSpecimenRequest;
import eu.dissco.core.handlemanager.domain.fdo.DoiRecordRequest;
import eu.dissco.core.handlemanager.domain.fdo.FdoProfile;
import eu.dissco.core.handlemanager.domain.fdo.FdoType;
import eu.dissco.core.handlemanager.domain.fdo.HandleRecordRequest;
import eu.dissco.core.handlemanager.domain.fdo.MappingRequest;
import eu.dissco.core.handlemanager.domain.fdo.MasRequest;
import eu.dissco.core.handlemanager.domain.fdo.MediaObjectRequest;
import eu.dissco.core.handlemanager.domain.fdo.OrganisationRequest;
import eu.dissco.core.handlemanager.domain.fdo.SourceSystemRequest;
import eu.dissco.core.handlemanager.domain.fdo.TombstoneRecordRequest;
import eu.dissco.core.handlemanager.domain.fdo.vocabulary.annotation.Motivation;
import eu.dissco.core.handlemanager.domain.fdo.vocabulary.media.LinkedDigitalObjectType;
import eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.PrimarySpecimenObjectIdType;
import eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.StructuralType;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiDataLinks;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiLinks;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiWrapperRead;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiWrapperReadSingle;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiWrapperWrite;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.FdoAttribute;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.FdoRecord;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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
import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.Document;

@Slf4j
public class TestUtils {

  public static final String ISSUE_DATE_TESTVAL = "2022-11-01T09:59:24.000Z";
  public static final Instant CREATED = Instant.parse(ISSUE_DATE_TESTVAL);
  public static final String PREFIX = "20.5000.1025";
  public static final String HANDLE = PREFIX + "/QRS-321-ABC";
  public static final String SUFFIX = "QRS-321-ABC";
  public static final String HANDLE_ALT = PREFIX + "/ABC-123-QRS";
  public static final List<String> HANDLE_LIST_STR;

  // Handles
  public static final String HANDLE_DOMAIN = "https://hdl.handle.net/";
  public static final String ROR_DOMAIN = "https://ror.org/";
  public static final String ISSUED_FOR_AGENT_TESTVAL = ROR_DOMAIN + "0566bfb96";
  public static final String PID_ISSUER_TESTVAL_OTHER = HANDLE_DOMAIN + "20.5000.1025/PID-ISSUER";
  public static final StructuralType STRUCTURAL_TYPE_TESTVAL = StructuralType.DIGITAL;
  public static final String[] LOC_TESTVAL = {"https://sandbox.dissco.tech/", "https://dissco.eu"};
  public static final String[] LOC_ALT_TESTVAL = {"naturalis.nl"};

  // DOI Request Attributes
  public static final String REFERENT_NAME_TESTVAL = "Bird nest";
  public static final String PRIMARY_REFERENT_TYPE_TESTVAL = "materialSample";


  // Generated Attributes
  public static final String PID_STATUS_TESTVAL = "TEST";
  public static final String REFERENT_DOI_NAME_TESTVAL = PREFIX + "/" + SUFFIX;
  //DOIs

  //Digital Specimens
  public static final String ROR_IDENTIFIER = "0x123";
  public static final String SPECIMEN_HOST_TESTVAL = ROR_DOMAIN + ROR_IDENTIFIER;
  public static final String SPECIMEN_HOST_NAME_TESTVAL = "Naturalis";
  // Annotations
  public static final String TARGET_DOI_TESTVAL = HANDLE_DOMAIN + PREFIX + "/111";
  public static final String TARGET_TYPE_TESTVAL = "digitalSpecimen";
  public static final Motivation MOTIVATION_TESTVAL = Motivation.EDITING;
  public static final UUID ANNOTATION_HASH_TESTVAL = UUID.fromString(
      "550e8400-e29b-41d4-a716-446655440000");

  // Media Objects
  public static final String MEDIA_HOST_TESTVAL = SPECIMEN_HOST_TESTVAL;
  public static final String MEDIA_HOST_NAME_TESTVAL = SPECIMEN_HOST_NAME_TESTVAL;
  public static final LinkedDigitalObjectType LINKED_DIGITAL_OBJECT_TYPE_TESTVAL = LinkedDigitalObjectType.SPECIMEN;
  public static final String LINKED_DO_PID_TESTVAL = HANDLE;
  public static final String LICENSE_NAME_TESTVAL = "CC0 1.0 Universal (CC0 1.0) Public Domain Dedication";
  public static final String PRIMARY_MEDIA_ID_TESTVAL = "https://images.com/ABC";
  // Mappings
  public static final String SOURCE_DATA_STANDARD_TESTVAL = "dwc";
  // MAS
  public static final String MAS_NAME_TESTVAL = "Plant Organ detection";

  public static final String API_URL = "https://sandbox.dissco.tech/api/v1";
  public static final String UI_URL = "https://sandbox.dissco.tech/";
  public static final String ORCHESTRATION_URL = "https://orchestration.dissco.tech/api/v1";
  public static final String PTR_TYPE_DOI = "doi";
  public final static String PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL = "BOTANICAL.QRS.123";
  public final static String NORMALISED_PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL =
      PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL + ":" + ROR_IDENTIFIER;
  public final static String EXTERNAL_PID = "21.T11148/d8de0819e144e4096645";

  // Tombstone Record vals
  public final static String TOMBSTONE_TEXT_TESTVAL = "pid was deleted";
  // Pid Type Record vals
  public static ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

  static {
    HANDLE_LIST_STR = List.of(HANDLE, HANDLE_ALT);
  }

  public static TransformerFactory TRANSFORMER_FACTORY = TransformerFactory.newInstance();
  public static DocumentBuilderFactory DOC_BUILDER_FACTORY = DocumentBuilderFactory.newInstance();

  private TestUtils() {
    throw new IllegalStateException("Utility class");
  }

  // Single Handle Attribute Lists
  public static FdoRecord givenHandleFdoRecord(String handle) throws Exception {
    return new FdoRecord(handle, FdoType.HANDLE, genHandleRecordAttributes(handle), null);
  }

  public static List<FdoAttribute> genHandleRecordAttributes(String handle) throws Exception {
    return genHandleRecordAttributes(handle, FdoType.HANDLE);
  }


  public static List<FdoAttribute> genHandleRecordAttributes(String handle, FdoType fdoType)
      throws Exception {
    List<FdoAttribute> fdoAttributes = new ArrayList<>();
    var request = givenHandleRecordRequestObject();
    var loc = setLocations(request.getLocations(), handle, fdoType, false);
    fdoAttributes.add(new FdoAttribute(LOC, CREATED, loc));

    // 1: FDO Profile
    fdoAttributes.add(new FdoAttribute(FDO_PROFILE, CREATED, fdoType.getFdoProfile()));

    // 2: FDO Record License
    fdoAttributes.add(new FdoAttribute(FDO_RECORD_LICENSE, CREATED,
        "https://creativecommons.org/publicdomain/zero/1.0/"));

    // 3: DigitalObjectType
    fdoAttributes.add(
        new FdoAttribute(DIGITAL_OBJECT_TYPE, CREATED, fdoType.getDigitalObjectType()));

    // 4: DigitalObjectName
    fdoAttributes.add(
        new FdoAttribute(DIGITAL_OBJECT_NAME, CREATED, fdoType.getDigitalObjectName()));

    // 5: Pid
    fdoAttributes.add(new FdoAttribute(PID, CREATED, HANDLE_DOMAIN + handle));

    // 6: PidIssuer
    fdoAttributes.add(new FdoAttribute(PID_ISSUER, CREATED, request.getPidIssuer()));

    // 7: pidIssuerName
    fdoAttributes.add(new FdoAttribute(PID_ISSUER_NAME, CREATED, PID_ISSUER_TESTVAL_OTHER));

    // 8: issuedForAgent
    fdoAttributes.add(new FdoAttribute(ISSUED_FOR_AGENT, CREATED, request.getIssuedForAgent()));

    // 9: issuedForAgentName
    fdoAttributes.add(new FdoAttribute(ISSUED_FOR_AGENT_NAME, CREATED, ISSUED_FOR_AGENT_TESTVAL));

    // 10: pidRecordIssueDate
    fdoAttributes.add(new FdoAttribute(PID_RECORD_ISSUE_DATE, CREATED, ISSUE_DATE_TESTVAL));

    // 11: pidRecordIssueNumber
    fdoAttributes.add(new FdoAttribute(PID_RECORD_ISSUE_NUMBER, CREATED, "1"));

    // 12: structuralType
    fdoAttributes.add(
        new FdoAttribute(STRUCTURAL_TYPE, CREATED, STRUCTURAL_TYPE_TESTVAL.toString()));

    // 13: PidStatus
    fdoAttributes.add(new FdoAttribute(PID_STATUS, CREATED, PID_STATUS_TESTVAL));

    return fdoAttributes;
  }

  public static List<FdoAttribute> genHandleRecordAttributesAltLoc(String handle) throws Exception {
    var attributes = genHandleRecordAttributes(handle, FdoType.HANDLE);
    var locOriginal = setLocations(LOC_TESTVAL, handle, FdoType.HANDLE, false);
    var locOriginalAttr = new FdoAttribute(LOC, CREATED, locOriginal);
    var locAlt = setLocations(LOC_ALT_TESTVAL, handle, FdoType.HANDLE, false);
    var locAltAttr = new FdoAttribute(LOC, CREATED, locAlt);
    attributes.set(attributes.indexOf(locOriginalAttr), locAltAttr);
    return attributes;
  }

  public static List<FdoAttribute> genTombstoneRecordFullAttributes(String handle)
      throws Exception {
    var fdoAttributes = genHandleRecordAttributes(handle, FdoType.TOMBSTONE);
    var oldPidStatus = new FdoAttribute(PID_STATUS, CREATED, PID_STATUS_TESTVAL);
    fdoAttributes.addAll(genHandleRecordAttributes(handle, FdoType.TOMBSTONE));
    fdoAttributes.remove(oldPidStatus);
    fdoAttributes = new ArrayList<>(
        (fdoAttributes.stream().filter(row -> row.getIndex() != LOC.index())).toList());
    fdoAttributes.add(givenLandingPageAttribute(handle));
    return fdoAttributes;
  }

  public static List<FdoAttribute> genUpdateRecordAttributesAltLoc(String handle)
      throws ParserConfigurationException, TransformerException {
    var locAlt = setLocations(LOC_ALT_TESTVAL, handle, FdoType.HANDLE, false);
    return List.of(new FdoAttribute(LOC, CREATED, locAlt));
  }

  public static List<FdoAttribute> genTombstoneRecordRequestAttributes(String handle)
      throws Exception {
    var tombstoneAttributes = new ArrayList<FdoAttribute>();
    tombstoneAttributes.add(new FdoAttribute(TOMBSTONE_TEXT, CREATED, TOMBSTONE_TEXT_TESTVAL));
    tombstoneAttributes.add(new FdoAttribute(PID_STATUS, CREATED, "ARCHIVED"));
    tombstoneAttributes.add(givenLandingPageAttribute(handle));
    return tombstoneAttributes;
  }

  public static FdoRecord givenDoiFdoRecord(String handle) throws Exception {
    return new FdoRecord(handle, FdoType.DOI, genDoiRecordAttributes(handle, FdoType.DOI), null);
  }

  public static List<FdoAttribute> genDoiRecordAttributes(String handle, FdoType type)
      throws Exception {
    return genDoiRecordAttributes(handle, type, givenDoiRecordRequestObject());
  }

  public static List<FdoAttribute> genDoiRecordAttributes(String handle, FdoType type,
      DoiRecordRequest request) throws Exception {
    var fdoRecord = genHandleRecordAttributes(handle, type);

    // 40: referentType
    fdoRecord.add(new FdoAttribute(REFERENT_TYPE, CREATED, request.getReferentType()));

    // 41: referentDoiName
    fdoRecord.add(new FdoAttribute(REFERENT_DOI_NAME, CREATED, REFERENT_DOI_NAME_TESTVAL));

    // 42: referentName
    fdoRecord.add(new FdoAttribute(REFERENT_NAME, CREATED, request.getReferentName()));

    // 43: primaryReferentType
    fdoRecord.add(
        new FdoAttribute(PRIMARY_REFERENT_TYPE, CREATED, request.getPrimaryReferentType()));
    return fdoRecord;
  }

  public static FdoRecord givenDigitalSpecimenFdoRecord(String handle) throws Exception {
    return new FdoRecord(handle, FdoType.DIGITAL_SPECIMEN, genDigitalSpecimenAttributes(handle),
        NORMALISED_PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL);
  }

  public static List<FdoAttribute> genDigitalSpecimenAttributes(String handle,
      DigitalSpecimenRequest request) throws Exception {
    List<FdoAttribute> fdoRecord = genDoiRecordAttributes(handle, FdoType.DIGITAL_SPECIMEN,
        request);
    // 200: Specimen Host
    fdoRecord.add(new FdoAttribute(SPECIMEN_HOST, CREATED, request.getSpecimenHost()));

    // 201: Specimen Host name
    fdoRecord.add(new FdoAttribute(SPECIMEN_HOST_NAME, CREATED, SPECIMEN_HOST_NAME_TESTVAL));

    // 202: primarySpecimenObjectId
    fdoRecord.add(new FdoAttribute(PRIMARY_SPECIMEN_OBJECT_ID, CREATED,
        request.getPrimarySpecimenObjectId()));

    // 203: primarySpecimenObjectIdType
    fdoRecord.add(new FdoAttribute(PRIMARY_SPECIMEN_OBJECT_ID_TYPE, CREATED,
        request.getPrimarySpecimenObjectIdType().toString()));

    // 204-217 are optional

    // 204: primarySpecimenObjectIdName
    if (request.getPrimarySpecimenObjectIdName() != null) {
      fdoRecord.add(new FdoAttribute(PRIMARY_SPECIMEN_OBJECT_ID_NAME, CREATED,
          request.getPrimarySpecimenObjectIdName()));
    }

    // 205: normalisedSpecimenObjectId
    fdoRecord.add(new FdoAttribute(NORMALISED_SPECIMEN_OBJECT_ID, CREATED,
        request.getNormalisedPrimarySpecimenObjectId()));

    // 206: specimenObjectIdAbsenceReason
    if (request.getSpecimenObjectIdAbsenceReason() != null) {
      fdoRecord.add(new FdoAttribute(SPECIMEN_OBJECT_ID_ABSENCE_REASON, CREATED,
          request.getSpecimenObjectIdAbsenceReason()));
    }

    // 207: otherSpecimenIds
    if (request.getOtherSpecimenIds() != null && !request.getOtherSpecimenIds().isEmpty()) {
      var otherSpecimenIds = MAPPER.writeValueAsString(request.getOtherSpecimenIds());
      fdoRecord.add(new FdoAttribute(OTHER_SPECIMEN_IDS, CREATED, otherSpecimenIds));
    }

    // 208: topicOrigin
    if (request.getTopicOrigin() != null) {
      fdoRecord.add(new FdoAttribute(TOPIC_ORIGIN, CREATED, request.getTopicOrigin().toString()));
    }

    // 209: topicDomain
    if (request.getTopicDomain() != null) {
      fdoRecord.add(new FdoAttribute(TOPIC_DOMAIN, CREATED, request.getTopicDomain().toString()));
    }

    // 210: topicDiscipline
    if (request.getTopicDiscipline() != null) {
      fdoRecord.add(
          new FdoAttribute(TOPIC_DISCIPLINE, CREATED, request.getTopicDiscipline().toString()));
    }

    // 211: topicCategory
    if (request.getTopicCategory() != null) {
      fdoRecord.add(
          new FdoAttribute(TOPIC_CATEGORY, CREATED, request.getTopicCategory().toString()));
    }

    // 212: livingOrPreserved
    if (request.getLivingOrPreserved() != null) {
      fdoRecord.add(new FdoAttribute(LIVING_OR_PRESERVED, CREATED,
          request.getLivingOrPreserved().toString()));
    }

    // 213: baseTypeOfSpecimen
    if (request.getBaseTypeOfSpecimen() != null) {
      fdoRecord.add(new FdoAttribute(BASE_TYPE_OF_SPECIMEN, CREATED,
          request.getBaseTypeOfSpecimen().toString()));
    }

    // 214: informationArtefactType
    if (request.getInformationArtefactType() != null) {
      fdoRecord.add(new FdoAttribute(INFORMATION_ARTEFACT_TYPE, CREATED,
          request.getInformationArtefactType().toString()));
    }

    // 215: materialSampleType
    if (request.getMaterialSampleType() != null) {
      fdoRecord.add(new FdoAttribute(MATERIAL_SAMPLE_TYPE, CREATED,
          request.getMaterialSampleType().toString()));
    }

    // 216: materialOrDigitalEntity
    if (request.getMaterialSampleType() != null) {
      fdoRecord.add(new FdoAttribute(MATERIAL_OR_DIGITAL_ENTITY, CREATED,
          request.getMaterialOrDigitalEntity().toString()));
    }

    // 217: markedAsType
    if (request.getMarkedAsType() != null) {
      fdoRecord.add(
          new FdoAttribute(MARKED_AS_TYPE, CREATED, request.getMarkedAsType().toString()));
    }

    // 218: wasDerivedFromEntity
    if (request.getDerivedFromEntity() != null) {
      fdoRecord.add(
          new FdoAttribute(WAS_DERIVED_FROM_ENTITY, CREATED, request.getDerivedFromEntity()));
    }
    var catId = request.getCatalogIdentifier();
    if (catId != null) {
      fdoRecord.add(new FdoAttribute(CATALOG_IDENTIFIER, CREATED, catId));
    }

    return fdoRecord;

  }

  public static List<FdoAttribute> genDigitalSpecimenAttributes(String handle) throws Exception {
    var request = givenDigitalSpecimenRequestObjectNullOptionals();
    return genDigitalSpecimenAttributes(handle, request);
  }

  public static FdoRecord givenDigitalMediaFdoRecord(String handle) throws Exception {
    return new FdoRecord(handle, FdoType.DIGITAL_SPECIMEN, genMediaObjectAttributes(handle),
        PRIMARY_MEDIA_ID_TESTVAL);
  }

  public static List<FdoAttribute> genMediaObjectAttributes(String handle) throws Exception {
    var request = givenMediaRequestObject();
    return genMediaObjectAttributes(handle, request);
  }

  public static List<FdoAttribute> genMediaObjectAttributes(String handle,
      MediaObjectRequest request) throws Exception {
    var fdoRecord = genDoiRecordAttributes(handle, FdoType.MEDIA_OBJECT);
    fdoRecord.add(new FdoAttribute(MEDIA_HOST, CREATED, request.getMediaHost()));
    if (request.getMediaHostName() == null) {
      fdoRecord.add(new FdoAttribute(MEDIA_HOST_NAME, CREATED, MEDIA_HOST_NAME_TESTVAL));
    } else {
      fdoRecord.add(new FdoAttribute(MEDIA_HOST_NAME, CREATED, request.getMediaHostName()));
    }
    if (request.getDctermsFormat() != null) {
      fdoRecord.add(
          new FdoAttribute(DCTERMS_FORMAT, CREATED, request.getDctermsFormat().toString()));
    }
    fdoRecord.add(new FdoAttribute(IS_DERIVED_FROM_SPECIMEN, CREATED,
        request.getIsDerivedFromSpecimen().toString()));
    if (request.getLinkedDigitalObjectPid() != null) {
      fdoRecord.add(
          new FdoAttribute(LINKED_DO_PID, CREATED, request.getLinkedDigitalObjectPid()));
    }
    if (request.getLinkedDigitalObjectType() != null) {
      fdoRecord.add(new FdoAttribute(LINKED_DO_TYPE, CREATED,
          request.getLinkedDigitalObjectType().toString()));
    }
    if (request.getLinkedAttribute() != null) {
      fdoRecord.add(new FdoAttribute(LINKED_ATTRIBUTE, CREATED, request.getLinkedAttribute()));
    }
    if (request.getPrimaryMediaId() != null) {
      fdoRecord.add(new FdoAttribute(PRIMARY_MEDIA_ID, CREATED, request.getPrimaryMediaId()));
    }
    if (request.getPrimaryMediaObjectIdType() != null) {
      fdoRecord.add(new FdoAttribute(PRIMARY_MO_ID_TYPE, CREATED,
          request.getPrimaryMediaObjectIdType().toString()));
    }
    if (request.getPrimaryMediaObjectIdName() != null) {
      fdoRecord.add(
          new FdoAttribute(PRIMARY_MO_ID_NAME, CREATED, request.getPrimaryMediaObjectIdName()));
    }
    if (request.getDcTermsType() != null) {
      fdoRecord.add(new FdoAttribute(DCTERMS_TYPE, CREATED, request.getDcTermsType().toString()));
    }
    if (request.getDctermsSubject() != null) {
      fdoRecord.add(new FdoAttribute(DCTERMS_SUBJECT, CREATED, request.getDctermsSubject()));
    }
    if (request.getDerivedFromEntity() != null) {
      fdoRecord.add(
          new FdoAttribute(DERIVED_FROM_ENTITY, CREATED, request.getDerivedFromEntity()));
    }
    if (request.getLicenseName() != null) {
      fdoRecord.add(new FdoAttribute(LICENSE_NAME, CREATED, request.getLicenseName()));
    }
    if (request.getLicenseUrl() != null) {
      fdoRecord.add(new FdoAttribute(LICENSE_URL, CREATED, request.getLicenseUrl()));
    }
    if (request.getRightsholderName() != null) {
      fdoRecord.add(new FdoAttribute(RIGHTSHOLDER_NAME, CREATED, request.getRightsholderName()));
    }
    if (request.getRightsholderPid() != null) {
      fdoRecord.add(new FdoAttribute(RIGHTSHOLDER_PID, CREATED, request.getRightsholderPid()));
    }
    if (request.getRightsholderPidType() != null) {
      fdoRecord.add(new FdoAttribute(RIGHTSHOLDER_PID_TYPE, CREATED,
          request.getRightsholderPidType().toString()));
    }
    if (request.getDctermsConformsTo() != null) {
      fdoRecord.add(new FdoAttribute(DC_TERMS_CONFORMS, CREATED, request.getDctermsConformsTo()));
    }
    return fdoRecord;
  }

  public static FdoRecord givenAnnotationFdoRecord(String handle, boolean includeHash)
      throws Exception {
    return new FdoRecord(handle, FdoType.ANNOTATION, genAnnotationAttributes(handle, includeHash),
        null);
  }

  public static List<FdoAttribute> genAnnotationAttributes(String handle, boolean includeHash)
      throws Exception {
    var fdoRecord = genHandleRecordAttributes(handle, FdoType.ANNOTATION);

    // 500 TargetPid
    fdoRecord.add(new FdoAttribute(TARGET_PID, CREATED, TARGET_DOI_TESTVAL));

    // 501 TargetType
    fdoRecord.add(new FdoAttribute(TARGET_TYPE, CREATED, TARGET_TYPE_TESTVAL));

    // 502 motivation
    fdoRecord.add(new FdoAttribute(MOTIVATION, CREATED, MOTIVATION_TESTVAL.toString()));

    // 503 AnnotationHash
    if (includeHash) {
      fdoRecord.add(
          new FdoAttribute(ANNOTATION_HASH, CREATED, ANNOTATION_HASH_TESTVAL.toString()));
    }
    return fdoRecord;
  }

  public static List<FdoAttribute> genMasAttributes(String handle) throws Exception {
    var fdoRecord = genHandleRecordAttributes(handle, FdoType.MAS);
    fdoRecord.add(new FdoAttribute(MAS_NAME, CREATED, (MAS_NAME_TESTVAL)));
    return fdoRecord;
  }

  public static List<FdoAttribute> genMappingAttributes(String handle) throws Exception {
    var fdoRecord = genHandleRecordAttributes(handle, FdoType.MAPPING);

    // 500 subjectDigitalObjectId
    fdoRecord.add(
        new FdoAttribute(SOURCE_DATA_STANDARD, CREATED,
            SOURCE_DATA_STANDARD_TESTVAL));

    return fdoRecord;
  }

  public static List<FdoAttribute> genSourceSystemAttributes(String handle) throws Exception {
    var fdoRecord = genHandleRecordAttributes(handle, FdoType.SOURCE_SYSTEM);

    // 600 hostInstitution
    fdoRecord.add(new FdoAttribute(SOURCE_SYSTEM_NAME, CREATED,
        SPECIMEN_HOST_TESTVAL));

    return fdoRecord;
  }

  public static List<FdoAttribute> genOrganisationAttributes(String handle,
      OrganisationRequest request) throws Exception {
    var fdoRecord = genDoiRecordAttributes(handle, FdoType.ORGANISATION, request);

    // 800 OrganisationIdentifier
    fdoRecord.add(new FdoAttribute(ORGANISATION_ID, CREATED,
        SPECIMEN_HOST_TESTVAL));

    // 801 OrganisationIdentifier
    fdoRecord.add(
        new FdoAttribute(ORGANISATION_ID_TYPE, CREATED,
            PTR_TYPE_DOI));

    // 802 OrganisationName
    fdoRecord.add(new FdoAttribute(ORGANISATION_NAME, CREATED,
        SPECIMEN_HOST_NAME_TESTVAL));

    return fdoRecord;
  }

  public static List<FdoAttribute> genOrganisationAttributes(String handle) throws Exception {
    return genOrganisationAttributes(handle, givenOrganisationRequestObject());
  }

  public static <T extends HandleRecordRequest> ObjectNode genCreateRecordRequest(T request,
      FdoType fdoType) {
    ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    ObjectNode rootNode = mapper.createObjectNode();
    ObjectNode dataNode = mapper.createObjectNode();
    ObjectNode attributeNode = mapper.valueToTree(request);

    if (attributeNode.has("referent")) {
      attributeNode.remove("referent");
    }

    dataNode.put("type", fdoType.getDigitalObjectType());
    dataNode.set("attributes", attributeNode);
    rootNode.set("data", dataNode);

    return rootNode;
  }

  // Single Requests
  public static HandleRecordRequest givenHandleRecordRequestObject() {
    return new HandleRecordRequest(ISSUED_FOR_AGENT_TESTVAL, PID_ISSUER_TESTVAL_OTHER,
        STRUCTURAL_TYPE_TESTVAL, LOC_TESTVAL);
  }

  public static DoiRecordRequest givenDoiRecordRequestObject() {
    return new DoiRecordRequest(ISSUED_FOR_AGENT_TESTVAL, PID_ISSUER_TESTVAL_OTHER,
        STRUCTURAL_TYPE_TESTVAL, LOC_TESTVAL, REFERENT_NAME_TESTVAL,
        FdoType.MEDIA_OBJECT.getDigitalObjectName(), PRIMARY_REFERENT_TYPE_TESTVAL);
  }

  public static DigitalSpecimenRequest givenDigitalSpecimenRequestObjectNullOptionals() {
    return givenDigitalSpecimenRequestObjectNullOptionals(PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL);
  }

  public static DigitalSpecimenRequest givenDigitalSpecimenRequestObjectNullOptionals(
      String primarySpecimenObjectId) {
    try {
      return new DigitalSpecimenRequest(ISSUED_FOR_AGENT_TESTVAL, PID_ISSUER_TESTVAL_OTHER,
          LOC_TESTVAL, REFERENT_NAME_TESTVAL, PRIMARY_REFERENT_TYPE_TESTVAL, SPECIMEN_HOST_TESTVAL,
          SPECIMEN_HOST_NAME_TESTVAL, primarySpecimenObjectId, PrimarySpecimenObjectIdType.GLOBAL,
          null, primarySpecimenObjectId, null, null, null, null, null, null, null, null, null, null,
          null, null, null, null);
    } catch (InvalidRequestException e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  public static MediaObjectRequest givenMediaRequestObject() throws InvalidRequestException {
    return new MediaObjectRequest(ISSUED_FOR_AGENT_TESTVAL, PID_ISSUER_TESTVAL_OTHER, LOC_TESTVAL,
        REFERENT_NAME_TESTVAL, PRIMARY_REFERENT_TYPE_TESTVAL, MEDIA_HOST_TESTVAL,
        MEDIA_HOST_NAME_TESTVAL, null, Boolean.TRUE, LINKED_DO_PID_TESTVAL,
        LINKED_DIGITAL_OBJECT_TYPE_TESTVAL, null, PRIMARY_MEDIA_ID_TESTVAL, null, null, null, null,
        null, null, null,
        SPECIMEN_HOST_TESTVAL, SPECIMEN_HOST_NAME_TESTVAL, null, null);
  }

  public static AnnotationRequest givenAnnotationRequestObject() {
    return new AnnotationRequest(ISSUED_FOR_AGENT_TESTVAL, PID_ISSUER_TESTVAL_OTHER, LOC_TESTVAL,
        TARGET_DOI_TESTVAL, TARGET_TYPE_TESTVAL, MOTIVATION_TESTVAL, ANNOTATION_HASH_TESTVAL);
  }

  public static AnnotationRequest givenAnnotationRequestObjectNoHash() {
    return new AnnotationRequest(ISSUED_FOR_AGENT_TESTVAL, PID_ISSUER_TESTVAL_OTHER, LOC_TESTVAL,
        TARGET_DOI_TESTVAL, TARGET_TYPE_TESTVAL, MOTIVATION_TESTVAL, null);
  }

  public static MappingRequest givenMappingRequestObject() {
    return new MappingRequest(ISSUED_FOR_AGENT_TESTVAL, PID_ISSUER_TESTVAL_OTHER, LOC_TESTVAL,
        SOURCE_DATA_STANDARD_TESTVAL);
  }

  public static SourceSystemRequest givenSourceSystemRequestObject() {
    return new SourceSystemRequest(ISSUED_FOR_AGENT_TESTVAL, PID_ISSUER_TESTVAL_OTHER, LOC_TESTVAL,
        SPECIMEN_HOST_TESTVAL);
  }

  public static OrganisationRequest givenOrganisationRequestObject() {
    return new OrganisationRequest(ISSUED_FOR_AGENT_TESTVAL, PID_ISSUER_TESTVAL_OTHER, LOC_TESTVAL,
        REFERENT_NAME_TESTVAL, PRIMARY_REFERENT_TYPE_TESTVAL, SPECIMEN_HOST_TESTVAL, PTR_TYPE_DOI);
  }

  public static MasRequest givenMasRecordRequestObject() {
    return new MasRequest(ISSUED_FOR_AGENT_TESTVAL, PID_ISSUER_TESTVAL_OTHER, LOC_TESTVAL,
        MAS_NAME_TESTVAL);
  }


  public static TombstoneRecordRequest genTombstoneRecordRequestObject() {
    return new TombstoneRecordRequest(TOMBSTONE_TEXT_TESTVAL);
  }

  public static JsonApiWrapperRead givenRecordResponseRead(List<String> handles, String path,
      FdoType recordType) throws Exception {
    List<JsonApiDataLinks> dataNodes = new ArrayList<>();

    for (String handle : handles) {
      var testDbRecord = genAttributes(recordType, handle);
      JsonNode recordAttributes = genObjectNodeAttributeRecord(testDbRecord);
      var pidLink = new JsonApiLinks(HANDLE_DOMAIN + handle);
      dataNodes.add(new JsonApiDataLinks(handle,
          recordType.getDigitalObjectType(), recordAttributes, pidLink));
    }

    var responseLink = new JsonApiLinks(path);
    return new JsonApiWrapperRead(responseLink, dataNodes);
  }

  public static JsonApiWrapperReadSingle givenRecordResponseReadSingle(String handle, String path,
      FdoType type, JsonNode attributes) {
    return new JsonApiWrapperReadSingle(new JsonApiLinks(path),
        new JsonApiDataLinks(handle, type.getDigitalObjectType(), attributes,
            new JsonApiLinks("https://hdl.handle.net/" + handle)));
  }

  public static JsonApiWrapperWrite givenRecordResponseWrite(List<String> handles,
      FdoType recordType) throws Exception {
    List<JsonApiDataLinks> dataNodes = new ArrayList<>();

    for (var handle : handles) {
      var testDbRecord = genAttributes(recordType, handle);
      JsonNode recordAttributes = genObjectNodeAttributeRecord(testDbRecord);

      var pidLink = new JsonApiLinks(HANDLE_DOMAIN + handle);
      dataNodes.add(
          new JsonApiDataLinks(handle, recordType.getDigitalObjectType(), recordAttributes,
              pidLink));
    }
    return new JsonApiWrapperWrite(dataNodes);
  }

  public static JsonApiWrapperWrite givenAnnotationResponseWrite(List<String> handles) {
    List<JsonApiDataLinks> dataNodes = new ArrayList<>();

    for (var handle : handles) {
      var testDbRecord = List.of(
          new FdoAttribute(ANNOTATION_HASH, CREATED, ANNOTATION_HASH_TESTVAL.toString()));
      JsonNode recordAttributes = genObjectNodeAttributeRecord(testDbRecord);

      var pidLink = new JsonApiLinks(HANDLE_DOMAIN + handle);
      dataNodes.add(
          new JsonApiDataLinks(handle, FdoType.ANNOTATION.getDigitalObjectType(), recordAttributes,
              pidLink));
    }
    return new JsonApiWrapperWrite(dataNodes);
  }

  public static JsonApiWrapperWrite givenRecordResponseWriteSmallResponse(
      List<FdoRecord> fdoRecords, FdoType fdoType) {
    List<JsonApiDataLinks> dataNodes = new ArrayList<>();
    List<FdoAttribute> fdoSublist;
    for (var fdoRecord : fdoRecords) {
      switch (fdoType) {
        case ANNOTATION -> {
          fdoSublist = List.of(getField(fdoRecord.attributes(), ANNOTATION_HASH));
        }
        case DIGITAL_SPECIMEN -> {
          fdoSublist = List.of(getField(fdoRecord.attributes(), NORMALISED_SPECIMEN_OBJECT_ID));
        }
        case MEDIA_OBJECT -> {
          fdoSublist = List.of(getField(fdoRecord.attributes(), PRIMARY_MEDIA_ID),
              getField(fdoRecord.attributes(), LINKED_DO_PID));
        }
        default -> {
          fdoSublist = fdoRecord.attributes();
        }
      }
      var recordAttributes = genObjectNodeAttributeRecord(fdoSublist);
      var pidLink = new JsonApiLinks(HANDLE_DOMAIN + fdoRecord.handle());
      dataNodes.add(
          new JsonApiDataLinks(fdoRecord.handle(), fdoType.getDigitalObjectType(), recordAttributes,
              pidLink));
    }
    return new JsonApiWrapperWrite(dataNodes);
  }

  public static FdoAttribute getField(List<FdoAttribute> fdoAttributes, FdoProfile targetField) {
    for (var attribute : fdoAttributes) {
      if (attribute.getIndex() == targetField.index()) {
        return attribute;
      }
    }
    log.error("Unable to find field {} in record {}", targetField, fdoAttributes);
    throw new IllegalStateException();
  }


  public static JsonApiWrapperWrite givenRecordResponseWriteGeneric(List<String> handles,
      FdoType fdoType) throws Exception {
    List<JsonApiDataLinks> dataNodes = new ArrayList<>();

    for (var handle : handles) {
      var testDbRecord = genAttributes(fdoType, handle);
      JsonNode recordAttributes = genObjectNodeAttributeRecord(testDbRecord);

      var pidLink = new JsonApiLinks(HANDLE_DOMAIN + handle);
      dataNodes.add(
          new JsonApiDataLinks(handle, "PID", recordAttributes,
              pidLink));
    }
    return new JsonApiWrapperWrite(dataNodes);
  }

  public static JsonApiWrapperWrite givenRecordResponseWrite(List<String> handles,
      FdoType attributeType, String recordType) throws Exception {
    List<JsonApiDataLinks> dataNodes = new ArrayList<>();

    for (var handle : handles) {
      var testDbRecord = genAttributes(attributeType, handle);
      JsonNode recordAttributes = genObjectNodeAttributeRecord(testDbRecord);

      var pidLink = new JsonApiLinks(HANDLE_DOMAIN + handle);
      dataNodes.add(new JsonApiDataLinks(handle, recordType,
          recordAttributes, pidLink));
    }
    return new JsonApiWrapperWrite(dataNodes);
  }

  public static JsonApiWrapperWrite givenRecordResponseWriteAltLoc(List<String> handles)
      throws Exception {
    return givenRecordResponseWriteAltLoc(handles, FdoType.HANDLE);
  }

  public static JsonApiWrapperWrite givenRecordResponseWriteAltLoc(List<String> handles,
      FdoType recordType) throws Exception {
    List<JsonApiDataLinks> dataNodes = new ArrayList<>();

    for (var handle : handles) {
      var testDbRecord = genUpdateRecordAttributesAltLoc(handle);
      JsonNode recordAttributes = genObjectNodeAttributeRecord(testDbRecord);

      var pidLink = new JsonApiLinks(HANDLE_DOMAIN + handle);
      dataNodes.add(new JsonApiDataLinks(handle,
          recordType.getDigitalObjectType(), recordAttributes, pidLink));
    }
    return new JsonApiWrapperWrite(dataNodes);
  }

  public static JsonApiWrapperWrite givenRecordResponseNullAttributes(List<String> handles) {
    return givenRecordResponseNullAttributes(handles, FdoType.HANDLE);
  }

  public static JsonApiWrapperWrite givenRecordResponseNullAttributes(List<String> handles,
      FdoType type) {
    List<JsonApiDataLinks> dataNodes = new ArrayList<>();
    for (var handle : handles) {
      var pidLink = new JsonApiLinks(HANDLE_DOMAIN + handle);
      dataNodes.add(new JsonApiDataLinks(handle,
          type.getDigitalObjectType(), null, pidLink));
    }
    return new JsonApiWrapperWrite(dataNodes);
  }


  public static JsonApiWrapperWrite givenRecordResponseWriteArchive(List<String> handles)
      throws Exception {
    List<JsonApiDataLinks> dataNodes = new ArrayList<>();

    for (var handle : handles) {
      var testDbRecord = genTombstoneRecordRequestAttributes(handle);
      JsonNode recordAttributes = genObjectNodeAttributeRecord(testDbRecord);

      var pidLink = new JsonApiLinks(HANDLE_DOMAIN + handle);
      dataNodes.add(new JsonApiDataLinks(handle,
          FdoType.TOMBSTONE.getDigitalObjectType(), recordAttributes, pidLink));
    }
    return new JsonApiWrapperWrite(dataNodes);
  }

  public static List<FdoAttribute> genAttributes(FdoType recordType, String handle)
      throws Exception {
    switch (recordType) {
      case DOI -> {
        return genDoiRecordAttributes(handle, recordType);
      }
      case DIGITAL_SPECIMEN -> {
        return genDigitalSpecimenAttributes(handle);
      }
      case MEDIA_OBJECT -> {
        return genMediaObjectAttributes(handle);
      }
      case ANNOTATION -> {
        return genAnnotationAttributes(handle, false);
      }
      case MAPPING -> {
        return genMappingAttributes(handle);
      }
      case SOURCE_SYSTEM -> {
        return genSourceSystemAttributes(handle);
      }
      case ORGANISATION -> {
        return genOrganisationAttributes(handle);
      }
      case MAS -> {
        return genMasAttributes(handle);
      }
      default -> {
        log.warn("Default type");
        return genHandleRecordAttributes(handle, FdoType.HANDLE);
      }
    }
  }

  public static List<JsonNode> genUpdateRequestBatch(List<String> handles, FdoType type) {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode requestNodeRoot = mapper.createObjectNode();
    ObjectNode requestNodeData = mapper.createObjectNode();
    List<JsonNode> requestNodeList = new ArrayList<>();

    for (var handle : handles) {
      requestNodeData.put("type", type.getDigitalObjectType());
      requestNodeData.put("id", handle);
      requestNodeData.set("attributes", genUpdateRequestAltLoc());
      requestNodeRoot.set("data", requestNodeData);

      requestNodeList.add(requestNodeRoot.deepCopy());

      requestNodeData.removeAll();
      requestNodeRoot.removeAll();
    }
    return requestNodeList;
  }

  public static List<JsonNode> genUpdateRequestBatch(List<String> handles) {
    return genUpdateRequestBatch(handles, FdoType.HANDLE);
  }

  public static List<JsonNode> genTombstoneRequestBatch(List<String> handles) {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode requestNodeRoot = mapper.createObjectNode();
    ObjectNode requestNodeData = mapper.createObjectNode();
    List<JsonNode> requestNodeList = new ArrayList<>();

    for (String handle : handles) {
      requestNodeData.put(NODE_TYPE, FdoType.HANDLE.getDigitalObjectType());
      requestNodeData.put(NODE_ID, handle);
      requestNodeData.set(NODE_ATTRIBUTES, genTombstoneRequest());
      requestNodeRoot.set(NODE_DATA, requestNodeData);

      requestNodeList.add(requestNodeRoot.deepCopy());

      requestNodeData.removeAll();
      requestNodeRoot.removeAll();
    }

    return requestNodeList;
  }

  public static JsonNode genUpdateRequestAltLoc() {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode rootNode = mapper.createObjectNode();
    rootNode.putArray("locations").add(LOC_ALT_TESTVAL[0]);
    return rootNode;
  }

  public static JsonNode genTombstoneRequest() {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode rootNode = mapper.createObjectNode();
    rootNode.put(TOMBSTONE_TEXT.get(), TOMBSTONE_TEXT_TESTVAL);
    return rootNode;
  }

  // Handle Attributes as ObjectNode
  public static JsonNode genObjectNodeAttributeRecord(List<FdoAttribute> dbRecord) {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode rootNode = mapper.createObjectNode();
    for (var row : dbRecord) {
      if (row.getIndex() != HS_ADMIN.index()) {
        var rowData = row.getValue();
        try {
          var nodeData = mapper.readTree(rowData);
          rootNode.set(row.getType(), nodeData);
        } catch (JsonProcessingException ignored) {
          rootNode.put(row.getType(), rowData);
        }
      }
    }
    return rootNode;
  }

  // Other Functions
  public static FdoAttribute givenLandingPageAttribute(String handle) throws Exception {
    var landingPage = new String[]{"Placeholder landing page"};
    var locations = setLocations(landingPage, handle, FdoType.TOMBSTONE, false);
    return new FdoAttribute(LOC, CREATED, locations);
  }

  public static String setLocations(String[] userLocations, String handle, FdoType type,
      boolean isDoiProfileTest) throws TransformerException, ParserConfigurationException {
    DOC_BUILDER_FACTORY.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);

    DocumentBuilder documentBuilder = DOC_BUILDER_FACTORY.newDocumentBuilder();

    var doc = documentBuilder.newDocument();
    var locations = doc.createElement("locations");
    doc.appendChild(locations);
    String[] objectLocations =
        isDoiProfileTest ? userLocations : concatLocations(userLocations, handle, type);

    for (int i = 0; i < objectLocations.length; i++) {
      var locs = doc.createElement("location");
      locs.setAttribute("id", String.valueOf(i));
      locs.setAttribute("href", objectLocations[i]);
      String weight = i < 1 ? "1" : "0";
      locs.setAttribute("weight", weight);
      locations.appendChild(locs);
    }
    return documentToString(doc);
  }

  private static String[] concatLocations(String[] userLocations, String handle, FdoType type) {
    ArrayList<String> objectLocations = new ArrayList<>();
    objectLocations.addAll(List.of(defaultLocations(handle, type)));
    objectLocations.addAll(List.of(userLocations));
    return objectLocations.toArray(new String[0]);
  }

  private static String[] defaultLocations(String handle, FdoType type) {
    switch (type) {
      case DIGITAL_SPECIMEN -> {
        String api = API_URL + "/specimens/" + handle;
        String ui = UI_URL + "/ds/" + handle;
        return new String[]{ui, api};
      }
      case MAPPING -> {
        return new String[]{ORCHESTRATION_URL + "/mapping/" + handle};
      }
      case SOURCE_SYSTEM -> {
        return new String[]{ORCHESTRATION_URL + "/source-system/" + handle};
      }
      case MEDIA_OBJECT -> {
        String api = API_URL + "/digitalMedia/" + handle;
        String ui = UI_URL + "/dm/" + handle;
        return new String[]{ui, api};
      }
      case ANNOTATION -> {
        return new String[]{API_URL + "/annotations/" + handle};
      }
      case ORGANISATION -> {
        return new String[]{SPECIMEN_HOST_TESTVAL};
      }
      case MAS -> {
        return new String[]{ORCHESTRATION_URL + "/mas/" + handle};
      }
      default -> {
        // Handle, DOI, Organisation (organisation handled separately)
        return new String[]{};
      }
    }
  }

  private static String documentToString(Document document) throws TransformerException {
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

}
