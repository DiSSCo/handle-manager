package eu.dissco.core.handlemanager.domain.requests.objects;

import static eu.dissco.core.handlemanager.domain.requests.vocabulary.PhysicalIdType.LOCAL;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.LivingOrPreserved;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.PhysicalIdType;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.TopicCategory;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.TopicDiscipline;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.TopicDomain;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.TopicOrigin;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.lang.Nullable;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class DigitalSpecimenRequest extends DoiRecordRequest {

  @JsonProperty(required = true)
  @JsonPropertyDescription("ROR or Qnumber of the host institution")
  private final String specimenHost;
  @Nullable
  private final String specimenHostName;
  @JsonPropertyDescription("Local identifier for the given specimen")
  @JsonProperty(required = true)
  private final String primarySpecimenObjectId;
  @JsonPropertyDescription("ID Type. Either combined or cetaf. Defaults to combined.")
  private final PhysicalIdType primarySpecimenObjectIdType;
  @Nullable
  private final String primarySpecimenObjectIdName;
  @Nullable
  private final String primarySpecimenObjectIdAbsenceReason;
  @Nullable
  private final List<OtherSpecimenId> otherSpecimenIds;
  @Nullable
  private final TopicOrigin topicOrigin;
  @Nullable
  private final TopicDomain topicDomain;
  @Nullable
  private final TopicDiscipline topicDiscipline;
  @Nullable
  private final TopicCategory topicCategory;
  @Nullable
  private final String objectType;
  @Nullable
  private final LivingOrPreserved livingOrPreserved;
  @Nullable
  private final String baseTypeOfSpecimen;
  @Nullable
  private final String informationArtefactType;
  @Nullable
  private final String materialSampleType;
  @Nullable
  private final String materialOrDigitalEntity;
  @Nullable
  private final Boolean markedAsType;
  @Nullable
  private final String derivedFromEntity;
  @Nullable
  private final String sourceSystemId;
  private final String normalisedPrimarySpecimenObjectId;
  private static final String REFERENT_TYPE = "Digital Specimen";

  public DigitalSpecimenRequest(
      // Handle
      String fdoProfile,
      String issuedForAgent,
      String digitalObjectTypePid,
      String pidIssuer,
      String structuralType,
      String[] locations,
      // Doi
      String referentName,
      String primaryReferentType,
      // DigitalSpecimen
      String specimenHost,
      String specimenHostName,
      String primarySpecimenObjectId,
      PhysicalIdType primarySpecimenObjectIdType,
      String primarySpecimenObjectIdName,
      String primarySpecimenObjectIdAbsenceReason,
      List<OtherSpecimenId> otherSpecimenIds,
      TopicOrigin topicOrigin,
      TopicDomain topicDomain,
      TopicDiscipline topicDiscipline,
      TopicCategory topicCategory,
      String objectType,
      LivingOrPreserved livingOrPreserved,
      String baseTypeOfSpecimen,
      String informationArtefactType,
      String materialSampleType,
      String materialOrDigitalEntity,
      Boolean markedAsType,
      String derivedFromEntity,
      String sourceSystemId
  ) throws InvalidRequestException {
    super(fdoProfile, issuedForAgent, digitalObjectTypePid, pidIssuer, structuralType, locations,
        referentName, REFERENT_TYPE, primaryReferentType);
    this.specimenHost = specimenHost;
    this.specimenHostName = specimenHostName;
    this.primarySpecimenObjectId = primarySpecimenObjectId;
    this.primarySpecimenObjectIdType =
        primarySpecimenObjectIdType == null ? LOCAL : primarySpecimenObjectIdType;
    this.primarySpecimenObjectIdName = primarySpecimenObjectIdName;
    this.primarySpecimenObjectIdAbsenceReason = primarySpecimenObjectIdAbsenceReason;
    this.otherSpecimenIds = otherSpecimenIds;
    this.topicOrigin = topicOrigin;
    this.topicDomain = topicDomain;
    this.topicDiscipline = topicDiscipline;
    this.topicCategory = topicCategory;
    this.objectType = objectType;
    this.livingOrPreserved = livingOrPreserved;
    this.baseTypeOfSpecimen = baseTypeOfSpecimen;
    this.informationArtefactType = informationArtefactType;
    this.materialSampleType = materialSampleType;
    this.materialOrDigitalEntity = setDefault(materialOrDigitalEntity, "digital");
    this.markedAsType = markedAsType;
    this.derivedFromEntity = derivedFromEntity;
    this.sourceSystemId = sourceSystemId;
    idXorAbsence();
    this.normalisedPrimarySpecimenObjectId = normalisePrimarySpecimenObjectId();
    validateTopicCategory();
  }

  private void idXorAbsence() throws InvalidRequestException {
    if ((this.primarySpecimenObjectId == null) == (this.primarySpecimenObjectIdAbsenceReason
        == null)) {
      throw new InvalidRequestException(
          "Request must contain exactly one of: [primarySpecimenObjectId, primarySpecimenObjectIdAbsenceReason]");
    }
  }

  private String normalisePrimarySpecimenObjectId() throws InvalidRequestException {
    if (this.primarySpecimenObjectIdType.isGlobal()) {
      return this.primarySpecimenObjectId;
    }
    if (this.sourceSystemId == null) {
      throw new InvalidRequestException(
          "Unable to create globally unique primary physical identifier type. Primary Specimen"
              + " Object ID Type is not global/resolvable, and no source system ID is provided");
    }
    return this.primarySpecimenObjectId + ":" + this.sourceSystemId;
  }

  private void validateTopicCategory() throws InvalidRequestException {
    if (this.topicCategory == null || this.topicDiscipline == null) {
      return;
    }
    var isCorrect = this.topicDiscipline.isCorrectCategory(this.topicCategory);
    if (!isCorrect) {
      throw new InvalidRequestException(
          "Discipline/Category Mismatch. Provided TopicDiscipline " + this.topicDiscipline
              + "  has the following topic categories " + this.topicDiscipline.getCategories()
              .toString() + ". Provided topicCategory: " + this.topicCategory);
    }
  }
}
