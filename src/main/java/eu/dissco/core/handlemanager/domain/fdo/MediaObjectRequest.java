package eu.dissco.core.handlemanager.domain.fdo;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.dissco.core.handlemanager.domain.fdo.vocabulary.media.DcTermsType;
import eu.dissco.core.handlemanager.domain.fdo.vocabulary.media.LinkedDigitalObjectType;
import eu.dissco.core.handlemanager.domain.fdo.vocabulary.media.MediaFormat;
import eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.PrimarySpecimenObjectIdType;
import eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.StructuralType;
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
  @JsonProperty(value = "dcterms:format")
  private final MediaFormat dctermsFormat;
  @JsonProperty(required = true)
  private final Boolean isDerivedFromSpecimen;
  @JsonProperty(required = true)
  private final String linkedDigitalObjectPid;
  @JsonProperty(required = true)
  private final LinkedDigitalObjectType linkedDigitalObjectType;
  @Nullable
  private final String linkedAttribute;
  @JsonProperty(required = true)
  private final String primaryMediaId;
  @Nullable
  private final PrimarySpecimenObjectIdType primaryMediaObjectIdType;
  @Nullable
  private final String primaryMediaObjectIdName;
  @Nullable
  @JsonProperty(value = "dcterms:type")
  private final DcTermsType dcTermsType;
  @Nullable
  @JsonProperty(value = "dcterms:subject")
  private final String dctermsSubject;
  @Nullable
  private final String derivedFromEntity;
  @Nullable
  private final String licenseName;
  @Nullable
  private final String licenseUrl;
  @Nullable
  private final String rightsholderName;
  @Nullable
  private final String rightsholderPid;
  @Nullable
  private final PrimarySpecimenObjectIdType rightsholderPidType;
  @Nullable
  @JsonProperty(value = "dcterms:conformsTo")
  private final String dctermsConformsTo;

  public MediaObjectRequest(
      String issuedForAgent,
      String pidIssuer,
      String[] locations,
      // Doi
      String referentName,
      String primaryReferentType,
      // Media
      String mediaHost,
      String mediaHostName,
      MediaFormat dctermsFormat,
      Boolean isDerivedFromSpecimen,
      String linkedDigitalObjectPid,
      LinkedDigitalObjectType linkedDigitalObjectType,
      String linkedAttribute,
      String primaryMediaId,
      PrimarySpecimenObjectIdType primaryMediaObjectIdType,
      String primaryMediaObjectIdName,
      DcTermsType dcTermsType,
      String dctermsSubject,
      String derivedFromEntity,
      String licenseName,
      String licenseUrl,
      String rightsholderPid,
      String rightsholderName,
      PrimarySpecimenObjectIdType rightsholderPidType,
      String dctermsConformsTo
  ) throws InvalidRequestException {
    super(issuedForAgent, pidIssuer, StructuralType.DIGITAL,
        locations,
        referentName, FdoType.MEDIA_OBJECT.getDigitalObjectName(), primaryReferentType);
    this.mediaHost = mediaHost;
    this.mediaHostName = mediaHostName;
    this.dctermsFormat = dctermsFormat;
    this.isDerivedFromSpecimen = isDerivedFromSpecimen;
    this.linkedDigitalObjectPid = linkedDigitalObjectPid;
    this.linkedDigitalObjectType = linkedDigitalObjectType;
    this.linkedAttribute = linkedAttribute;
    this.primaryMediaId = primaryMediaId;
    this.primaryMediaObjectIdType = primaryMediaObjectIdType;
    this.primaryMediaObjectIdName = primaryMediaObjectIdName;
    this.dcTermsType = dcTermsType;
    this.dctermsSubject = dctermsSubject;
    this.derivedFromEntity = derivedFromEntity;
    this.licenseName = licenseName;
    this.licenseUrl = licenseUrl;
    this.rightsholderPid = rightsholderPid == null ? mediaHost : rightsholderPid;
    this.rightsholderName = rightsholderName;
    this.rightsholderPidType = rightsholderPidType;
    this.dctermsConformsTo = dctermsConformsTo;
    validateRightsholder(rightsholderName, rightsholderPid);
  }

  private void validateRightsholder(String name, String pid) throws InvalidRequestException {
    if (name != null && pid == null) {
      throw new InvalidRequestException(
          "Invalid media request. Rightsholder name provided without an identifier");
    }
  }

}
