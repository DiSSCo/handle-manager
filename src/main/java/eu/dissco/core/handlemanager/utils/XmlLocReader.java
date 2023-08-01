package eu.dissco.core.handlemanager.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import eu.dissco.core.handlemanager.exceptions.DataCiteException;
import java.util.List;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class XmlLocReader {
  private final XmlMapper xmlMapper;

  public XmlLocReader(){
    this.xmlMapper = new XmlMapper();
  }

  public List<String> getLocationsFromXml(String xmlDoc) throws DataCiteException {
    try {
      var locations = xmlMapper.readValue(xmlDoc, LocationParentXml.class);
      return locations.getLocation().stream().map(LocationXml::getHref).toList();
    } catch (JsonProcessingException e){
      log.error("Unable to parse 10320/loc field in handle");
      throw new DataCiteException("Unable to parse 10320/loc field in handle");
    }
  }

  public String getLandingPageLocation(List<String> locations, String targetLoc){
    for (var location : locations){
      if (location.contains(targetLoc)){
        return location;
      }
    }
    log.error("Unable to find landing page location from 10320/loc in handle record. Using first value in field");
    return locations.get(0);
  }

  @Data
  protected static class LocationParentXml {
    @JacksonXmlCData
    @JacksonXmlElementWrapper(useWrapping = false)
    List<LocationXml> location;
  }

  @Setter
  protected static class LocationXml {
    @Getter
    String href;
    String id;
    String weight;
  }

}
