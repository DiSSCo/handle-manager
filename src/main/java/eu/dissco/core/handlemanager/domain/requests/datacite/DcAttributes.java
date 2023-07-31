package eu.dissco.core.handlemanager.domain.requests.datacite;

import static eu.dissco.core.handlemanager.domain.FdoProfile.DIGITAL_OBJECT_NAME;
import static eu.dissco.core.handlemanager.domain.FdoProfile.LOC;
import static eu.dissco.core.handlemanager.domain.FdoProfile.MATERIAL_SAMPLE_TYPE;
import static eu.dissco.core.handlemanager.domain.FdoProfile.PID_RECORD_ISSUE_DATE;
import static eu.dissco.core.handlemanager.domain.FdoProfile.PRIMARY_SPECIMEN_OBJECT_ID;
import static eu.dissco.core.handlemanager.domain.FdoProfile.REFERENT_NAME;
import static eu.dissco.core.handlemanager.domain.FdoProfile.SPECIMEN_HOST;
import static eu.dissco.core.handlemanager.domain.FdoProfile.SPECIMEN_HOST_NAME;
import static eu.dissco.core.handlemanager.domain.FdoProfile.TOPIC_DISCIPLINE;
import static eu.dissco.core.handlemanager.domain.FdoProfile.TOPIC_DOMAIN;
import static eu.dissco.core.handlemanager.domain.FdoProfile.TOPIC_ORIGIN;
import static eu.dissco.core.handlemanager.domain.requests.datacite.DcAttributes.UriScheme.HANDLE;
import static eu.dissco.core.handlemanager.domain.requests.datacite.DcAttributes.UriScheme.QID;
import static eu.dissco.core.handlemanager.domain.requests.datacite.DcAttributes.UriScheme.ROR;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import eu.dissco.core.handlemanager.domain.FdoProfile;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.HandleAttribute;
import eu.dissco.core.handlemanager.exceptions.DataCiteException;
import eu.dissco.core.handlemanager.utils.XmlLocReader;
import jakarta.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@JsonInclude(Include.NON_NULL)
class DcAttributes {

  private final String suffix;
  private final String doi;
  private final List<DcCreator> creators; // IssuedForAgent
  private final List<DcTitle> titles; // ReferentName
  private final Integer publicationYear;
  private final List<DcSubject> subjects; // topic origin, topic domain, topic discipline, topic category (last one to do)
  private final List<DcContributor> contributors; // SpecimenHost
  private final List<DcDate> dates; // IssueDate
  private final List<DcAlternateIdentifier> alternateIdentifiers; // primarySpecimenObjectId
  private final DcType types;
  private final List<DcRelatedIdentifiers> relatedIdentifiers; // tombstone pids; primary specimenObjectid
  @Nullable
  private final List<DcDescription> descriptions;
  private final String url; // What to do with multiple locations?
  private final String prefix = "10.82621";
  private final String publisher = "Distributed System of Scientific Collections";
  private final String schemaVersion = "http://datacite.org/schema/kernel-4.4";
  private final String event = "publish";
  @Getter(AccessLevel.NONE)
  private final List<String> xmlLocations;
  @Getter(AccessLevel.NONE)
  private final XmlLocReader xmlLocReader;
  @Getter(AccessLevel.NONE)
  private final List<HandleAttribute> pidRecord;
  @Getter(AccessLevel.NONE)
  private final String handle;
  @Getter(AccessLevel.NONE)
  private static final String MISSING_MANDATORY_VALUE_MSG = "Unable to create DOI. Missing mandatory DataCite attribute %s in existing Handle Profile for handle %s";
  @Getter(AccessLevel.NONE)
  private static final DateTimeFormatter dt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  public DcAttributes(List<HandleAttribute> pidRecord) {
    this.xmlLocReader = new XmlLocReader();
    this.pidRecord = pidRecord;
    this.handle = new String(pidRecord.get(0).handle(), StandardCharsets.UTF_8);
    this.suffix = handle.replace("20.5000.1025/", "");
    this.doi = this.prefix + "/" + this.suffix;
    this.creators = setCreators();
    this.titles = setTitles();
    this.publicationYear = setPublicationYear();
    this.subjects = setSubjects();
    this.dates = setDates();
    this.contributors = setContributors();
    this.xmlLocations = setXmlLocations();
    this.alternateIdentifiers = setAltIds();
    this.types = setType();
    this.relatedIdentifiers = setRelatedIdentifiers();
    this.descriptions = setDescription();
    this.url = xmlLocations.get(0);
  }

  private static List<DcCreator> setCreators() {
    return List.of(new DcCreator("Digital System of Scientific Collections",
        List.of(new DcNameIdentifiers("https://ror.org", "https://ror.org/0566bfb96", "ROR"))));
  }

  private List<DcTitle> setTitles() {
    var title = getPidData(REFERENT_NAME);
    return title.map(s -> List.of(new DcTitle(s, "en-GB", null))).orElse(Collections.emptyList());
  }

  private Integer setPublicationYear() {
    var created = getPidData(PID_RECORD_ISSUE_DATE);
    if (created.isEmpty()) {
      throw new DataCiteException(
          String.format(MISSING_MANDATORY_VALUE_MSG, PID_RECORD_ISSUE_DATE.get(), handle));
    }
    return ZonedDateTime.parse(created.get()).getYear();
  }

  private Optional<String> getPidData(FdoProfile attribute) {
    for (var pidRow : pidRecord) {
      if (pidRow.type().equals(attribute.get())) {
        return Optional.of(new String(pidRow.data(), StandardCharsets.UTF_8));
      }
    }
    log.warn("Unable to find attribute {} in pid record for handle record {}", attribute.get(),
        new String(pidRecord.get(0).handle(), StandardCharsets.UTF_8));
    return Optional.empty();
  }

  private List<DcSubject> setSubjects() {
    var subjectList = new ArrayList<DcSubject>();
    getPidData(TOPIC_ORIGIN).ifPresent(data -> subjectList.add(
        new DcSubject(data, TOPIC_ORIGIN.get(), null, null, null)));
    getPidData(TOPIC_DOMAIN).ifPresent(data -> subjectList.add(
        new DcSubject(data, TOPIC_DOMAIN.get(), null, null, null)));
    getPidData(TOPIC_DISCIPLINE).ifPresent(data -> subjectList.add(
        new DcSubject(data, TOPIC_DISCIPLINE.get(), null, null, null)));
    return subjectList;
  }

  private List<DcDate> setDates() {
    var created = getPidData(PID_RECORD_ISSUE_DATE);
    return created.map(s -> List.of(new DcDate(ZonedDateTime.parse(s).format(dt), "Issued")))
        .orElse(null);
  }

  private List<DcContributor> setContributors() {
    var specimenHost = getPidData(SPECIMEN_HOST);
    var specimenHostName = getPidData(SPECIMEN_HOST_NAME);
    if (specimenHost.isEmpty() || specimenHostName.isEmpty()) {
      return Collections.emptyList();
    }
    var uri = getIdentifierScheme(specimenHost.get());
    return List.of(new DcContributor(specimenHostName.get(), List.of(
        new DcNameIdentifiers(uri.getUri(), specimenHost.get(), uri.getSchemeName()))));
  }

  private List<String> setXmlLocations(){
    var loc = getPidData(LOC);
    if (loc.isEmpty()) {
      throw new DataCiteException(String.format(MISSING_MANDATORY_VALUE_MSG, LOC.get(), handle));
    }
    return xmlLocReader.getLocationsFromXml(loc.get());
  }

  private static UriScheme getIdentifierScheme(String identifier) {
    if (identifier.contains("ror")) {
      return ROR;
    }
    if (identifier.contains("hdl")) {
      return HANDLE;
    }
    return QID;
  }

  private List<DcAlternateIdentifier> setAltIds() {
    var physicalId = getPidData(PRIMARY_SPECIMEN_OBJECT_ID);
    if (physicalId.isEmpty()) {
      throw new DataCiteException(
          String.format(MISSING_MANDATORY_VALUE_MSG, PRIMARY_SPECIMEN_OBJECT_ID.get(), handle));
    }
    return List.of(
        new DcAlternateIdentifier(PRIMARY_SPECIMEN_OBJECT_ID.get(), physicalId.get()),
        new DcAlternateIdentifier("Handle", handle));
  }

  private DcType setType() {
    var objectType = getPidData(DIGITAL_OBJECT_NAME);
    if (objectType.isEmpty()) {
      throw new DataCiteException(
          String.format(MISSING_MANDATORY_VALUE_MSG, DIGITAL_OBJECT_NAME.get(), handle));
    }
    return new DcType(objectType.get());
  }

  private List<DcDescription> setDescription() {
    var materialSampleType = getPidData(MATERIAL_SAMPLE_TYPE);
    return materialSampleType.map(s -> List.of(
        new DcDescription(MATERIAL_SAMPLE_TYPE.get() + ": " + s))).orElse(Collections.emptyList());
  }

  private List<DcRelatedIdentifiers> setRelatedIdentifiers() {
    List<DcRelatedIdentifiers> relatedIdentifiersList = new ArrayList<>();
    var locs = new ArrayList<>(xmlLocations);
    locs.remove(0);
    for (var location : locs){
      relatedIdentifiersList.add(new DcRelatedIdentifiers("IsVariantFormOf", location, "URL", "Dataset"));
    }
    return relatedIdentifiersList;
  }

  protected enum UriScheme {
    ROR("https://ror.org", "ROR"),
    HANDLE("https://hdl.handle.net", "Handle"),
    QID(null, "Q Number");

    final String uri;
    final String schemeName;

    private UriScheme(String uri, String schemeName) {
      this.uri = uri;
      this.schemeName = schemeName;
    }

    public String getUri() {
      return uri;
    }

    public String getSchemeName() {
      return schemeName;
    }
  }

}
