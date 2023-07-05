package eu.dissco.core.handlemanager.domain.requests.objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.LivingOrPreserved;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.PhysicalIdType;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.lang.Nullable;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class DigitalSpecimenRequest extends DoiRecordRequest {

  @JsonProperty(required = true)
  @JsonPropertyDescription("ROR of the host institution")
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
  private final String[] otherSpecimenIds;
  @Nullable
  private final String topicOrigin;
  @Nullable
  private final String topicDomain;
  @Nullable
  private final String topicDiscipline;
  // Add topic Category
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
  private final String materialOrDigitalEntity;
  @Nullable
  private final Boolean markedAsType;
  @Nullable
  private final String wasDerivedFrom;

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
      String[] otherSpecimenIds,
      String topicOrigin,
      String topicDomain,
      String topicDiscipline,
      String objectType,
      LivingOrPreserved livingOrPreserved,
      String baseTypeOfSpecimen,
      String informationArtefactType,
      String materialSampleType,
      String materialOrDigitalEntity,
      Boolean markedAsType,
      String wasDerivedFrom
  ) throws InvalidRequestException {
    super(fdoProfile, issuedForAgent, digitalObjectTypePid, pidIssuer, structuralType, locations,
        referentName, primaryReferentType);
    this.specimenHost = specimenHost;
    this.specimenHostName = specimenHostName;
    this.primarySpecimenObjectId = primarySpecimenObjectId;
    this.primarySpecimenObjectIdType =
        primarySpecimenObjectIdType == null ? PhysicalIdType.COMBINED : primarySpecimenObjectIdType;
    this.primarySpecimenObjectIdName = primarySpecimenObjectIdName;
    this.primarySpecimenObjectIdAbsenceReason = primarySpecimenObjectIdAbsenceReason;
    this.otherSpecimenIds = otherSpecimenIds;
    this.topicOrigin = topicOrigin;
    this.topicDomain = topicDomain;
    this.topicDiscipline = topicDiscipline;
    this.objectType = objectType;
    this.livingOrPreserved = livingOrPreserved;
    this.baseTypeOfSpecimen = baseTypeOfSpecimen;
    this.informationArtefactType = informationArtefactType;
    this.materialSampleType = materialSampleType;
    this.materialOrDigitalEntity = setDefault(materialOrDigitalEntity, "digital");
    this.markedAsType = markedAsType;
    this.wasDerivedFrom = wasDerivedFrom;
    idXorAbsence();
  }

  private void idXorAbsence() throws InvalidRequestException {

    if ((this.primarySpecimenObjectId == null) == (this.primarySpecimenObjectIdAbsenceReason == null)) {
      throw new InvalidRequestException(
          "Request must contain exactly one of: [primarySpecimenObjectId, primarySpecimenObjectIdAbsenceReason]");
    }
  }

}
