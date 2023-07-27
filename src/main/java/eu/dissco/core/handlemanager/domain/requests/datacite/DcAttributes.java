package eu.dissco.core.handlemanager.domain.requests.datacite;

import static eu.dissco.core.handlemanager.domain.FdoProfile.MATERIAL_SAMPLE_TYPE;
import static eu.dissco.core.handlemanager.domain.FdoProfile.TOPIC_DISCIPLINE;
import static eu.dissco.core.handlemanager.domain.FdoProfile.TOPIC_DOMAIN;
import static eu.dissco.core.handlemanager.domain.FdoProfile.TOPIC_ORIGIN;
import static eu.dissco.core.handlemanager.domain.requests.datacite.DcAttributes.UriScheme.HANDLE;
import static eu.dissco.core.handlemanager.domain.requests.datacite.DcAttributes.UriScheme.ROR;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.dissco.core.handlemanager.domain.requests.objects.DigitalSpecimenRequest;
import jakarta.annotation.Nullable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
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
  private String event;

  @JsonProperty("prefix")
  private static final String PREFIX = "10.20.5000.1025";

  @JsonProperty("publisher")
  private static final String PUBLISHER = "Distributed System of Scientific Collections";

  @JsonProperty("schemaVersion")
  private static final String SCHEMA_VERSION = "http://datacite.org/schema/kernel-4.4";

  @Getter(AccessLevel.NONE)
  private static final DateTimeFormatter dt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  public DcAttributes(Instant created, DigitalSpecimenRequest request, String suffix, String url) {
    var zonedDateTime = created.atZone(ZoneId.of("UTC"));
    this.event = "register";
    this.suffix = suffix;
    this.creators = buildCreators();
    this.titles = buildTitles(request);
    this.publicationYear = zonedDateTime.getYear();
    this.subjects = buildSubjects(request);
    this.dates = buildDates(zonedDateTime);
    this.contributors = buildContributors(request);
    this.alternateIdentifiers = buildAltIds(request);
    this.types = buildTypes(request);
    this.relatedIdentifiers = null;
    this.descriptions = buildDescription(request);
    this.url = url;
  }

  private static List<DcCreator> buildCreators() {
    return List.of(
        new DcCreator("Digital System of Scientific Collections",
            List.of(new DcNameIdentifiers("https://ror.org",
                "https://ror.org/0566bfb96",
                "ROR"
            ))));
  }

  protected void setEvent(Event event){
    this.event = event.toString();
  }

  private static List<DcTitle> buildTitles(DigitalSpecimenRequest request) {
    return List.of(
        new DcTitle(request.getReferentName(),
            "en-GB",
            null
        )
    );
  }

  private static List<DcSubject> buildSubjects(DigitalSpecimenRequest request) {
    var subjectList = new ArrayList<DcSubject>();
    if (request.getTopicOrigin() != null) {
      subjectList.add(new DcSubject(request.getTopicOrigin(), TOPIC_ORIGIN.get(), null, null, null));
    }
    if (request.getTopicDomain() != null) {
      subjectList.add(new DcSubject(request.getTopicDomain(), TOPIC_DOMAIN.get(), null, null, null));
    }
    if (request.getTopicDiscipline() != null) {
      subjectList.add(
          new DcSubject(request.getTopicDiscipline(), TOPIC_DISCIPLINE.get(), null, null, null));
    }
    return subjectList;
  }

  private static List<DcDate> buildDates(ZonedDateTime zonedDateTime) {
    return List.of(new DcDate(zonedDateTime.format(dt), "Issued"));
  }

  private static List<DcContributor> buildContributors(DigitalSpecimenRequest request) {
    var uri = getIdentifierScheme(request.getSpecimenHost());

    return List.of(
        new DcContributor(request.getSpecimenHostName(),
            List.of(new DcNameIdentifiers(uri.getUri(), request.getSpecimenHost(),
                uri.getSchemeName()))));
  }

  private static UriScheme getIdentifierScheme(String identifier) {
    if (identifier.contains("ror")) {
      return ROR;
    }
    return HANDLE;
  }

  private static List<DcAlternateIdentifier> buildAltIds(DigitalSpecimenRequest request) {
    return List.of(
        new DcAlternateIdentifier("Primary Specimen Object Identifier",
            request.getPrimarySpecimenObjectId())
    );
  }

  private static List<DcType> buildTypes(DigitalSpecimenRequest request) {
    return List.of(new DcType(
        request.getDigitalObjectType(),
        "TBD"
    ));
  }

  private static List<DcDescription> buildDescription(DigitalSpecimenRequest request) {
    if (request.getMaterialSampleType() == null) {
      return null;
    }
    return List.of(new DcDescription(
        MATERIAL_SAMPLE_TYPE.get() + ": " + request.getMaterialSampleType()));
  }

  protected enum UriScheme {
    ROR("https://ror.org", "ROR"),
    HANDLE("https://hdl.handle.net", "Handle");

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
