package eu.dissco.core.handlemanager.domain.requests.objects;

import static eu.dissco.core.handlemanager.domain.requests.vocabulary.PrimaryObjectIdType.LOCAL;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.BaseTypeOfSpecimen;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.InformationArtefactType;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.LivingOrPreserved;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.MaterialOrDigitalEntity;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.MaterialSampleType;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.PrimaryObjectIdType;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.StructuralType;
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
  private final PrimaryObjectIdType primarySpecimenObjectIdType;
  @Nullable
  private final String primarySpecimenObjectIdName;
  @Nullable
  private final String normalisedPrimarySpecimenObjectId;
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
  private final LivingOrPreserved livingOrPreserved;
  @Nullable
  private final BaseTypeOfSpecimen baseTypeOfSpecimen;
  @Nullable
  private final InformationArtefactType informationArtefactType;
  @Nullable
  private final MaterialSampleType materialSampleType;
  @Nullable
  private final MaterialOrDigitalEntity materialOrDigitalEntity;
  @Nullable
  private final Boolean markedAsType;
  @Nullable
  private final String derivedFromEntity;
  @Nullable
  private final String sourceSystemId;
  private static final String REFERENT_TYPE = "Digital Specimen";

  public DigitalSpecimenRequest(
      // Handle
      String fdoProfile,
      String issuedForAgent,
      String digitalObjectTypePid,
      String pidIssuer,
      String[] locations,
      // Doi
      String referentName,
      String primaryReferentType,
      // DigitalSpecimen
      String specimenHost,
      String specimenHostName,
      String primarySpecimenObjectId,
      PrimaryObjectIdType primarySpecimenObjectIdType,
      String primarySpecimenObjectIdName,
      String primarySpecimenObjectIdAbsenceReason,
      List<OtherSpecimenId> otherSpecimenIds,
      TopicOrigin topicOrigin,
      TopicDomain topicDomain,
      TopicDiscipline topicDiscipline,
      TopicCategory topicCategory,
      LivingOrPreserved livingOrPreserved,
      BaseTypeOfSpecimen baseTypeOfSpecimen,
      InformationArtefactType informationArtefactType,
      MaterialSampleType materialSampleType,
      MaterialOrDigitalEntity materialOrDigitalEntity,
      Boolean markedAsType,
      String derivedFromEntity,
      String sourceSystemId,
      String normalisedPrimarySpecimenObjetId) throws InvalidRequestException {
    super(fdoProfile, issuedForAgent, digitalObjectTypePid, pidIssuer, StructuralType.DIGITAL,
        locations,
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
    this.livingOrPreserved = livingOrPreserved;
    this.baseTypeOfSpecimen = baseTypeOfSpecimen;
    this.informationArtefactType = informationArtefactType;
    this.materialSampleType = materialSampleType;
    this.materialOrDigitalEntity = materialOrDigitalEntity;
    this.markedAsType = markedAsType;
    this.derivedFromEntity = derivedFromEntity;
    this.sourceSystemId = sourceSystemId;
    idXorAbsence();
    this.normalisedPrimarySpecimenObjectId =
        normalisedPrimarySpecimenObjetId == null ? normalisePrimarySpecimenObjectId()
            : normalisedPrimarySpecimenObjetId;
    validateTopicCategory();
    validateMaterialSampleType();
    validateInformationArtefactType();
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
              + "  has the following topic categories " + this.topicDiscipline.getTopicCategories()
              .toString() + ". Provided topicCategory: " + this.topicCategory);
    }
  }

  private void validateMaterialSampleType() throws InvalidRequestException {
    if (this.materialSampleType == null) {
      return;
    }
    if (this.topicDiscipline == null && this.topicDomain == null && this.topicOrigin == null) {
      return;
    }
    if (this.topicDiscipline != null && this.topicDiscipline.isCorrectMaterialSampleType(
        this.materialSampleType)) {
      return;
    }
    if (this.topicDomain != null && this.topicDomain.isCorrectMaterialSampleType(
        this.materialSampleType)) {
      return;
    }
    if (this.topicOrigin != null && this.topicOrigin.isCorrectMaterialSampleType(
        materialSampleType)) {
      return;
    }
    throw new InvalidRequestException(
        "Invalid material sample type for provided topicDiscipline/topicDomain/topicOrigin.");
  }

  private void validateInformationArtefactType() throws InvalidRequestException {
    if (this.baseTypeOfSpecimen != null && this.baseTypeOfSpecimen.equals(
        BaseTypeOfSpecimen.MATERIAL) && this.informationArtefactType != null) {
      throw new InvalidRequestException(
          "Field informationArtefactType is only valid for Information Artefacts, not Material Entities");
    }
  }

}
