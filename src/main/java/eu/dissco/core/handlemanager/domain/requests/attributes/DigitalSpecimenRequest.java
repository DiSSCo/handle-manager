package eu.dissco.core.handlemanager.domain.requests.attributes;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class DigitalSpecimenRequest extends DoiRecordRequest {

  @JsonProperty(required = true)
  @JsonPropertyDescription("ROR of the host institution")
  private final String specimenHost;
  @JsonPropertyDescription("Local identifier for the given specimen")
  @JsonProperty(required = true)
  private final String primarySpecimenObjectId;

  @JsonPropertyDescription("ID Type. Either combined or cetaf. Defaults to combined.")
  private final PhysicalIdType primarySpecimenObjectIdType;
  private final String primarySpecimenObjectIdName;
  private final String primarySpecimenObjectIdAbsenceReason;
  private final String[] otherSpecimenIds;
  private final String topicOrigin;
  private final String topicDomain;
  private final String topicDiscipline;
  private final String objectType;
  private final LivingOrPreserved livingOrPreserved;
  private final String baseTypeOfSpecimen;
  private final String informationArtefactType;
  private final String materialSampleType;
  private final String materialOrDigitalEntity;
  private final String markedAsType;
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
      String markedAsType,
      String wasDerivedFrom
  ) throws InvalidRequestException {
    super(fdoProfile, issuedForAgent, digitalObjectTypePid, pidIssuer, structuralType, locations,
        referentName, primaryReferentType);
    this.specimenHost = specimenHost;
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
    if ((this.primarySpecimenObjectId == null) == (this.primarySpecimenObjectIdAbsenceReason
        == null)) {
      throw new InvalidRequestException(
          "Request must contain exactly one of: [primarySpecimenObjectId, primarySpecimenObjectIdAbsenceReason]");
    }
  }

}
