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
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.HAS_RELATED_PID;
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
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.TOMBSTONED_DATE;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.TOMBSTONED_TEXT;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.TOPIC_CATEGORY;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.TOPIC_DISCIPLINE;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.TOPIC_DOMAIN;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.TOPIC_ORIGIN;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.WAS_DERIVED_FROM_ENTITY;
import static eu.dissco.core.handlemanager.domain.fdo.FdoType.ANNOTATION;
import static eu.dissco.core.handlemanager.domain.fdo.FdoType.DIGITAL_MEDIA;
import static eu.dissco.core.handlemanager.domain.fdo.FdoType.DIGITAL_SPECIMEN;
import static eu.dissco.core.handlemanager.domain.fdo.FdoType.TOMBSTONE;
import static eu.dissco.core.handlemanager.domain.jsonapi.JsonApiFields.NODE_ATTRIBUTES;
import static eu.dissco.core.handlemanager.domain.jsonapi.JsonApiFields.NODE_DATA;
import static eu.dissco.core.handlemanager.domain.jsonapi.JsonApiFields.NODE_ID;
import static eu.dissco.core.handlemanager.domain.jsonapi.JsonApiFields.NODE_TYPE;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.dissco.core.handlemanager.configuration.InstantDeserializer;
import eu.dissco.core.handlemanager.configuration.InstantSerializer;
import eu.dissco.core.handlemanager.domain.fdo.AnnotationRequest;
import eu.dissco.core.handlemanager.domain.fdo.DataMappingRequest;
import eu.dissco.core.handlemanager.domain.fdo.DigitalMediaRequest;
import eu.dissco.core.handlemanager.domain.fdo.DigitalSpecimenRequest;
import eu.dissco.core.handlemanager.domain.fdo.DoiRecordRequest;
import eu.dissco.core.handlemanager.domain.fdo.FdoProfile;
import eu.dissco.core.handlemanager.domain.fdo.FdoType;
import eu.dissco.core.handlemanager.domain.fdo.HandleRecordRequest;
import eu.dissco.core.handlemanager.domain.fdo.MasRequest;
import eu.dissco.core.handlemanager.domain.fdo.OrganisationRequest;
import eu.dissco.core.handlemanager.domain.fdo.SourceSystemRequest;
import eu.dissco.core.handlemanager.domain.fdo.TombstoneRecordRequest;
import eu.dissco.core.handlemanager.domain.fdo.vocabulary.PidStatus;
import eu.dissco.core.handlemanager.domain.fdo.vocabulary.annotation.Motivation;
import eu.dissco.core.handlemanager.domain.fdo.vocabulary.media.LinkedDigitalObjectType;
import eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.BaseTypeOfSpecimen;
import eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.LivingOrPreserved;
import eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.MaterialOrDigitalEntity;
import eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.MaterialSampleType;
import eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.OtherSpecimenId;
import eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.PrimarySpecimenObjectIdType;
import eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.StructuralType;
import eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.TopicCategory;
import eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.TopicDiscipline;
import eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.TopicDomain;
import eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.TopicOrigin;
import eu.dissco.core.handlemanager.domain.fdo.vocabulary.tombstone.HasRelatedPid;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiDataLinks;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiLinks;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiWrapperRead;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiWrapperWrite;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.FdoAttribute;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.FdoRecord;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
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
  public static final String ISSUED_FOR_AGENT_TESTVAL = ROR_DOMAIN + "0566bfb96";
  public static final String PID_ISSUER_TESTVAL_OTHER = HANDLE_DOMAIN + "20.5000.1025/PID-ISSUER";
  public static final StructuralType STRUCTURAL_TYPE_TESTVAL = StructuralType.DIGITAL;
  public static final String[] LOC_TESTVAL = {"https://sandbox.dissco.tech/", "https://dissco.eu"};
  public static final String[] LOC_ALT_TESTVAL = {"naturalis.nl"};

  // DOI Request Attributes
  public static final String REFERENT_NAME_TESTVAL = "Bird nest";
  public static final String PRIMARY_REFERENT_TYPE_TESTVAL = "materialSample";

  // Generated Attributes
  public static final String PID_STATUS_TESTVAL = PidStatus.ACTIVE.name();
  public static final String REFERENT_DOI_NAME_TESTVAL = PREFIX + "/" + SUFFIX;

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
  public static final String PRIMARY_MEDIA_ID_TESTVAL = "https://images.com/ABC";
  // Mappings
  public static final String SOURCE_DATA_STANDARD_TESTVAL = "dwc";
  // MAS
  public static final String MAS_NAME_TESTVAL = "Plant Organ detection";
  // Tombstone Record vals
  public static final String TOMBSTONE_TEXT_TESTVAL = "pid was deleted";
  // Misc
  public static final String API_URL = "https://sandbox.dissco.tech/api/v1";
  public static final String UI_URL = "https://sandbox.dissco.tech";
  public static final String PATH = UI_URL + HANDLE;
  public static final String ORCHESTRATION_URL = "https://orchestration.dissco.tech/api/v1";
  public static final String PTR_TYPE_DOI = "doi";
  public static final String PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL = "BOTANICAL.QRS.123";
  public static final String NORMALISED_PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL =
      PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL + ":" + ROR_IDENTIFIER;
  public static final String EXTERNAL_PID = "21.T11148/d8de0819e144e4096645";
  public static final TransformerFactory TRANSFORMER_FACTORY = TransformerFactory.newInstance();
  public static final DocumentBuilderFactory DOC_BUILDER_FACTORY = DocumentBuilderFactory.newInstance();
  public static final ObjectMapper MAPPER;

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

  // Handle Attribute Lists
  public static FdoRecord givenHandleFdoRecord(String handle) throws Exception {
    return new FdoRecord(handle, FdoType.HANDLE,
        genHandleRecordAttributes(handle, CREATED, FdoType.HANDLE), null);
  }

  public static List<FdoAttribute> genHandleRecordAttributes(String handle, Instant timestamp,
      FdoType fdoType) throws Exception {
    List<FdoAttribute> fdoAttributes = new ArrayList<>();
    var request = givenHandleRecordRequestObject();
    var loc = setLocations(request.getLocations(), handle, fdoType);
    fdoAttributes.add(new FdoAttribute(LOC, CREATED, loc));
    // 1: FDO Profile
    fdoAttributes.add(new FdoAttribute(FDO_PROFILE, timestamp, fdoType.getFdoProfile()));
    // 2: FDO Record License
    fdoAttributes.add(new FdoAttribute(FDO_RECORD_LICENSE, timestamp,
        "https://creativecommons.org/publicdomain/zero/1.0/"));
    // 3: DigitalObjectType
    fdoAttributes.add(
        new FdoAttribute(DIGITAL_OBJECT_TYPE, timestamp, fdoType.getDigitalObjectType()));
    // 4: DigitalObjectName
    fdoAttributes.add(
        new FdoAttribute(DIGITAL_OBJECT_NAME, timestamp, fdoType.getDigitalObjectName()));
    // 5: Pid
    fdoAttributes.add(new FdoAttribute(PID, timestamp, fdoType.getDomain() + handle));
    // 6: PidIssuer
    fdoAttributes.add(new FdoAttribute(PID_ISSUER, timestamp, request.getPidIssuer()));
    // 7: pidIssuerName
    fdoAttributes.add(new FdoAttribute(PID_ISSUER_NAME, timestamp, PID_ISSUER_TESTVAL_OTHER));
    // 8: issuedForAgent
    fdoAttributes.add(new FdoAttribute(ISSUED_FOR_AGENT, timestamp, request.getIssuedForAgent()));
    // 9: issuedForAgentName
    fdoAttributes.add(new FdoAttribute(ISSUED_FOR_AGENT_NAME, timestamp, ISSUED_FOR_AGENT_TESTVAL));
    // 10: pidRecordIssueDate
    fdoAttributes.add(new FdoAttribute(PID_RECORD_ISSUE_DATE, timestamp, ISSUE_DATE_TESTVAL));
    // 11: pidRecordIssueNumber
    fdoAttributes.add(new FdoAttribute(PID_RECORD_ISSUE_NUMBER, timestamp, "1"));
    // 12: structuralType
    fdoAttributes.add(
        new FdoAttribute(STRUCTURAL_TYPE, timestamp, STRUCTURAL_TYPE_TESTVAL.toString()));
    // 13: PidStatus
    fdoAttributes.add(new FdoAttribute(PID_STATUS, timestamp, PID_STATUS_TESTVAL));
    // 100 ADMIN
    fdoAttributes.add(new FdoAttribute(timestamp, PREFIX));

    return fdoAttributes;
  }

  public static FdoRecord givenUpdatedFdoRecord(FdoType fdoType, String primaryLocalId)
      throws Exception {
    var attributes = new ArrayList<>(genAttributes(fdoType, HANDLE, UPDATED));
    var locUpdated = setLocations(LOC_ALT_TESTVAL, HANDLE, fdoType);
    var attributesWithUpdatedTimeStamp = attributes.stream().map(attribute -> {
      if (attribute.getIndex() == LOC.index()) {
        return new FdoAttribute(LOC, UPDATED, locUpdated);
      }
      if (attribute.getIndex() == FDO_RECORD_LICENSE.index()) {
        return new FdoAttribute(FDO_RECORD_LICENSE, CREATED, attribute.getValue());
      }
      if (attribute.getIndex() == PID.index()) {
        return new FdoAttribute(PID, CREATED, attribute.getValue());
      }
      if (attribute.getIndex() == PID_RECORD_ISSUE_DATE.index()) {
        return new FdoAttribute(PID_RECORD_ISSUE_DATE, CREATED, attribute.getValue());
      }
      if (attribute.getIndex() == PID_RECORD_ISSUE_NUMBER.index()) {
        return new FdoAttribute(PID_RECORD_ISSUE_NUMBER, UPDATED, "2");
      }
      if (attribute.getIndex() == PID_STATUS.index()) {
        return new FdoAttribute(PID_STATUS, CREATED, attribute.getValue());
      }
      if (attribute.getIndex() == HS_ADMIN.index()) {
        return new FdoAttribute(CREATED, PREFIX);
      }
      return attribute;
    }).toList();
    return new FdoRecord(HANDLE, fdoType, attributesWithUpdatedTimeStamp, primaryLocalId);
  }

  public static FdoRecord givenDoiFdoRecord(String handle) throws Exception {
    return new FdoRecord(handle, FdoType.DOI,
        genDoiRecordAttributes(handle, CREATED, FdoType.DOI, givenDoiRecordRequestObject()), null);
  }

  public static List<FdoAttribute> genDoiRecordAttributes(String handle, Instant timestamp,
      FdoType type, DoiRecordRequest request) throws Exception {
    var fdoRecord = genHandleRecordAttributes(handle, timestamp, type);
    // 40: referentType
    fdoRecord.add(new FdoAttribute(REFERENT_TYPE, timestamp, request.getReferentType()));
    // 41: referentDoiName
    fdoRecord.add(new FdoAttribute(REFERENT_DOI_NAME, timestamp, REFERENT_DOI_NAME_TESTVAL));
    // 42: referentName
    fdoRecord.add(new FdoAttribute(REFERENT_NAME, timestamp, request.getReferentName()));
    // 43: primaryReferentType
    fdoRecord.add(
        new FdoAttribute(PRIMARY_REFERENT_TYPE, timestamp, request.getPrimaryReferentType()));
    return fdoRecord;
  }

  public static FdoRecord givenDigitalSpecimenFdoRecord(String handle) throws Exception {
    return new FdoRecord(handle, FdoType.DIGITAL_SPECIMEN,
        genDigitalSpecimenAttributes(handle, givenDigitalSpecimenRequestObjectNullOptionals(),
            CREATED),
        NORMALISED_PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL);
  }

  public static List<FdoAttribute> genDigitalSpecimenAttributes(String handle,
      DigitalSpecimenRequest request, Instant timestamp) throws Exception {
    List<FdoAttribute> fdoRecord = genDoiRecordAttributes(handle, timestamp,
        FdoType.DIGITAL_SPECIMEN, request);
    // 200: Specimen Host
    fdoRecord.add(new FdoAttribute(SPECIMEN_HOST, timestamp, request.getSpecimenHost()));
    // 201: Specimen Host name
    fdoRecord.add(new FdoAttribute(SPECIMEN_HOST_NAME, timestamp, SPECIMEN_HOST_NAME_TESTVAL));
    // 202: primarySpecimenObjectId
    fdoRecord.add(new FdoAttribute(PRIMARY_SPECIMEN_OBJECT_ID, timestamp,
        request.getPrimarySpecimenObjectId()));
    // 203: primarySpecimenObjectIdType
    fdoRecord.add(new FdoAttribute(PRIMARY_SPECIMEN_OBJECT_ID_TYPE, timestamp,
        request.getPrimarySpecimenObjectIdType().toString()));
    // 204: primarySpecimenObjectIdName
    fdoRecord.add(new FdoAttribute(PRIMARY_SPECIMEN_OBJECT_ID_NAME, timestamp,
        request.getPrimarySpecimenObjectIdName()));
    // 205: normalisedSpecimenObjectId
    fdoRecord.add(new FdoAttribute(NORMALISED_SPECIMEN_OBJECT_ID, timestamp,
        request.getNormalisedPrimarySpecimenObjectId()));
    // 206: specimenObjectIdAbsenceReason
    fdoRecord.add(new FdoAttribute(SPECIMEN_OBJECT_ID_ABSENCE_REASON, timestamp,
        request.getSpecimenObjectIdAbsenceReason()));
    // 207: otherSpecimenIds
    if (request.getOtherSpecimenIds() != null && !request.getOtherSpecimenIds().isEmpty()) {
      var otherSpecimenIds = MAPPER.writeValueAsString(request.getOtherSpecimenIds());
      fdoRecord.add(new FdoAttribute(OTHER_SPECIMEN_IDS, timestamp, otherSpecimenIds));
    } else {
      fdoRecord.add(new FdoAttribute(OTHER_SPECIMEN_IDS, timestamp, null));
    }
    // 208: topicOrigin
    if (request.getTopicOrigin() != null) {
      fdoRecord.add(new FdoAttribute(TOPIC_ORIGIN, timestamp, request.getTopicOrigin().toString()));
    } else {
      fdoRecord.add(new FdoAttribute(TOPIC_ORIGIN, timestamp, null));
    }
    // 209: topicDomain
    if (request.getTopicDomain() != null) {
      fdoRecord.add(new FdoAttribute(TOPIC_DOMAIN, timestamp, request.getTopicDomain().toString()));
    } else {
      fdoRecord.add(new FdoAttribute(TOPIC_DOMAIN, timestamp, null));
    }
    // 210: topicDiscipline
    if (request.getTopicDiscipline() != null) {
      fdoRecord.add(
          new FdoAttribute(TOPIC_DISCIPLINE, timestamp, request.getTopicDiscipline().toString()));
    } else {
      fdoRecord.add(new FdoAttribute(TOPIC_DISCIPLINE, timestamp, null));
    }
    // 211: topicCategory
    if (request.getTopicCategory() != null) {
      fdoRecord.add(
          new FdoAttribute(TOPIC_CATEGORY, timestamp, request.getTopicCategory().toString()));
    } else {
      fdoRecord.add(new FdoAttribute(TOPIC_CATEGORY, timestamp, null));
    }
    // 212: livingOrPreserved
    if (request.getLivingOrPreserved() != null) {
      fdoRecord.add(new FdoAttribute(LIVING_OR_PRESERVED, timestamp,
          request.getLivingOrPreserved().toString()));
    } else {
      fdoRecord.add(new FdoAttribute(LIVING_OR_PRESERVED, timestamp, null));
    }
    // 213: baseTypeOfSpecimen
    if (request.getBaseTypeOfSpecimen() != null) {
      fdoRecord.add(new FdoAttribute(BASE_TYPE_OF_SPECIMEN, timestamp,
          request.getBaseTypeOfSpecimen().toString()));
    } else {
      fdoRecord.add(new FdoAttribute(BASE_TYPE_OF_SPECIMEN, timestamp, null));
    }
    // 214: informationArtefactType
    if (request.getInformationArtefactType() != null) {
      fdoRecord.add(new FdoAttribute(INFORMATION_ARTEFACT_TYPE, timestamp,
          request.getInformationArtefactType().toString()));
    } else {
      fdoRecord.add(new FdoAttribute(INFORMATION_ARTEFACT_TYPE, timestamp, null));
    }
    // 215: materialSampleType
    if (request.getMaterialSampleType() != null) {
      fdoRecord.add(new FdoAttribute(MATERIAL_SAMPLE_TYPE, timestamp,
          request.getMaterialSampleType().toString()));
    } else {
      fdoRecord.add(new FdoAttribute(MATERIAL_SAMPLE_TYPE, timestamp, null));
    }
    // 216: materialOrDigitalEntity
    if (request.getMaterialOrDigitalEntity() != null) {
      fdoRecord.add(new FdoAttribute(MATERIAL_OR_DIGITAL_ENTITY, timestamp,
          request.getMaterialOrDigitalEntity().toString()));
    } else {
      fdoRecord.add(new FdoAttribute(MATERIAL_OR_DIGITAL_ENTITY, timestamp, null));
    }
    // 217: markedAsType
    if (request.getMarkedAsType() != null) {
      fdoRecord.add(
          new FdoAttribute(MARKED_AS_TYPE, timestamp, request.getMarkedAsType().toString()));
    } else {
      fdoRecord.add(new FdoAttribute(MARKED_AS_TYPE, timestamp, null));
    }
    // 218: wasDerivedFromEntity
    fdoRecord.add(new FdoAttribute(WAS_DERIVED_FROM_ENTITY, timestamp,
        String.valueOf(request.getDerivedFromEntity() != null)));
    fdoRecord.add(new FdoAttribute(CATALOG_IDENTIFIER, timestamp, request.getCatalogIdentifier()));
    return fdoRecord;
  }

  public static FdoRecord givenDigitalMediaFdoRecord(String handle) throws Exception {
    return new FdoRecord(handle, DIGITAL_MEDIA,
        genDigitalMediaAttributes(handle, givenDigitalMediaRequestObject(), CREATED),
        PRIMARY_MEDIA_ID_TESTVAL);
  }

  public static List<FdoAttribute> genDigitalMediaAttributes(String handle,
      DigitalMediaRequest request, Instant timestamp) throws Exception {
    var fdoRecord = genDoiRecordAttributes(handle, timestamp, FdoType.DIGITAL_MEDIA, request);
    fdoRecord.add(new FdoAttribute(MEDIA_HOST, timestamp, request.getMediaHost()));
    if (request.getMediaHostName() == null) {
      fdoRecord.add(new FdoAttribute(MEDIA_HOST_NAME, timestamp, MEDIA_HOST_NAME_TESTVAL));
    } else {
      fdoRecord.add(new FdoAttribute(MEDIA_HOST_NAME, timestamp, request.getMediaHostName()));
    }
    if (request.getDctermsFormat() != null) {
      fdoRecord.add(
          new FdoAttribute(DCTERMS_FORMAT, timestamp, request.getDctermsFormat().toString()));
    } else {
      fdoRecord.add(new FdoAttribute(DCTERMS_FORMAT, timestamp, null));
    }
    fdoRecord.add(new FdoAttribute(IS_DERIVED_FROM_SPECIMEN, timestamp,
        request.getIsDerivedFromSpecimen().toString()));
    fdoRecord.add(new FdoAttribute(LINKED_DO_PID, timestamp, request.getLinkedDigitalObjectPid()));
    if (request.getLinkedDigitalObjectType() != null) {
      fdoRecord.add(new FdoAttribute(LINKED_DO_TYPE, timestamp,
          request.getLinkedDigitalObjectType().toString()));
    } else {
      fdoRecord.add(new FdoAttribute(LINKED_DO_TYPE, timestamp, null));
    }
    fdoRecord.add(new FdoAttribute(LINKED_ATTRIBUTE, timestamp, request.getLinkedAttribute()));
    fdoRecord.add(new FdoAttribute(PRIMARY_MEDIA_ID, timestamp, request.getPrimaryMediaId()));
    if (request.getPrimaryMediaObjectIdType() != null) {
      fdoRecord.add(new FdoAttribute(PRIMARY_MO_ID_TYPE, timestamp,
          request.getPrimaryMediaObjectIdType().toString()));
    } else {
      fdoRecord.add(new FdoAttribute(PRIMARY_MO_ID_TYPE, timestamp, null));
    }
    fdoRecord.add(
        new FdoAttribute(PRIMARY_MO_ID_NAME, timestamp, request.getPrimaryMediaObjectIdName()));
    if (request.getDcTermsType() != null) {
      fdoRecord.add(new FdoAttribute(DCTERMS_TYPE, timestamp, request.getDcTermsType().toString()));
    } else {
      fdoRecord.add(new FdoAttribute(DCTERMS_TYPE, timestamp, null));
    }
    fdoRecord.add(new FdoAttribute(DCTERMS_SUBJECT, timestamp, request.getDctermsSubject()));
    fdoRecord.add(new FdoAttribute(DERIVED_FROM_ENTITY, timestamp, request.getDerivedFromEntity()));
    fdoRecord.add(new FdoAttribute(LICENSE_NAME, timestamp, request.getLicenseName()));
    fdoRecord.add(new FdoAttribute(LICENSE_URL, timestamp, request.getLicenseUrl()));
    fdoRecord.add(new FdoAttribute(RIGHTSHOLDER_NAME, timestamp, request.getRightsholderName()));
    fdoRecord.add(new FdoAttribute(RIGHTSHOLDER_PID, timestamp, request.getRightsholderPid()));
    if (request.getRightsholderPidType() != null) {
      fdoRecord.add(new FdoAttribute(RIGHTSHOLDER_PID_TYPE, timestamp,
          request.getRightsholderPidType().toString()));
    } else {
      fdoRecord.add(new FdoAttribute(RIGHTSHOLDER_PID_TYPE, timestamp, null));
    }
    fdoRecord.add(new FdoAttribute(DC_TERMS_CONFORMS, timestamp, request.getDctermsConformsTo()));
    return fdoRecord;
  }

  public static FdoRecord givenAnnotationFdoRecord(String handle, boolean includeHash)
      throws Exception {
    var localId = includeHash ? ANNOTATION_HASH_TESTVAL.toString() : null;
    return new FdoRecord(handle, FdoType.ANNOTATION, genAnnotationAttributes(handle, includeHash),
        localId);
  }

  public static FdoRecord givenMasFdoRecord(String handle) throws Exception {
    return new FdoRecord(handle, FdoType.MAS, genMasAttributes(handle, CREATED), null);
  }

  public static FdoRecord givenSourceSystemFdoRecord(String handle) throws Exception {
    return new FdoRecord(handle, FdoType.SOURCE_SYSTEM, genSourceSystemAttributes(handle, CREATED),
        null);
  }

  public static FdoRecord givenOrganisationFdoRecord(String handle) throws Exception {
    return new FdoRecord(handle, FdoType.ORGANISATION,
        genOrganisationAttributes(handle, CREATED, givenOrganisationRequestObject()), null);
  }

  public static FdoRecord givenDataMappingFdoRecord(String handle) throws Exception {
    return new FdoRecord(handle, FdoType.DATA_MAPPING, genMappingAttributes(handle, CREATED), null);
  }

  public static FdoRecord givenTombstoneFdoRecord() throws Exception {
    return new FdoRecord(HANDLE, FdoType.HANDLE,
        genTombstoneAttributes(givenTombstoneRecordRequestObject()), null);
  }

  public static List<FdoAttribute> genAnnotationAttributes(String handle, boolean includeHash)
      throws Exception {
    return genAnnotationAttributes(handle, CREATED, includeHash);
  }

  public static List<FdoAttribute> genAnnotationAttributes(String handle, Instant timestamp,
      boolean includeHash) throws Exception {
    var fdoRecord = genHandleRecordAttributes(handle, timestamp, FdoType.ANNOTATION);
    // 500 TargetPid
    fdoRecord.add(new FdoAttribute(TARGET_PID, timestamp, TARGET_DOI_TESTVAL));
    // 501 TargetType
    fdoRecord.add(new FdoAttribute(TARGET_TYPE, timestamp, TARGET_TYPE_TESTVAL));
    // 502 motivation
    fdoRecord.add(new FdoAttribute(MOTIVATION, timestamp, MOTIVATION_TESTVAL.toString()));
    // 503 AnnotationHash
    if (includeHash) {
      fdoRecord.add(
          new FdoAttribute(ANNOTATION_HASH, timestamp, ANNOTATION_HASH_TESTVAL.toString()));
    } else {
      fdoRecord.add(new FdoAttribute(ANNOTATION_HASH, timestamp, null));
    }
    return fdoRecord;
  }

  public static List<FdoAttribute> genMasAttributes(String handle, Instant timestamp)
      throws Exception {
    var fdoRecord = genHandleRecordAttributes(handle, timestamp, FdoType.MAS);
    fdoRecord.add(new FdoAttribute(MAS_NAME, timestamp, MAS_NAME_TESTVAL));
    return fdoRecord;
  }

  public static List<FdoAttribute> genMappingAttributes(String handle, Instant timestamp)
      throws Exception {
    var fdoRecord = genHandleRecordAttributes(handle, timestamp, FdoType.DATA_MAPPING);
    // 500 subjectDigitalObjectId
    fdoRecord.add(new FdoAttribute(SOURCE_DATA_STANDARD, timestamp, SOURCE_DATA_STANDARD_TESTVAL));
    return fdoRecord;
  }

  public static List<FdoAttribute> genSourceSystemAttributes(String handle, Instant timestamp)
      throws Exception {
    var fdoRecord = genHandleRecordAttributes(handle, timestamp, FdoType.SOURCE_SYSTEM);
    // 600 hostInstitution
    fdoRecord.add(new FdoAttribute(SOURCE_SYSTEM_NAME, timestamp, SPECIMEN_HOST_TESTVAL));
    return fdoRecord;
  }

  public static List<FdoAttribute> genOrganisationAttributes(String handle, Instant timestamp,
      OrganisationRequest request) throws Exception {
    var fdoRecord = genDoiRecordAttributes(handle, timestamp, FdoType.ORGANISATION, request);
    // 800 OrganisationIdentifier
    fdoRecord.add(new FdoAttribute(ORGANISATION_ID, timestamp, SPECIMEN_HOST_TESTVAL));
    // 801 OrganisationIdentifier
    fdoRecord.add(new FdoAttribute(ORGANISATION_ID_TYPE, timestamp, PTR_TYPE_DOI));
    // 802 OrganisationName
    fdoRecord.add(new FdoAttribute(ORGANISATION_NAME, timestamp, ISSUED_FOR_AGENT_TESTVAL));
    return fdoRecord;
  }

  public static List<FdoAttribute> genTombstoneAttributes(TombstoneRecordRequest request)
      throws Exception {
    var fdoRecord = genHandleRecordAttributes(HANDLE, CREATED, FdoType.HANDLE);
    fdoRecord.add(new FdoAttribute(TOMBSTONED_TEXT, UPDATED, request.getTombstonedText()));
    fdoRecord.set(fdoRecord.indexOf(new FdoAttribute(PID_RECORD_ISSUE_NUMBER, CREATED, "1")),
        new FdoAttribute(PID_RECORD_ISSUE_NUMBER, UPDATED, "2"));

    // 31: hasRelatedPID
    if (request.getHasRelatedPID() != null && !request.getHasRelatedPID().isEmpty()) {
      fdoRecord.add(new FdoAttribute(HAS_RELATED_PID, UPDATED,
          MAPPER.writeValueAsString(request.getHasRelatedPID())));
    } else {
      fdoRecord.add(new FdoAttribute(HAS_RELATED_PID, UPDATED,
          MAPPER.writeValueAsString(Collections.emptyList())));
    }
    // 32: tombstonedDate
    fdoRecord.add(new FdoAttribute(TOMBSTONED_DATE, UPDATED, UPDATE_DATE_TESTVAL));
    return fdoRecord;
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

  public static HandleRecordRequest givenHandleRecordRequestObjectUpdate() {
    return new HandleRecordRequest(ISSUED_FOR_AGENT_TESTVAL, PID_ISSUER_TESTVAL_OTHER,
        STRUCTURAL_TYPE_TESTVAL, LOC_ALT_TESTVAL);
  }

  public static DoiRecordRequest givenDoiRecordRequestObject() {
    return new DoiRecordRequest(ISSUED_FOR_AGENT_TESTVAL, PID_ISSUER_TESTVAL_OTHER,
        STRUCTURAL_TYPE_TESTVAL, LOC_TESTVAL, REFERENT_NAME_TESTVAL,
        FdoType.DIGITAL_MEDIA.getDigitalObjectName(), PRIMARY_REFERENT_TYPE_TESTVAL);
  }

  public static DoiRecordRequest givenDoiRecordRequestObjectUpdate() {
    return new DoiRecordRequest(ISSUED_FOR_AGENT_TESTVAL, PID_ISSUER_TESTVAL_OTHER,
        STRUCTURAL_TYPE_TESTVAL, LOC_ALT_TESTVAL, REFERENT_NAME_TESTVAL,
        FdoType.DIGITAL_MEDIA.getDigitalObjectName(), PRIMARY_REFERENT_TYPE_TESTVAL);
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
          null, NORMALISED_PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL, null, null, null, null, null, null,
          null, null, null, null, null, null, null, null);
    } catch (InvalidRequestException e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  public static DigitalSpecimenRequest givenDigitalSpecimenRequestObject() throws Exception {
    var otherSpecimenIds = new OtherSpecimenId("Catalog Id", "Catalog Id");
    return new DigitalSpecimenRequest(ISSUED_FOR_AGENT_TESTVAL, PID_ISSUER_TESTVAL_OTHER,
        LOC_TESTVAL, REFERENT_NAME_TESTVAL, PRIMARY_REFERENT_TYPE_TESTVAL, SPECIMEN_HOST_TESTVAL,
        SPECIMEN_HOST_NAME_TESTVAL, PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL,
        PrimarySpecimenObjectIdType.GLOBAL, null, NORMALISED_PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL,
        null, List.of(otherSpecimenIds), TopicOrigin.HUMAN_MADE, TopicDomain.ARCHIVE,
        TopicDiscipline.ANTHRO, TopicCategory.HUMAN, LivingOrPreserved.PRESERVED,
        BaseTypeOfSpecimen.MATERIAL, null, MaterialSampleType.ORG_PART,
        MaterialOrDigitalEntity.DIGITAL, true, "Entity this was derived from", "Catalog id");
  }

  public static DigitalSpecimenRequest givenDigitalSpecimenRequestObjectUpdate() throws Exception {
    return new DigitalSpecimenRequest(ISSUED_FOR_AGENT_TESTVAL, PID_ISSUER_TESTVAL_OTHER,
        LOC_ALT_TESTVAL, REFERENT_NAME_TESTVAL, PRIMARY_REFERENT_TYPE_TESTVAL,
        SPECIMEN_HOST_TESTVAL, SPECIMEN_HOST_NAME_TESTVAL, PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL,
        PrimarySpecimenObjectIdType.GLOBAL, null, NORMALISED_PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL,
        null, null, null, null, null, null, null, null, null, null, null, null, null, null);
  }

  public static DigitalMediaRequest givenDigitalMediaRequestObject()
      throws InvalidRequestException {
    return new DigitalMediaRequest(ISSUED_FOR_AGENT_TESTVAL, PID_ISSUER_TESTVAL_OTHER, LOC_TESTVAL,
        REFERENT_NAME_TESTVAL, PRIMARY_REFERENT_TYPE_TESTVAL, MEDIA_HOST_TESTVAL,
        MEDIA_HOST_NAME_TESTVAL, null, Boolean.TRUE, LINKED_DO_PID_TESTVAL,
        LINKED_DIGITAL_OBJECT_TYPE_TESTVAL, null, PRIMARY_MEDIA_ID_TESTVAL, null, null, null, null,
        null, null, null, SPECIMEN_HOST_TESTVAL, SPECIMEN_HOST_NAME_TESTVAL, null, null);
  }

  public static DigitalMediaRequest givenDigitalMediaRequestObjectUpdate()
      throws InvalidRequestException {
    return new DigitalMediaRequest(ISSUED_FOR_AGENT_TESTVAL, PID_ISSUER_TESTVAL_OTHER,
        LOC_ALT_TESTVAL, REFERENT_NAME_TESTVAL, PRIMARY_REFERENT_TYPE_TESTVAL, MEDIA_HOST_TESTVAL,
        MEDIA_HOST_NAME_TESTVAL, null, Boolean.TRUE, LINKED_DO_PID_TESTVAL,
        LINKED_DIGITAL_OBJECT_TYPE_TESTVAL, null, PRIMARY_MEDIA_ID_TESTVAL, null, null, null, null,
        null, null, null, SPECIMEN_HOST_TESTVAL, SPECIMEN_HOST_NAME_TESTVAL, null, null);
  }

  public static AnnotationRequest givenAnnotationRequestObject() {
    return new AnnotationRequest(ISSUED_FOR_AGENT_TESTVAL, PID_ISSUER_TESTVAL_OTHER, LOC_TESTVAL,
        TARGET_DOI_TESTVAL, TARGET_TYPE_TESTVAL, MOTIVATION_TESTVAL, ANNOTATION_HASH_TESTVAL);
  }

  public static AnnotationRequest givenAnnotationRequestObjectUpdate() {
    return new AnnotationRequest(ISSUED_FOR_AGENT_TESTVAL, PID_ISSUER_TESTVAL_OTHER,
        LOC_ALT_TESTVAL, TARGET_DOI_TESTVAL, TARGET_TYPE_TESTVAL, MOTIVATION_TESTVAL, null);
  }


  public static AnnotationRequest givenAnnotationRequestObjectNoHash() {
    return new AnnotationRequest(ISSUED_FOR_AGENT_TESTVAL, PID_ISSUER_TESTVAL_OTHER, LOC_TESTVAL,
        TARGET_DOI_TESTVAL, TARGET_TYPE_TESTVAL, MOTIVATION_TESTVAL, null);
  }

  public static DataMappingRequest givenDataMappingRequestObject() {
    return new DataMappingRequest(ISSUED_FOR_AGENT_TESTVAL, PID_ISSUER_TESTVAL_OTHER, LOC_TESTVAL,
        SOURCE_DATA_STANDARD_TESTVAL);
  }

  public static DataMappingRequest givenDataMappingRequestObjectUpdate() {
    return new DataMappingRequest(ISSUED_FOR_AGENT_TESTVAL, PID_ISSUER_TESTVAL_OTHER,
        LOC_ALT_TESTVAL, SOURCE_DATA_STANDARD_TESTVAL);
  }

  public static SourceSystemRequest givenSourceSystemRequestObject() {
    return new SourceSystemRequest(ISSUED_FOR_AGENT_TESTVAL, PID_ISSUER_TESTVAL_OTHER, LOC_TESTVAL,
        SPECIMEN_HOST_TESTVAL);
  }

  public static SourceSystemRequest givenSourceSystemRequestObjectUpdate() {
    return new SourceSystemRequest(ISSUED_FOR_AGENT_TESTVAL, PID_ISSUER_TESTVAL_OTHER,
        LOC_ALT_TESTVAL, SPECIMEN_HOST_TESTVAL);
  }

  public static OrganisationRequest givenOrganisationRequestObject() {
    return new OrganisationRequest(ISSUED_FOR_AGENT_TESTVAL, PID_ISSUER_TESTVAL_OTHER, LOC_TESTVAL,
        REFERENT_NAME_TESTVAL, PRIMARY_REFERENT_TYPE_TESTVAL, SPECIMEN_HOST_TESTVAL, PTR_TYPE_DOI);
  }

  public static OrganisationRequest givenOrganisationRequestObjectUpdate() {
    return new OrganisationRequest(ISSUED_FOR_AGENT_TESTVAL, PID_ISSUER_TESTVAL_OTHER,
        LOC_ALT_TESTVAL, REFERENT_NAME_TESTVAL, PRIMARY_REFERENT_TYPE_TESTVAL,
        SPECIMEN_HOST_TESTVAL, PTR_TYPE_DOI);
  }

  public static MasRequest givenMasRecordRequestObject() {
    return new MasRequest(ISSUED_FOR_AGENT_TESTVAL, PID_ISSUER_TESTVAL_OTHER, LOC_TESTVAL,
        MAS_NAME_TESTVAL);
  }

  public static MasRequest givenMasRecordRequestObjectUpdate() {
    return new MasRequest(ISSUED_FOR_AGENT_TESTVAL, PID_ISSUER_TESTVAL_OTHER,
        LOC_ALT_TESTVAL, MAS_NAME_TESTVAL);
  }

  public static TombstoneRecordRequest givenTombstoneRecordRequestObject() {
    return new TombstoneRecordRequest(TOMBSTONE_TEXT_TESTVAL,
        List.of(new HasRelatedPid(HANDLE_ALT, "Media ID")));
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
      JsonNode recordAttributes = jsonFormatFdoRecord(testDbRecord);
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
      JsonNode recordAttributes = jsonFormatFdoRecord(testDbRecord);
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
    JsonNode recordAttributes = jsonFormatFdoRecord(fdoRecord.attributes());
    var pidLink = new JsonApiLinks(HANDLE_DOMAIN + HANDLE);
    var dataNodes = List.of(
        new JsonApiDataLinks(HANDLE, fdoRecord.fdoType().getDigitalObjectType(), recordAttributes,
            pidLink));
    return new JsonApiWrapperWrite(dataNodes);
  }

  public static JsonApiWrapperWrite givenWriteResponseIdsOnly(
      List<FdoRecord> fdoRecords, FdoType fdoType, String domain) {
    List<JsonApiDataLinks> dataNodes = new ArrayList<>();
    List<FdoAttribute> fdoSublist;
    for (var fdoRecord : fdoRecords) {
      switch (fdoType) {
        case ANNOTATION -> {
          if (fdoRecord.primaryLocalId() == null) {
            fdoSublist = fdoRecord.attributes();
          } else {
            fdoSublist = List.of(getField(fdoRecord.attributes(), ANNOTATION_HASH));
          }
        }
        case DIGITAL_SPECIMEN ->
            fdoSublist = List.of(getField(fdoRecord.attributes(), NORMALISED_SPECIMEN_OBJECT_ID));
        case DIGITAL_MEDIA ->
            fdoSublist = List.of(getField(fdoRecord.attributes(), PRIMARY_MEDIA_ID),
                getField(fdoRecord.attributes(), LINKED_DO_PID));
        default -> fdoSublist = fdoRecord.attributes();
      }
      var recordAttributes = jsonFormatFdoRecord(fdoSublist);
      var pidLink = new JsonApiLinks(domain + fdoRecord.handle());
      dataNodes.add(
          new JsonApiDataLinks(fdoRecord.handle(), fdoType.getDigitalObjectType(), recordAttributes,
              pidLink));
    }
    return new JsonApiWrapperWrite(dataNodes);
  }

  public static List<FdoAttribute> genAttributes(FdoType fdoType, String handle) throws Exception {
    return genAttributes(fdoType, handle, CREATED);
  }

  public static List<FdoAttribute> genAttributes(FdoType fdoType, String handle, Instant timestamp)
      throws Exception {
    switch (fdoType) {
      case DOI -> {
        return genDoiRecordAttributes(handle, timestamp, fdoType, givenDoiRecordRequestObject());
      }
      case DIGITAL_SPECIMEN -> {
        return genDigitalSpecimenAttributes(handle,
            givenDigitalSpecimenRequestObjectNullOptionals(), timestamp);
      }
      case DIGITAL_MEDIA -> {
        return genDigitalMediaAttributes(handle, givenDigitalMediaRequestObject(), timestamp);
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
        return genOrganisationAttributes(handle, timestamp, givenOrganisationRequestObject());
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

  public static List<JsonNode> givenUpdateRequest() {
    return givenUpdateRequest(List.of(HANDLE), FdoType.HANDLE,
        MAPPER.valueToTree(givenHandleRecordRequestObjectUpdate()));
  }

  public static JsonNode givenUpdateRequestSingle(FdoType fdoType, Object request) {
    return givenUpdateRequest(List.of(HANDLE), fdoType, MAPPER.valueToTree(request)).get(0);
  }

  public static List<JsonNode> givenUpdateRequest(List<String> handles, FdoType type,
      JsonNode requestAttributes) {
    var requestNodeList = new ArrayList<JsonNode>();
    for (var handle : handles) {
      requestNodeList.add(MAPPER.createObjectNode()
          .set(NODE_DATA, MAPPER.createObjectNode()
              .put(NODE_TYPE, type.getDigitalObjectType())
              .put(NODE_ID, handle)
              .set(NODE_ATTRIBUTES, requestAttributes)));
    }
    return requestNodeList;
  }

  public static List<JsonNode> givenTombstoneRequest() {
    var request = MAPPER.createObjectNode()
        .set(NODE_DATA, MAPPER.createObjectNode()
            .put(NODE_ID, HANDLE)
            .set(NODE_ATTRIBUTES, MAPPER.valueToTree(givenTombstoneRecordRequestObject())));
    return List.of(request);
  }

  // Handle Attributes as ObjectNode
  public static JsonNode jsonFormatFdoRecord(List<FdoAttribute> dbRecord) {
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

  public static String setLocations(String[] userLocations, String handle, FdoType type)
      throws TransformerException, ParserConfigurationException {
    DOC_BUILDER_FACTORY.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);

    DocumentBuilder documentBuilder = DOC_BUILDER_FACTORY.newDocumentBuilder();

    var doc = documentBuilder.newDocument();
    var locations = doc.createElement("locations");
    doc.appendChild(locations);
    String[] objectLocations = concatLocations(userLocations, handle, type);

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
        return new String[]{api, ui};
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
        return new String[]{api, ui};
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

}
