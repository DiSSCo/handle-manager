package eu.dissco.core.handlemanager.testUtils;

import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.ANNOTATION_HASH;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.BASE_TYPE_OF_SPECIMEN;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.CATALOG_IDENTIFIER;
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
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.MEDIA_FORMAT;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.MEDIA_HOST;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.MEDIA_HOST_NAME;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.MEDIA_MIME_TYPE;
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
import eu.dissco.core.handlemanager.domain.fdo.DataMappingRequest;
import eu.dissco.core.handlemanager.domain.fdo.DigitalMediaRequest;
import eu.dissco.core.handlemanager.domain.fdo.DigitalSpecimenRequest;
import eu.dissco.core.handlemanager.domain.fdo.DoiRecordRequest;
import eu.dissco.core.handlemanager.domain.fdo.FdoType;
import eu.dissco.core.handlemanager.domain.fdo.HandleRecordRequest;
import eu.dissco.core.handlemanager.domain.fdo.MasRequest;
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
import eu.dissco.core.handlemanager.domain.repsitoryobjects.HandleAttribute;
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
  public final static String HANDLE_URI = "https://hdl.handle.net/";
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
  public static final String[] LOC_TESTVAL = {"https://sandbox.dissco.tech/",
      "https://dissco.eu"};
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
  public static final String TARGET_DOI_TESTVAL = HANDLE_URI + PREFIX + "/111";
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
  public static final String DIGITAL_OBJECT_NAME_TESTVAL = "digitalSpecimen";

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

  public static List<HandleAttribute> genHandleRecordAttributes(byte[] handle) throws Exception {
    return genHandleRecordAttributes(handle, FdoType.HANDLE);
  }

  public static List<HandleAttribute> genHandleRecordAttributes(byte[] handle, FdoType type)
      throws Exception {

    List<HandleAttribute> fdoRecord = new ArrayList<>();
    var request = givenHandleRecordRequestObject();
    byte[] loc = setLocations(request.getLocations(),
        new String(handle, StandardCharsets.UTF_8),
        type, false);
    fdoRecord.add(new HandleAttribute(LOC.index(), handle, LOC.get(), loc));

    // 1: FDO Profile
    fdoRecord.add(new HandleAttribute(FDO_PROFILE.index(), handle, FDO_PROFILE.get(),
        type.getFdoProfile().getBytes(StandardCharsets.UTF_8)));

    // 2: FDO Record License
    byte[] pidKernelMetadataLicense = "https://creativecommons.org/publicdomain/zero/1.0/".getBytes(
        StandardCharsets.UTF_8);
    fdoRecord.add(new HandleAttribute(FDO_RECORD_LICENSE.index(), handle,
        FDO_RECORD_LICENSE.get(), pidKernelMetadataLicense));

    // 3: DigitalObjectType
    fdoRecord.add(
        new HandleAttribute(DIGITAL_OBJECT_TYPE, handle,
            type.getDigitalObjectType()));

    // 4: DigitalObjectName
    fdoRecord.add(
        new HandleAttribute(DIGITAL_OBJECT_NAME, handle, type.getDigitalObjectName()));

    // 5: Pid
    byte[] pid = ("https://hdl.handle.net/" + new String(handle,
        StandardCharsets.UTF_8)).getBytes(
        StandardCharsets.UTF_8);
    fdoRecord.add(new HandleAttribute(PID.index(), handle, PID.get(), pid));

    // 6: PidIssuer
    fdoRecord.add(new HandleAttribute(PID_ISSUER.index(), handle, PID_ISSUER.get(),
        request.getPidIssuer().getBytes(StandardCharsets.UTF_8)));

    // 7: pidIssuerName
    fdoRecord.add(new HandleAttribute(PID_ISSUER_NAME.index(), handle, PID_ISSUER_NAME.get(),
        PID_ISSUER_TESTVAL_OTHER.getBytes(StandardCharsets.UTF_8)));

    // 8: issuedForAgent
    fdoRecord.add(new HandleAttribute(ISSUED_FOR_AGENT.index(), handle, ISSUED_FOR_AGENT.get(),
        request.getIssuedForAgent().getBytes(StandardCharsets.UTF_8)));

    // 9: issuedForAgentName
    fdoRecord.add(
        new HandleAttribute(ISSUED_FOR_AGENT_NAME.index(), handle,
            ISSUED_FOR_AGENT_NAME.get(),
            ISSUED_FOR_AGENT_TESTVAL.getBytes(StandardCharsets.UTF_8)));

    // 10: pidRecordIssueDate
    fdoRecord.add(new HandleAttribute(PID_RECORD_ISSUE_DATE.index(), handle,
        PID_RECORD_ISSUE_DATE.get(), ISSUE_DATE_TESTVAL.getBytes(StandardCharsets.UTF_8)));

    // 11: pidRecordIssueNumber
    fdoRecord.add(new HandleAttribute(PID_RECORD_ISSUE_NUMBER.index(), handle,
        PID_RECORD_ISSUE_NUMBER.get(), "1".getBytes(StandardCharsets.UTF_8)));

    // 12: structuralType
    fdoRecord.add(new HandleAttribute(STRUCTURAL_TYPE.index(), handle,
        STRUCTURAL_TYPE.get(),
        STRUCTURAL_TYPE_TESTVAL.toString().getBytes(StandardCharsets.UTF_8)));

    // 13: PidStatus
    fdoRecord.add(new HandleAttribute(PID_STATUS.index(), handle, PID_STATUS.get(),
        "TEST".getBytes(StandardCharsets.UTF_8)));

    return fdoRecord;
  }

  public static List<HandleAttribute> genHandleRecordAttributesAltLoc(byte[] handle)
      throws Exception {
    List<HandleAttribute> attributes = genHandleRecordAttributes(handle, FdoType.HANDLE);

    byte[] locOriginal = setLocations(LOC_TESTVAL, new String(handle, StandardCharsets.UTF_8),
        FdoType.HANDLE, false);
    var locOriginalAttr = new HandleAttribute(LOC.index(), handle, LOC.get(), locOriginal);

    byte[] locAlt = setLocations(LOC_ALT_TESTVAL, new String(handle, StandardCharsets.UTF_8),
        FdoType.HANDLE, false);
    var locAltAttr = new HandleAttribute(LOC.index(), handle, LOC.get(), locAlt);

    attributes.set(attributes.indexOf(locOriginalAttr), locAltAttr);

    return attributes;
  }

  public static List<HandleAttribute> genTombstoneRecordFullAttributes(byte[] handle)
      throws Exception {
    List<HandleAttribute> attributes = genHandleRecordAttributes(handle, FdoType.TOMBSTONE);
    HandleAttribute oldPidStatus = new HandleAttribute(PID_STATUS.index(), handle,
        PID_STATUS.get(), PID_STATUS_TESTVAL.getBytes(StandardCharsets.UTF_8));
    attributes.addAll(genHandleRecordAttributes(handle, FdoType.TOMBSTONE));
    attributes.remove(oldPidStatus);
    attributes = new ArrayList<>(
        (attributes.stream().filter(row -> row.getIndex() != LOC.index())).toList());
    attributes.add(givenLandingPageAttribute(handle));
    return attributes;
  }

  public static List<HandleAttribute> genUpdateRecordAttributesAltLoc(byte[] handle)
      throws ParserConfigurationException, TransformerException {
    byte[] locAlt = setLocations(LOC_ALT_TESTVAL, new String(handle, StandardCharsets.UTF_8),
        FdoType.HANDLE, false);
    return List.of(new HandleAttribute(LOC.index(), handle, LOC.get(), locAlt));
  }

  public static List<HandleAttribute> genTombstoneRecordRequestAttributes(byte[] handle)
      throws Exception {
    List<HandleAttribute> tombstoneAttributes = new ArrayList<>();
    tombstoneAttributes.add(
        new HandleAttribute(TOMBSTONE_TEXT.index(), handle, TOMBSTONE_TEXT.get(),
            TOMBSTONE_TEXT_TESTVAL.getBytes(StandardCharsets.UTF_8)));
    tombstoneAttributes.add(new HandleAttribute(PID_STATUS.index(), handle, PID_STATUS.get(),
        "ARCHIVED".getBytes(StandardCharsets.UTF_8)));
    tombstoneAttributes.add(givenLandingPageAttribute(handle));
    return tombstoneAttributes;
  }

  public static List<HandleAttribute> genDoiRecordAttributes(byte[] handle, FdoType type)
      throws Exception {
    return genDoiRecordAttributes(handle, type, givenDoiRecordRequestObject());
  }

  public static List<HandleAttribute> genDoiRecordAttributes(byte[] handle, FdoType type,
      DoiRecordRequest request)
      throws Exception {
    List<HandleAttribute> fdoRecord = genHandleRecordAttributes(handle, type);

    // 40: referentType
    fdoRecord.add(
        new HandleAttribute(REFERENT_TYPE.index(), handle, REFERENT_TYPE.get(),
            request.getReferentType().getBytes(StandardCharsets.UTF_8)));

    // 41: referentDoiName
    fdoRecord.add(
        new HandleAttribute(REFERENT_DOI_NAME.index(), handle, REFERENT_DOI_NAME.get(),
            REFERENT_DOI_NAME_TESTVAL.getBytes(StandardCharsets.UTF_8)));

    // 42: referentName
    fdoRecord.add(
        new HandleAttribute(REFERENT_NAME.index(), handle, REFERENT_NAME.get(),
            request.getReferentName().getBytes(StandardCharsets.UTF_8)));

    // 43: primaryReferentType
    fdoRecord.add(
        new HandleAttribute(PRIMARY_REFERENT_TYPE.index(), handle,
            PRIMARY_REFERENT_TYPE.get(),
            request.getPrimaryReferentType().getBytes(StandardCharsets.UTF_8)));

    return fdoRecord;
  }

  public static List<HandleAttribute> genDigitalSpecimenAttributes(byte[] handle,
      DigitalSpecimenRequest request) throws Exception {
    List<HandleAttribute> fdoRecord = genDoiRecordAttributes(handle, FdoType.DIGITAL_SPECIMEN,
        request);
    // 200: Specimen Host
    fdoRecord.add(
        new HandleAttribute(SPECIMEN_HOST.index(), handle,
            SPECIMEN_HOST.get(),
            request.getSpecimenHost().getBytes(StandardCharsets.UTF_8)));

    // 201: Specimen Host name
    fdoRecord.add(
        new HandleAttribute(SPECIMEN_HOST_NAME.index(), handle,
            SPECIMEN_HOST_NAME.get(),
            SPECIMEN_HOST_NAME_TESTVAL.getBytes(StandardCharsets.UTF_8)));

    // 202: primarySpecimenObjectId
    fdoRecord.add(
        new HandleAttribute(PRIMARY_SPECIMEN_OBJECT_ID.index(), handle,
            PRIMARY_SPECIMEN_OBJECT_ID.get(),
            request.getPrimarySpecimenObjectId().getBytes(StandardCharsets.UTF_8)));

    // 203: primarySpecimenObjectIdType
    fdoRecord.add(
        new HandleAttribute(PRIMARY_SPECIMEN_OBJECT_ID_TYPE.index(), handle,
            PRIMARY_SPECIMEN_OBJECT_ID_TYPE.get(),
            request.getPrimarySpecimenObjectIdType().getBytes()));

    // 204-217 are optional

    // 204: primarySpecimenObjectIdName
    if (request.getPrimarySpecimenObjectIdName() != null) {
      fdoRecord.add(
          new HandleAttribute(PRIMARY_SPECIMEN_OBJECT_ID_NAME.index(), handle,
              PRIMARY_SPECIMEN_OBJECT_ID_NAME.get(),
              request.getPrimarySpecimenObjectIdName()
                  .getBytes(StandardCharsets.UTF_8)));
    }

    // 205: normalisedSpecimenObjectId
    fdoRecord.add(
        new HandleAttribute(NORMALISED_SPECIMEN_OBJECT_ID, handle,
            request.getNormalisedPrimarySpecimenObjectId()));

    // 206: specimenObjectIdAbsenceReason
    if (request.getPrimarySpecimenObjectIdAbsenceReason() != null) {
      fdoRecord.add(
          new HandleAttribute(SPECIMEN_OBJECT_ID_ABSENCE_REASON.index(), handle,
              SPECIMEN_OBJECT_ID_ABSENCE_REASON.get(),
              request.getPrimarySpecimenObjectIdAbsenceReason()
                  .getBytes(StandardCharsets.UTF_8)));
    }

    // 207: otherSpecimenIds
    if (request.getOtherSpecimenIds() != null && !request.getOtherSpecimenIds().isEmpty()) {
      var otherSpecimenIds = MAPPER.writeValueAsString(request.getOtherSpecimenIds());
      fdoRecord.add(new HandleAttribute(OTHER_SPECIMEN_IDS, handle, otherSpecimenIds));
    }

    // 208: topicOrigin
    if (request.getTopicOrigin() != null) {
      fdoRecord.add(
          new HandleAttribute(TOPIC_ORIGIN.index(), handle,
              TOPIC_ORIGIN.get(),
              request.getTopicOrigin().toString().getBytes(StandardCharsets.UTF_8)));
    }

    // 209: topicDomain
    if (request.getTopicDomain() != null) {
      fdoRecord.add(
          new HandleAttribute(TOPIC_DOMAIN.index(), handle,
              TOPIC_DOMAIN.get(),
              request.getTopicDomain().toString().getBytes(StandardCharsets.UTF_8)));
    }

    // 210: topicDiscipline
    if (request.getTopicDiscipline() != null) {
      fdoRecord.add(
          new HandleAttribute(TOPIC_DISCIPLINE.index(), handle,
              TOPIC_DISCIPLINE.get(),
              request.getTopicDiscipline().toString()
                  .getBytes(StandardCharsets.UTF_8)));
    }

    // 211: topicCategory
    if (request.getTopicCategory() != null) {
      fdoRecord.add(
          new HandleAttribute(TOPIC_CATEGORY, handle,
              request.getTopicCategory().toString()));
    }

    // 212: livingOrPreserved
    if (request.getLivingOrPreserved() != null) {
      fdoRecord.add(
          new HandleAttribute(LIVING_OR_PRESERVED.index(), handle,
              LIVING_OR_PRESERVED.get(),
              request.getLivingOrPreserved().getBytes()));
    }

    // 213: baseTypeOfSpecimen
    if (request.getBaseTypeOfSpecimen() != null) {
      fdoRecord.add(
          new HandleAttribute(BASE_TYPE_OF_SPECIMEN.index(), handle,
              BASE_TYPE_OF_SPECIMEN.get(),
              request.getBaseTypeOfSpecimen().toString()
                  .getBytes(StandardCharsets.UTF_8)));
    }

    // 214: informationArtefactType
    if (request.getInformationArtefactType() != null) {
      fdoRecord.add(
          new HandleAttribute(INFORMATION_ARTEFACT_TYPE.index(), handle,
              INFORMATION_ARTEFACT_TYPE.get(),
              request.getInformationArtefactType().toString()
                  .getBytes(StandardCharsets.UTF_8)));
    }

    // 215: materialSampleType
    if (request.getMaterialSampleType() != null) {
      fdoRecord.add(
          new HandleAttribute(MATERIAL_SAMPLE_TYPE.index(), handle,
              MATERIAL_SAMPLE_TYPE.get(),
              request.getMaterialSampleType().toString()
                  .getBytes(StandardCharsets.UTF_8)));
    }

    // 216: materialOrDigitalEntity
    if (request.getMaterialSampleType() != null) {
      fdoRecord.add(
          new HandleAttribute(MATERIAL_OR_DIGITAL_ENTITY, handle,
              request.getMaterialOrDigitalEntity().toString()));
    }

    // 217: markedAsType
    if (request.getMarkedAsType() != null) {
      fdoRecord.add(
          new HandleAttribute(MARKED_AS_TYPE.index(), handle,
              MARKED_AS_TYPE.get(),
              request.getMarkedAsType().toString().getBytes(StandardCharsets.UTF_8)));
    }

    // 218: wasDerivedFromEntity
    if (request.getDerivedFromEntity() != null) {
      fdoRecord.add(
          new HandleAttribute(WAS_DERIVED_FROM_ENTITY.index(), handle,
              WAS_DERIVED_FROM_ENTITY.get(),
              request.getDerivedFromEntity().getBytes(StandardCharsets.UTF_8)));
    }
    var catId = request.getCatalogIdentifier();
    if (catId != null) {
      fdoRecord.add(new HandleAttribute(CATALOG_IDENTIFIER, handle, catId));
    }

    return fdoRecord;

  }

  public static List<HandleAttribute> genDigitalSpecimenAttributes(byte[] handle)
      throws Exception {
    var request = givenDigitalSpecimenRequestObjectNullOptionals();
    return genDigitalSpecimenAttributes(handle, request);
  }

  public static List<HandleAttribute> genDigitalMediaAttributes(byte[] handle)
      throws Exception {
    var request = givenMediaRequestObject();
    return genDigitalMediaAttributes(handle, request);
  }

  public static List<HandleAttribute> genDigitalMediaAttributes(byte[] handle,
      DigitalMediaRequest request) throws Exception {
    List<HandleAttribute> fdoRecord = genDoiRecordAttributes(handle, FdoType.DIGITAL_MEDIA);
    fdoRecord.add(new HandleAttribute(MEDIA_HOST, handle, request.getMediaHost()));
    if (request.getMediaHostName() == null) {
      fdoRecord.add(new HandleAttribute(MEDIA_HOST_NAME, handle, MEDIA_HOST_NAME_TESTVAL));
    } else {
      fdoRecord.add(new HandleAttribute(MEDIA_HOST_NAME, handle, request.getMediaHostName()));
    }
    if (request.getMediaFormat() != null) {
      fdoRecord.add(
          new HandleAttribute(MEDIA_FORMAT, handle, request.getMediaFormat().toString()));
    }
    fdoRecord.add(new HandleAttribute(IS_DERIVED_FROM_SPECIMEN, handle,
        request.getIsDerivedFromSpecimen().toString()));
    if (request.getLinkedDigitalObjectPid() != null) {
      fdoRecord.add(
          new HandleAttribute(LINKED_DO_PID, handle,
              request.getLinkedDigitalObjectPid()));
    }
    if (request.getLinkedDigitalObjectType() != null) {
      fdoRecord.add(
          new HandleAttribute(LINKED_DO_TYPE, handle, request.getLinkedDigitalObjectType()
              .toString()));
    }
    if (request.getLinkedAttribute() != null) {
      fdoRecord.add(
          new HandleAttribute(LINKED_ATTRIBUTE, handle, request.getLinkedAttribute()));
    }
    if (request.getPrimaryMediaId() != null) {
      fdoRecord.add(
          new HandleAttribute(PRIMARY_MEDIA_ID, handle, request.getPrimaryMediaId()));
    }
    if (request.getPrimaryMediaObjectIdType() != null) {
      fdoRecord.add(
          new HandleAttribute(PRIMARY_MO_ID_TYPE, handle,
              request.getPrimaryMediaObjectIdType().toString()));
    }
    if (request.getPrimaryMediaObjectIdName() != null) {
      fdoRecord.add(
          new HandleAttribute(PRIMARY_MO_ID_NAME, handle,
              request.getPrimaryMediaObjectIdName()));
    }
    if (request.getDcTermsType() != null) {
      fdoRecord.add(
          new HandleAttribute(DCTERMS_TYPE, handle,
              request.getDcTermsType().toString()));
    }
    if (request.getMediaMimeType() != null) {
      fdoRecord.add(
          new HandleAttribute(MEDIA_MIME_TYPE, handle, request.getMediaMimeType()));
    }
    if (request.getDerivedFromEntity() != null) {
      fdoRecord.add(
          new HandleAttribute(DERIVED_FROM_ENTITY, handle,
              request.getDerivedFromEntity()));
    }
    if (request.getLicenseName() != null) {
      fdoRecord.add(
          new HandleAttribute(LICENSE_NAME, handle, request.getLicenseName()));
    }
    if (request.getLicense() != null) {
      fdoRecord.add(
          new HandleAttribute(LICENSE_URL, handle, request.getLicense()));
    }
    if (request.getRightsholderName() != null) {
      fdoRecord.add(
          new HandleAttribute(RIGHTSHOLDER_NAME, handle, request.getRightsholderName()));
    }
    if (request.getRightsholderPid() != null) {
      fdoRecord.add(
          new HandleAttribute(RIGHTSHOLDER_PID, handle, request.getRightsholderPid()));
    }
    if (request.getRightsholderPidType() != null) {
      fdoRecord.add(
          new HandleAttribute(RIGHTSHOLDER_PID_TYPE, handle,
              request.getRightsholderPidType().toString()));
    }
    if (request.getDctermsConformsTo() != null) {
      fdoRecord.add(
          new HandleAttribute(DC_TERMS_CONFORMS, handle, request.getDctermsConformsTo()));
    }

    return fdoRecord;
  }

  public static List<HandleAttribute> genAnnotationAttributes(byte[] handle, boolean includeHash)
      throws Exception {
    var fdoRecord = genHandleRecordAttributes(handle, FdoType.ANNOTATION);

    // 500 TargetPid
    fdoRecord.add(new HandleAttribute(TARGET_PID, handle, TARGET_DOI_TESTVAL));

    // 501 TargetType
    fdoRecord.add(new HandleAttribute(TARGET_TYPE, handle, TARGET_TYPE_TESTVAL));

    // 502 motivation
    fdoRecord.add(
        new HandleAttribute(MOTIVATION, handle, MOTIVATION_TESTVAL.toString()));

    // 503 AnnotationHash
    if (includeHash) {
      fdoRecord.add(
          new HandleAttribute(ANNOTATION_HASH, handle,
              ANNOTATION_HASH_TESTVAL.toString()));
    }
    return fdoRecord;
  }

  public static List<HandleAttribute> genMasAttributes(byte[] handle)
      throws Exception {
    var fdoRecord = genHandleRecordAttributes(handle, FdoType.MAS);

    fdoRecord.add(new HandleAttribute(MAS_NAME.index(), handle, MAS_NAME.get(),
        (MAS_NAME_TESTVAL).getBytes(StandardCharsets.UTF_8)));

    return fdoRecord;
  }

  public static List<HandleAttribute> genDataMappingAttributes(byte[] handle)
      throws Exception {
    var fdoRecord = genHandleRecordAttributes(handle, FdoType.DATA_MAPPING);

    // 500 subjectDigitalObjectId
    fdoRecord.add(new HandleAttribute(SOURCE_DATA_STANDARD.index(), handle,
        SOURCE_DATA_STANDARD.get(),
        SOURCE_DATA_STANDARD_TESTVAL.getBytes(StandardCharsets.UTF_8)));

    return fdoRecord;
  }

  public static List<HandleAttribute> genSourceSystemAttributes(byte[] handle)
      throws Exception {
    var fdoRecord = genHandleRecordAttributes(handle, FdoType.SOURCE_SYSTEM);

    // 600 hostInstitution
    fdoRecord.add(new HandleAttribute(SOURCE_SYSTEM_NAME.index(), handle,
        SOURCE_SYSTEM_NAME.get(), SPECIMEN_HOST_TESTVAL.getBytes(StandardCharsets.UTF_8)));

    return fdoRecord;
  }

  public static List<HandleAttribute> genOrganisationAttributes(byte[] handle,
      OrganisationRequest request)
      throws Exception {
    var fdoRecord = genDoiRecordAttributes(handle, FdoType.ORGANISATION, request);

    // 800 OrganisationIdentifier
    fdoRecord.add(new HandleAttribute(ORGANISATION_ID.index(), handle,
        ORGANISATION_ID.get(), SPECIMEN_HOST_TESTVAL.getBytes(StandardCharsets.UTF_8)));

    // 801 OrganisationIdentifier
    fdoRecord.add(new HandleAttribute(ORGANISATION_ID_TYPE.index(), handle,
        ORGANISATION_ID_TYPE.get(), PTR_TYPE_DOI.getBytes(StandardCharsets.UTF_8)));

    // 802 OrganisationName
    fdoRecord.add(
        new HandleAttribute(ORGANISATION_NAME.index(), handle, ORGANISATION_NAME.get(),
            SPECIMEN_HOST_NAME_TESTVAL.getBytes(
                StandardCharsets.UTF_8)));

    return fdoRecord;
  }

  public static List<HandleAttribute> genOrganisationAttributes(byte[] handle)
      throws Exception {
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
    return new HandleRecordRequest(
        ISSUED_FOR_AGENT_TESTVAL,
        PID_ISSUER_TESTVAL_OTHER,
        STRUCTURAL_TYPE_TESTVAL,
        LOC_TESTVAL
    );
  }

  public static DoiRecordRequest givenDoiRecordRequestObject() {
    return new DoiRecordRequest(
        ISSUED_FOR_AGENT_TESTVAL,
        PID_ISSUER_TESTVAL_OTHER,
        STRUCTURAL_TYPE_TESTVAL,
        LOC_TESTVAL,
        REFERENT_NAME_TESTVAL,
        FdoType.DIGITAL_MEDIA.getDigitalObjectName(),
        PRIMARY_REFERENT_TYPE_TESTVAL
    );
  }

  public static DigitalSpecimenRequest givenDigitalSpecimenRequestObjectNullOptionals() {
    return givenDigitalSpecimenRequestObjectNullOptionals(PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL);
  }

  public static DigitalSpecimenRequest givenDigitalSpecimenRequestObjectNullOptionals(
      String primarySpecimenObjectId) {
    try {
      return new DigitalSpecimenRequest(
          ISSUED_FOR_AGENT_TESTVAL,
          PID_ISSUER_TESTVAL_OTHER,
          LOC_TESTVAL,
          REFERENT_NAME_TESTVAL,
          PRIMARY_REFERENT_TYPE_TESTVAL,
          SPECIMEN_HOST_TESTVAL,
          SPECIMEN_HOST_NAME_TESTVAL,
          primarySpecimenObjectId,
          PrimarySpecimenObjectIdType.GLOBAL, null, primarySpecimenObjectId,
          null, null, null, null, null, null, null,
          null,
          null,
          null, null, null,
          null, null);
    } catch (InvalidRequestException e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  public static DigitalMediaRequest givenMediaRequestObject() throws InvalidRequestException {
    return new DigitalMediaRequest(
        ISSUED_FOR_AGENT_TESTVAL,
        PID_ISSUER_TESTVAL_OTHER,
        LOC_TESTVAL,
        REFERENT_NAME_TESTVAL,
        PRIMARY_REFERENT_TYPE_TESTVAL,
        MEDIA_HOST_TESTVAL, MEDIA_HOST_NAME_TESTVAL, null, Boolean.TRUE,
        LINKED_DO_PID_TESTVAL,
        LINKED_DIGITAL_OBJECT_TYPE_TESTVAL, null, HANDLE, null, null, null, null, null,
        null,
        null, SPECIMEN_HOST_TESTVAL, SPECIMEN_HOST_NAME_TESTVAL, null, null
    );
  }

  public static AnnotationRequest givenAnnotationRequestObject() {
    return new AnnotationRequest(
        ISSUED_FOR_AGENT_TESTVAL,
        PID_ISSUER_TESTVAL_OTHER,
        LOC_TESTVAL,
        TARGET_DOI_TESTVAL,
        TARGET_TYPE_TESTVAL,
        MOTIVATION_TESTVAL,
        ANNOTATION_HASH_TESTVAL
    );
  }

  public static AnnotationRequest givenAnnotationRequestObjectNoHash() {
    return new AnnotationRequest(
        ISSUED_FOR_AGENT_TESTVAL,
        PID_ISSUER_TESTVAL_OTHER,
        LOC_TESTVAL,
        TARGET_DOI_TESTVAL,
        TARGET_TYPE_TESTVAL,
        MOTIVATION_TESTVAL,
        null
    );
  }

  public static DataMappingRequest givenDataMappingRequestObject() {
    return new DataMappingRequest(
        ISSUED_FOR_AGENT_TESTVAL,
        PID_ISSUER_TESTVAL_OTHER,
        LOC_TESTVAL,
        SOURCE_DATA_STANDARD_TESTVAL
    );
  }

  public static SourceSystemRequest givenSourceSystemRequestObject() {
    return new SourceSystemRequest(
        ISSUED_FOR_AGENT_TESTVAL,
        PID_ISSUER_TESTVAL_OTHER,
        LOC_TESTVAL,
        SPECIMEN_HOST_TESTVAL
    );
  }

  public static OrganisationRequest givenOrganisationRequestObject() {
    return new OrganisationRequest(
        ISSUED_FOR_AGENT_TESTVAL,
        PID_ISSUER_TESTVAL_OTHER,
        LOC_TESTVAL,
        REFERENT_NAME_TESTVAL,
        PRIMARY_REFERENT_TYPE_TESTVAL,
        SPECIMEN_HOST_TESTVAL,
        PTR_TYPE_DOI
    );
  }

  public static MasRequest givenMasRecordRequestObject() {
    return new MasRequest(
        ISSUED_FOR_AGENT_TESTVAL,
        PID_ISSUER_TESTVAL_OTHER,
        LOC_TESTVAL,
        MAS_NAME_TESTVAL
    );
  }


  public static TombstoneRecordRequest genTombstoneRecordRequestObject() {
    return new TombstoneRecordRequest(
        TOMBSTONE_TEXT_TESTVAL
    );
  }

  public static JsonApiWrapperRead givenRecordResponseRead(List<byte[]> handles, String path,
      FdoType recordType)
      throws Exception {
    List<JsonApiDataLinks> dataNodes = new ArrayList<>();

    for (byte[] handle : handles) {
      var testDbRecord = genAttributes(recordType, handle);
      JsonNode recordAttributes = genObjectNodeAttributeRecord(testDbRecord);
      var pidLink = new JsonApiLinks(HANDLE_URI + new String(handle, StandardCharsets.UTF_8));
      dataNodes.add(new JsonApiDataLinks(new String(handle, StandardCharsets.UTF_8),
          recordType.getDigitalObjectType(),
          recordAttributes, pidLink));
    }

    var responseLink = new JsonApiLinks(path);
    return new JsonApiWrapperRead(responseLink, dataNodes);
  }

  public static JsonApiWrapperReadSingle givenRecordResponseReadSingle(String handle, String path,
      FdoType type, JsonNode attributes) {
    return new JsonApiWrapperReadSingle(
        new JsonApiLinks(path),
        new JsonApiDataLinks(handle, type.getDigitalObjectType(), attributes,
            new JsonApiLinks("https://hdl.handle.net/" + handle)));
  }

  public static JsonApiWrapperWrite givenRecordResponseWrite(List<byte[]> handles,
      FdoType recordType)
      throws Exception {
    List<JsonApiDataLinks> dataNodes = new ArrayList<>();

    for (byte[] handle : handles) {
      var testDbRecord = genAttributes(recordType, handle);
      JsonNode recordAttributes = genObjectNodeAttributeRecord(testDbRecord);

      var pidLink = new JsonApiLinks(HANDLE_URI + new String(handle, StandardCharsets.UTF_8));
      dataNodes.add(new JsonApiDataLinks(new String(handle, StandardCharsets.UTF_8),
          recordType.getDigitalObjectType(),
          recordAttributes, pidLink));
    }
    return new JsonApiWrapperWrite(dataNodes);
  }

  public static JsonApiWrapperWrite givenAnnotationResponseWrite(List<byte[]> handles) {
    List<JsonApiDataLinks> dataNodes = new ArrayList<>();

    for (byte[] handle : handles) {
      var testDbRecord = List.of(
          new HandleAttribute(ANNOTATION_HASH, handle,
              ANNOTATION_HASH_TESTVAL.toString()));
      JsonNode recordAttributes = genObjectNodeAttributeRecord(testDbRecord);

      var pidLink = new JsonApiLinks(HANDLE_URI + new String(handle, StandardCharsets.UTF_8));
      dataNodes.add(new JsonApiDataLinks(new String(handle, StandardCharsets.UTF_8),
          FdoType.ANNOTATION.getDigitalObjectType(),
          recordAttributes, pidLink));
    }
    return new JsonApiWrapperWrite(dataNodes);
  }

  public static JsonApiWrapperWrite givenRecordResponseWriteSmallResponse(
      List<HandleAttribute> testDbRecord, List<byte[]> handles, FdoType type) {
    List<JsonApiDataLinks> dataNodes = new ArrayList<>();
    for (var handle : handles) {
      JsonNode recordAttributes = genObjectNodeAttributeRecord(testDbRecord);
      var pidLink = new JsonApiLinks(HANDLE_URI + new String(handle, StandardCharsets.UTF_8));
      dataNodes.add(
          new JsonApiDataLinks(new String(handle, StandardCharsets.UTF_8),
              type.getDigitalObjectType(),
              recordAttributes, pidLink));
    }
    return new JsonApiWrapperWrite(dataNodes);
  }


  public static JsonApiWrapperWrite givenRecordResponseWriteGeneric(List<byte[]> handles,
      FdoType recordType)
      throws Exception {
    List<JsonApiDataLinks> dataNodes = new ArrayList<>();

    for (byte[] handle : handles) {
      var testDbRecord = genAttributes(recordType, handle);
      JsonNode recordAttributes = genObjectNodeAttributeRecord(testDbRecord);

      var pidLink = new JsonApiLinks(HANDLE_URI + new String(handle, StandardCharsets.UTF_8));
      dataNodes.add(new JsonApiDataLinks(new String(handle, StandardCharsets.UTF_8), "PID",
          recordAttributes, pidLink));
    }
    return new JsonApiWrapperWrite(dataNodes);
  }

  public static JsonApiWrapperWrite givenRecordResponseWrite(List<byte[]> handles,
      FdoType attributeType, String recordType)
      throws Exception {
    List<JsonApiDataLinks> dataNodes = new ArrayList<>();

    for (byte[] handle : handles) {
      var testDbRecord = genAttributes(attributeType, handle);
      JsonNode recordAttributes = genObjectNodeAttributeRecord(testDbRecord);

      var pidLink = new JsonApiLinks(HANDLE_URI + new String(handle, StandardCharsets.UTF_8));
      dataNodes.add(
          new JsonApiDataLinks(new String(handle, StandardCharsets.UTF_8), recordType,
              recordAttributes, pidLink));
    }
    return new JsonApiWrapperWrite(dataNodes);
  }

  public static JsonApiWrapperWrite givenRecordResponseWriteAltLoc(List<byte[]> handles)
      throws Exception {
    return givenRecordResponseWriteAltLoc(handles, FdoType.HANDLE);
  }

  public static JsonApiWrapperWrite givenRecordResponseWriteAltLoc(List<byte[]> handles,
      FdoType recordType)
      throws Exception {
    List<JsonApiDataLinks> dataNodes = new ArrayList<>();

    for (byte[] handle : handles) {
      var testDbRecord = genUpdateRecordAttributesAltLoc(handle);
      JsonNode recordAttributes = genObjectNodeAttributeRecord(testDbRecord);

      var pidLink = new JsonApiLinks(HANDLE_URI + new String(handle, StandardCharsets.UTF_8));
      dataNodes.add(
          new JsonApiDataLinks(new String(handle, StandardCharsets.UTF_8),
              recordType.getDigitalObjectType(),
              recordAttributes, pidLink));
    }
    return new JsonApiWrapperWrite(dataNodes);
  }

  public static JsonApiWrapperWrite givenRecordResponseNullAttributes(List<byte[]> handles) {
    return givenRecordResponseNullAttributes(handles, FdoType.HANDLE);
  }

  public static JsonApiWrapperWrite givenRecordResponseNullAttributes(List<byte[]> handles,
      FdoType type) {
    List<JsonApiDataLinks> dataNodes = new ArrayList<>();
    for (byte[] handle : handles) {
      var pidLink = new JsonApiLinks(HANDLE_URI + new String(handle, StandardCharsets.UTF_8));
      dataNodes.add(
          new JsonApiDataLinks(new String(handle, StandardCharsets.UTF_8),
              type.getDigitalObjectType(), null,
              pidLink));
    }
    return new JsonApiWrapperWrite(dataNodes);
  }


  public static JsonApiWrapperWrite givenRecordResponseWriteArchive(List<byte[]> handles)
      throws Exception {
    List<JsonApiDataLinks> dataNodes = new ArrayList<>();

    for (byte[] handle : handles) {
      var testDbRecord = genTombstoneRecordRequestAttributes(handle);
      JsonNode recordAttributes = genObjectNodeAttributeRecord(testDbRecord);

      var pidLink = new JsonApiLinks(HANDLE_URI + new String(handle, StandardCharsets.UTF_8));
      dataNodes.add(
          new JsonApiDataLinks(new String(handle, StandardCharsets.UTF_8),
              FdoType.TOMBSTONE.getDigitalObjectType(),
              recordAttributes, pidLink));
    }
    return new JsonApiWrapperWrite(dataNodes);
  }

  public static List<HandleAttribute> genAttributes(FdoType recordType, byte[] handle)
      throws Exception {
    switch (recordType) {
      case DOI -> {
        return genDoiRecordAttributes(handle, recordType);
      }
      case DIGITAL_SPECIMEN -> {
        return genDigitalSpecimenAttributes(handle);
      }
      case DIGITAL_MEDIA -> {
        return genDigitalMediaAttributes(handle);
      }
      case ANNOTATION -> {
        return genAnnotationAttributes(handle, false);
      }
      case DATA_MAPPING -> {
        return genDataMappingAttributes(handle);
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

  public static List<JsonNode> genUpdateRequestBatch(List<byte[]> handles, FdoType type) {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode requestNodeRoot = mapper.createObjectNode();
    ObjectNode requestNodeData = mapper.createObjectNode();
    List<JsonNode> requestNodeList = new ArrayList<>();

    for (byte[] handle : handles) {
      requestNodeData.put("type", type.getDigitalObjectType());
      requestNodeData.put("id", new String(handle, StandardCharsets.UTF_8));
      requestNodeData.set("attributes", genUpdateRequestAltLoc());
      requestNodeRoot.set("data", requestNodeData);

      requestNodeList.add(requestNodeRoot.deepCopy());

      requestNodeData.removeAll();
      requestNodeRoot.removeAll();
    }
    return requestNodeList;
  }

  public static List<JsonNode> genUpdateRequestBatch(List<byte[]> handles) {
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
  public static JsonNode genObjectNodeAttributeRecord(List<HandleAttribute> dbRecord) {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode rootNode = mapper.createObjectNode();

    for (HandleAttribute row : dbRecord) {
      if (row.getIndex() != HS_ADMIN.index()) {
        var rowData = new String(row.getData(), StandardCharsets.UTF_8);
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

  public static byte[] givenLandingPage(String handle) throws Exception {
    var landingPage = new String[]{"Placeholder landing page"};
    return setLocations(landingPage, handle, FdoType.TOMBSTONE, false);
  }

  public static HandleAttribute givenLandingPageAttribute(byte[] handle) throws Exception {
    var data = givenLandingPage(new String(handle, StandardCharsets.UTF_8));
    return new HandleAttribute(LOC.index(), handle, LOC.get(), data);
  }

  public static byte[] setLocations(String[] userLocations, String handle, FdoType type,
      boolean isDoiProfileTest)
      throws TransformerException, ParserConfigurationException {
    DOC_BUILDER_FACTORY.setFeature("http://apache.org/xml/features/disallow-doctype-decl",
        true);

    DocumentBuilder documentBuilder = DOC_BUILDER_FACTORY.newDocumentBuilder();

    var doc = documentBuilder.newDocument();
    var locations = doc.createElement("locations");
    doc.appendChild(locations);
    String[] objectLocations = isDoiProfileTest
        ? userLocations : concatLocations(userLocations, handle, type);

    for (int i = 0; i < objectLocations.length; i++) {
      var locs = doc.createElement("location");
      locs.setAttribute("id", String.valueOf(i));
      locs.setAttribute("href", objectLocations[i]);
      String weight = i < 1 ? "1" : "0";
      locs.setAttribute("weight", weight);
      locations.appendChild(locs);
    }
    return documentToString(doc).getBytes(StandardCharsets.UTF_8);
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
      case DATA_MAPPING -> {
        return new String[]{ORCHESTRATION_URL + "/mapping/" + handle};
      }
      case SOURCE_SYSTEM -> {
        return new String[]{ORCHESTRATION_URL + "/source-system/" + handle};
      }
      case DIGITAL_MEDIA -> {
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
    return new String(new ClassPathResource(fileName).getInputStream()
        .readAllBytes(), StandardCharsets.UTF_8);
  }

}
