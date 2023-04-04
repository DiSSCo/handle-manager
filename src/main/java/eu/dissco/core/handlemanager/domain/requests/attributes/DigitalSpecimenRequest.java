package eu.dissco.core.handlemanager.domain.requests.attributes;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class DigitalSpecimenRequest extends DoiRecordRequest {

  @JsonProperty(required = true)
  private final String specimenHost;

  private final String specimenHostName;
  private final String primarySpecimenObjectId;
  private final String primarySpecimenObjectIdType;
  private final String primarySpecimenObjectIdName;
  private final String primarySpecimenObjectIdAbsenceReason;
  private final String[] otherSpecimenIds;
  private final String topicOrigin;
  private final String topicDomain;
  private final String topicDiscipline;
  private final String objectType;
  private final String livingOrPreserved;
  private final String baseTypeOfSpecimen;
  private final String informationArtefactType;
  private final String materialSampleType;
  private final String materialOrDigitalEntity;
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
      String primarySpecimenObjectIdType,
      String primarySpecimenObjectIdName,
      String primarySpecimenObjectIdAbsenceReason,
      String[] otherSpecimenIds,
      String topicOrigin,
      String topicDomain,
      String topicDiscipline,
      String objectType,
      String livingOrPreserved,
      String baseTypeOfSpecimen,
      String informationArtefactType,
      String materialSampleType,
      String materialOrDigitalEntity,
      String wasDerivedFrom
  ) {
    super(fdoProfile, issuedForAgent, digitalObjectTypePid, pidIssuer, structuralType, locations, referentName, primaryReferentType);
    this.specimenHost = specimenHost;
    this.specimenHostName=specimenHostName;
    this.primarySpecimenObjectId = primarySpecimenObjectId;
    this.primarySpecimenObjectIdType = primarySpecimenObjectIdType;
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
    this.wasDerivedFrom = wasDerivedFrom;
  }

  private String setDefault(String attribute, String defaultVal){
    return attribute == null ? defaultVal : attribute;
  }

}
