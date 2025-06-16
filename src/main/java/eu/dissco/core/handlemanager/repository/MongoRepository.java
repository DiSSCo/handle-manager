package eu.dissco.core.handlemanager.repository;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.in;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.DIGITAL_OBJECT_TYPE;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.NORMALISED_SPECIMEN_OBJECT_ID;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.PRIMARY_MEDIA_ID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.ReplaceOneModel;
import eu.dissco.core.handlemanager.domain.fdo.FdoProfile;
import eu.dissco.core.handlemanager.domain.fdo.FdoType;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.FdoAttribute;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.FdoRecord;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class MongoRepository {

  private final MongoCollection<Document> collection;
  private final ObjectMapper mapper;
  private static final String ID = "_id";
  private static final String VALUES = "values";

  public List<FdoRecord> getHandleRecords(List<String> ids) throws JsonProcessingException {
    var results = collection.find(in(ID, ids));
    return formatResults(results);
  }

  public List<String> getExistingHandles(List<String> ids) {
    var results = collection.find(in(ID, ids));
    if (results.first() == null) {
      return Collections.emptyList();
    }
    var existingHandles = new ArrayList<String>();
    results.forEach(r -> existingHandles.add(r.get(ID).toString()));
    log.info("Collision of size {} occurred in generating new handles", existingHandles.size());
    return existingHandles;
  }

  public void postHandleRecords(List<Document> handleRecords) {
    log.info("Posting {} new records to database", handleRecords.size());
    if (!handleRecords.isEmpty()) {
      collection.insertMany(handleRecords);
    }
  }

  public void updateHandleRecords(List<Document> handleRecords) {
    var queryList = handleRecords.stream().map(doc -> {
      var filter = eq(doc.get(ID));
      return new ReplaceOneModel<>(filter, doc);
    }).toList();
    log.info("Updating {} records to database", handleRecords.size());
    if (!queryList.isEmpty()) {
      collection.bulkWrite(queryList);
    }
  }

  public List<FdoRecord> searchByPrimaryLocalId(String localIdField, List<String> localIds)
      throws JsonProcessingException {
    var results = collection.find(in(localIdField, localIds));
    return formatResults(results);
  }

  public void rollbackHandles(List<String> ids) {
    var filter = in(ID, ids);
    collection.deleteMany(filter);
  }

  public long rollbackHandlesFromLocalId(String localIdField, List<String> localIds) {
    var filter = in(localIdField, localIds);
    return collection.deleteMany(filter).getDeletedCount();
  }

  private List<FdoRecord> formatResults(FindIterable<Document> results)
      throws JsonProcessingException {
    var handleRecords = new ArrayList<FdoRecord>();
    for (var result : results) {
      var jsonRecord = mapper.readValue(result.toJson(), JsonNode.class);
      if (jsonRecord.get(VALUES) == null) {
        log.warn("Unable to read handle record values \n {}",
            mapper.writeValueAsString(jsonRecord));
      } else {
        var attributes = mapper.convertValue(jsonRecord.get(VALUES),
            new TypeReference<Collection<FdoAttribute>>() {
            });
        var attributeMap = buildAttributeMap(attributes);
        var fdoType = FdoType.fromString(attributeMap.get(DIGITAL_OBJECT_TYPE).getValue());
        handleRecords.add(new FdoRecord(jsonRecord.get(ID).asText(),
            fdoType, attributeMap, getLocalId(jsonRecord, fdoType), attributes));
      }
    }
    return handleRecords;
  }

  private static Map<FdoProfile, FdoAttribute> buildAttributeMap(
      Collection<FdoAttribute> attributeList) {
    return attributeList.stream()
        .collect(Collectors.toMap(
            a -> FdoProfile.fromString(a.getType()),
            Function.identity(),
            (oldVal, newVal) -> oldVal
        ));
  }

  private String getLocalId(JsonNode jsonRecord, FdoType fdoType) {
    if (FdoType.DIGITAL_SPECIMEN.equals(fdoType)) {
      if (jsonRecord.get(NORMALISED_SPECIMEN_OBJECT_ID.get()) != null) {
        return jsonRecord.get(NORMALISED_SPECIMEN_OBJECT_ID.get()).asText();
      } else return getLocalIdSearchJson(jsonRecord, NORMALISED_SPECIMEN_OBJECT_ID);

    }
    if (FdoType.DIGITAL_MEDIA.equals(fdoType)) {
      if (jsonRecord.get(PRIMARY_MEDIA_ID.get())!=null) {
        return jsonRecord.get(PRIMARY_MEDIA_ID.get()).asText();
      } else return getLocalIdSearchJson(jsonRecord, PRIMARY_MEDIA_ID);
    }
    return null;
  }

  private String getLocalIdSearchJson(JsonNode jsonRecord, FdoProfile targetTerm) {
    try {
      for (var value : jsonRecord.get(VALUES)) {
        if (targetTerm.get().equals(value.get("type").asText())) {
          return value.get("data").get("value").asText();
        }
      }
    } catch (NullPointerException e) {
      log.warn("Invalid FDO record {}", jsonRecord.get(ID).asText());
    }
    return null;
  }
}
