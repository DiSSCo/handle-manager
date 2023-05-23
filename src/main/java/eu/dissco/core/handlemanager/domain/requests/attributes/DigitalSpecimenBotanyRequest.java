package eu.dissco.core.handlemanager.domain.requests.attributes;

import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class DigitalSpecimenBotanyRequest extends DigitalSpecimenRequest {

  public DigitalSpecimenBotanyRequest(
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
      String markedAsType,
      String wasDerivedFrom
  ) throws InvalidRequestException {
    super(fdoProfile, issuedForAgent, digitalObjectTypePid, pidIssuer, structuralType, locations,
        referentName, primaryReferentType,
        specimenHost, specimenHostName, primarySpecimenObjectId, primarySpecimenObjectIdType,
        primarySpecimenObjectIdName, primarySpecimenObjectIdAbsenceReason, otherSpecimenIds,
        topicOrigin, topicDomain, topicDiscipline, objectType, livingOrPreserved,
        baseTypeOfSpecimen, informationArtefactType, materialSampleType,
        materialOrDigitalEntity, markedAsType, wasDerivedFrom);
  }

}
