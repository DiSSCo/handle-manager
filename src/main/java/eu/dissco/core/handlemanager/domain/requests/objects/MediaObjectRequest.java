package eu.dissco.core.handlemanager.domain.requests.objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.PrimaryObjectIdType;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.StructuralType;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.media.LinkedDigitalObjectType;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.media.MediaFormat;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.media.PrimaryMediaObjectType;
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
  @Nullable
  private final String primaryMediaId;
  @Nullable
  private final PrimaryObjectIdType primaryMediaObjectIdType;
  @Nullable
  private final String primaryMediaObjectIdName;
  @Nullable
  private final String derivedFromPrimarySpecimenObjectId;
  @Nullable
  private final PrimaryMediaObjectType primaryMediaObjectType;
  @Nullable
  private final String mediaMimeType;
  @Nullable
  private final String derivedFromEntity;
  private final String licenseName;
  @Nullable
  private final String licenseUrl;
  @Nullable
  private final String rightsholderName;
  @Nullable
  private final String rightsholderPid;
  @Nullable
  private final String rightsholderPidType;
  @Nullable
  @JsonProperty(value = "dcterms:conforms")
  private final String dctermsConforms;

  private static final String REFERENT_TYPE = "Digital Media Object";

  public MediaObjectRequest(
      String fdoProfile,
      String issuedForAgent,
      String digitalObjectTypePid,
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
      PrimaryObjectIdType primaryMediaObjectIdType,
      String primaryMediaObjectIdName,
      String derivedFromPrimarySpecimenObjectId,
      PrimaryMediaObjectType primaryMediaObjectType,
      String mediaMimeType,
      String derivedFromEntity,
      String licenseName,
      String licenseUrl,
      String rightsholderPid,
      String rightsholderName,
      String rightsholderPidType,
      String dctermsConforms
  ) throws InvalidRequestException {
    super(fdoProfile, issuedForAgent, digitalObjectTypePid, pidIssuer, StructuralType.DIGITAL,
        locations,
        referentName, REFERENT_TYPE, primaryReferentType);
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
    this.derivedFromPrimarySpecimenObjectId = derivedFromPrimarySpecimenObjectId;
    this.primaryMediaObjectType = primaryMediaObjectType;
    this.mediaMimeType = mediaMimeType;
    this.derivedFromEntity = derivedFromEntity;
    this.licenseName = licenseName;
    this.licenseUrl = licenseUrl;
    this.rightsholderPid = rightsholderPid == null ? mediaHost : rightsholderPid;
    this.rightsholderName = rightsholderName;
    this.rightsholderPidType = rightsholderPidType;
    this.dctermsConforms = dctermsConforms;
    validatePrimarySpecimenObjectId();
    validateRightsholder(rightsholderName, rightsholderPid);
  }

  private void validatePrimarySpecimenObjectId() throws InvalidRequestException {
    if (Boolean.TRUE.equals(this.isDerivedFromSpecimen)
        && this.derivedFromPrimarySpecimenObjectId == null) {
      throw new InvalidRequestException(
          "Invalid media Request. Media object is derived from specimen, but no specimen id is provided");
    }
  }

  private void validateRightsholder(String name, String pid) throws InvalidRequestException {
    if (name != null && pid == null) {
      throw new InvalidRequestException(
          "Invalid media request. Rightsholder name provided without an identifier");
    }

  }

}
