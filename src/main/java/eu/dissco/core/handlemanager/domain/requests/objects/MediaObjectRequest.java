package eu.dissco.core.handlemanager.domain.requests.objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.FdoType;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.media.LinkedDigitalObjectType;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.media.MediaFormat;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.media.PrimaryMediaObjectType;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.specimen.PrimarySpecimenObjectIdType;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.specimen.StructuralType;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.lang.Nullable;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class MediaObjectRequest extends DoiRecordRequest {

  @JsonProperty(required = true)
  private final String mediaHost;
  @Nullable
  private final String mediaHostName;
  @Nullable
  private final MediaFormat mediaFormat;
  @JsonProperty(required = true)
  private final Boolean isDerivedFromSpecimen;
  @JsonProperty(required = true)
  private final String linkedDigitalObjectPid;
  @JsonProperty(required = true)
  private final LinkedDigitalObjectType linkedDigitalObjectType;
  @Nullable
  private final String linkedAttribute;
  private final String primaryMediaId;
  @Nullable
  private final PrimarySpecimenObjectIdType primaryMediaObjectIdType;
  @Nullable
  private final String primaryMediaObjectIdName;
  @Nullable
  private final PrimaryMediaObjectType primaryMediaObjectType;
  @Nullable
  private final String mediaMimeType;
  @Nullable
  private final String derivedFromEntity;
  @Nullable
  private final String licenseName;
  @Nullable
  private final String license;
  @Nullable
  private final String rightsholderName;
  @Nullable
  private final String rightsholderPid;
  @Nullable
  private final PrimarySpecimenObjectIdType rightsholderPidType;
  @Nullable
  @JsonProperty(value = "dcterms:conforms")
  private final String dctermsConforms;

  public MediaObjectRequest(
      String fdoProfile,
      String issuedForAgent,
      String pidIssuer,
      String[] locations,
      // Doi
      String referentName,
      String primaryReferentType,
      // Media
      String mediaHost,
      String mediaHostName,
      MediaFormat mediaFormat,
      Boolean isDerivedFromSpecimen,
      String linkedDigitalObjectPid,
      LinkedDigitalObjectType linkedDigitalObjectType,
      String linkedAttribute,
      String primaryMediaId,
      PrimarySpecimenObjectIdType primaryMediaObjectIdType,
      String primaryMediaObjectIdName,
      PrimaryMediaObjectType primaryMediaObjectType,
      String mediaMimeType,
      String derivedFromEntity,
      String licenseName,
      String license,
      String rightsholderPid,
      String rightsholderName,
      PrimarySpecimenObjectIdType rightsholderPidType,
      String dctermsConforms
  ) throws InvalidRequestException {
    super(fdoProfile, issuedForAgent, pidIssuer, StructuralType.DIGITAL,
        locations,
        referentName, FdoType.MEDIA_OBJECT.getDigitalObjectName(), primaryReferentType);
    this.mediaHost = mediaHost;
    this.mediaHostName = mediaHostName;
    this.mediaFormat = mediaFormat;
    this.isDerivedFromSpecimen = isDerivedFromSpecimen;
    this.linkedDigitalObjectPid = linkedDigitalObjectPid;
    this.linkedDigitalObjectType = linkedDigitalObjectType;
    this.linkedAttribute = linkedAttribute;
    this.primaryMediaId = primaryMediaId;
    this.primaryMediaObjectIdType = primaryMediaObjectIdType;
    this.primaryMediaObjectIdName = primaryMediaObjectIdName;
    this.primaryMediaObjectType = primaryMediaObjectType;
    this.mediaMimeType = mediaMimeType;
    this.derivedFromEntity = derivedFromEntity;
    this.licenseName = licenseName;
    this.license = license;
    this.rightsholderPid = rightsholderPid == null ? mediaHost : rightsholderPid;
    this.rightsholderName = rightsholderName;
    this.rightsholderPidType = rightsholderPidType;
    this.dctermsConforms = dctermsConforms;
    validateRightsholder(rightsholderName, rightsholderPid);
  }

  private void validateRightsholder(String name, String pid) throws InvalidRequestException {
    if (name != null && pid == null) {
      throw new InvalidRequestException(
          "Invalid media request. Rightsholder name provided without an identifier");
    }
  }

}
