package eu.dissco.core.handlemanager.domain.requests.datacite;

import static eu.dissco.core.handlemanager.domain.FdoProfile.DIGITAL_OBJECT_TYPE;
import static eu.dissco.core.handlemanager.domain.FdoProfile.FDO_PROFILE;
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
import com.fasterxml.jackson.annotation.JsonProperty;
import eu.dissco.core.handlemanager.domain.FdoProfile;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.HandleAttribute;
import eu.dissco.core.handlemanager.domain.requests.objects.DigitalSpecimenRequest;
import eu.dissco.core.handlemanager.exceptions.DataCiteException;
import jakarta.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@RequiredArgsConstructor
@Slf4j
@JsonInclude(JsonInclude.Include.NON_EMPTY)
class DcAttributes {

  private final String suffix;
  private final List<DcCreator> creators; // IssuedForAgent
  private final List<DcTitle> titles; // ReferentName
  private final int publicationYear;
  private final List<DcSubject> subjects; // topic origin, topic domain, topic discipline, topic category (last one to do)
  private final List<DcContributor> contributors; // SpecimenHost
  private final List<DcDate> dates; // IssueDate
  private final List<DcAlternateIdentifier> alternateIdentifiers; // primarySpecimenObjectId
  private final List<DcType> types; // needs mapping
  private final List<DcRelatedIdentifiers> relatedIdentifiers; // tombstone pids; primary specimenObjectid
  @Nullable
  private final List<DcDescription> descriptions; // needs mapping (208-215)
  private final String url; // What to do with multiple locations?

  @JsonProperty("event")
  private static final String EVENT = "publish";

  @JsonProperty("prefix")
  private static final String PREFIX = "10.22";

  @JsonProperty("publisher")
  private static final String PUBLISHER = "Distributed System of Scientific Collections";

  @JsonProperty("schemaVersion")
  private static final String SCHEMA_VERSION = "http://datacite.org/schema/kernel-4.4";

  @Getter(AccessLevel.NONE)
  private final List<HandleAttribute> pidRecord;

  @Getter(AccessLevel.NONE)
  private final String handle;

  @Getter(AccessLevel.NONE)
  private static final String MISSING_MANDATORY_VALUE_MSG = "Unable to create DOI. Missing mandatory DataCite attribute %s in existing Handle Profile for handle %s";

  @Getter(AccessLevel.NONE)
  Pattern regexPattern = Pattern.compile("\"[^\"]*\"");


  @Getter(AccessLevel.NONE)
  private static final DateTimeFormatter dt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  public DcAttributes(List<HandleAttribute> pidRecord) {
    this.pidRecord = pidRecord;
    this.handle = new String(pidRecord.get(0).handle(), StandardCharsets.UTF_8);
    this.suffix = handle.replace("20.5000.1025/", "");
    this.creators = buildCreators();
    this.titles = buildTitles();
    this.publicationYear = getPublicationYear();
    this.subjects = buildSubjects();
    this.dates = buildDates();
    this.contributors = buildContributors();
    this.alternateIdentifiers = buildAltIds();
    this.types = buildTypes();
    this.relatedIdentifiers = null;
    this.descriptions = buildDescription();
    this.url = getFirstLocation();
  }

  private static List<DcCreator> buildCreators() {
    return List.of(new DcCreator("Digital System of Scientific Collections",
        List.of(new DcNameIdentifiers("https://ror.org", "https://ror.org/0566bfb96", "ROR"))));
  }

  private List<DcTitle> buildTitles() {
    var title = getPidData(REFERENT_NAME);
    return title.map(s -> List.of(new DcTitle(s, "en-GB", null))).orElse(Collections.emptyList());
  }

  private int getPublicationYear() {
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


  private List<DcSubject> buildSubjects() {
    var subjectList = new ArrayList<DcSubject>();
    getPidData(TOPIC_ORIGIN).ifPresent(data -> subjectList.add(
        new DcSubject(data, TOPIC_ORIGIN.get(), null, null, null)));
    getPidData(TOPIC_DOMAIN).ifPresent(data -> subjectList.add(
        new DcSubject(data, TOPIC_DOMAIN.get(), null, null, null)));
    getPidData(TOPIC_DISCIPLINE).ifPresent(data -> subjectList.add(
        new DcSubject(data, TOPIC_DISCIPLINE.get(), null, null, null)));
    return subjectList;
  }

  private List<DcDate> buildDates() {
    var created = getPidData(PID_RECORD_ISSUE_DATE);
    return created.map(s -> List.of(new DcDate(ZonedDateTime.parse(s).format(dt), "Issued")))
        .orElse(null);
  }

  private List<DcContributor> buildContributors() {
    var specimenHost = getPidData(SPECIMEN_HOST);
    var specimenHostName = getPidData(SPECIMEN_HOST_NAME);
    if (specimenHost.isEmpty() || specimenHostName.isEmpty()) {
      return Collections.emptyList();
    }
    var uri = getIdentifierScheme(specimenHost.get());
    return List.of(new DcContributor(specimenHostName.get(), List.of(
        new DcNameIdentifiers(uri.getUri(), specimenHost.get(), uri.getSchemeName()))));
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

  private List<DcAlternateIdentifier> buildAltIds() {
    var physicalId = getPidData(PRIMARY_SPECIMEN_OBJECT_ID);
    if (physicalId.isEmpty()) {
      throw new DataCiteException(
          String.format(MISSING_MANDATORY_VALUE_MSG, PRIMARY_SPECIMEN_OBJECT_ID.get(), handle));
    }
    return List.of(
        new DcAlternateIdentifier(PRIMARY_SPECIMEN_OBJECT_ID.get(), physicalId.get()),
        new DcAlternateIdentifier("Handle", handle));
  }

  private List<DcType> buildTypes() {
    var objectType = getPidData(DIGITAL_OBJECT_TYPE);
    if (objectType.isEmpty()) {
      throw new DataCiteException(
          String.format(MISSING_MANDATORY_VALUE_MSG, DIGITAL_OBJECT_TYPE.get(), handle));
    }
    return List.of(new DcType(objectType.get(), "TBD"));
  }

  private List<DcDescription> buildDescription() {
    var materialSampleType = getPidData(MATERIAL_SAMPLE_TYPE);
    return materialSampleType.map(s -> List.of(
        new DcDescription(MATERIAL_SAMPLE_TYPE.get() + ": " + s))).orElse(Collections.emptyList());
  }

  private String getFirstLocation() {
    var loc = getPidData(LOC);
    if (loc.isEmpty()) {
      throw new DataCiteException(String.format(MISSING_MANDATORY_VALUE_MSG, LOC.get(), handle));
    }
    Matcher matcher = regexPattern.matcher(loc.get());
    if (matcher.find()) {
      return matcher.group().replace("\"", "");
    }
    throw new DataCiteException(String.format(MISSING_MANDATORY_VALUE_MSG, "Location url", handle));
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
