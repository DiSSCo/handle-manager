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
import eu.dissco.core.handlemanager.domain.fdo.FdoType;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.FdoAttribute;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.FdoRecord;
import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class PidMongoRepository {

  private final MongoCollection<Document> collection;
  private final ObjectMapper mapper;
  private static String ID = "_id";

  public JsonNode getHandleRecord(String id)
      throws PidResolutionException, JsonProcessingException {
    var result = collection.find(eq(ID, id));
    if (result.first() == null) {
      throw new PidResolutionException("Unable to find handle " + id);
    }
    return mapper.readValue(result.first().toJson(), JsonNode.class);
  }

  public void postBatchHandleRecord(List<Document> handleRecords) {
    collection.insertMany(handleRecords);
  }

  public void updateHandleRecord(List<Document> handleRecords, List<String> ids) {
    var filter = in(ID, ids);
    var queryList = handleRecords.stream().map(doc -> new ReplaceOneModel<>(filter, doc)).toList();
    collection.bulkWrite(queryList);
  }

  public List<FdoRecord> getHandleRecords(List<String> ids) throws JsonProcessingException {
    var results = collection.find(in(ID, ids));
    return formatResults(results);
  }

  public List<FdoRecord> searchByPrimaryLocalId(String localIdField, List<String> localIds)
      throws JsonProcessingException {
    var results = collection.find(in(localIdField, localIds));
    return formatResults(results);
  }

  private List<FdoRecord> formatResults(FindIterable<Document> results)
      throws JsonProcessingException {
    var handleRecords = new ArrayList<FdoRecord>();
    for (var result : results) {
      var jsonRecord = mapper.readValue(result.toJson(), JsonNode.class);
      if (jsonRecord.get("values") == null) {
        log.warn("Unable to read handle record values \n {}",
            mapper.writeValueAsString(jsonRecord));
      } else {
        var attributes = mapper.convertValue(jsonRecord.get("values"),
            new TypeReference<List<FdoAttribute>>() {
            });
        var fdoType = getFdoType(attributes, jsonRecord.get("_id").asText());
        handleRecords.add(new FdoRecord(jsonRecord.get("_id").asText(),
            fdoType, attributes, getLocalId(jsonRecord, fdoType)));
      }
    }
    return handleRecords;
  }

  private FdoType getFdoType(List<FdoAttribute> fdoAttributes, String id) {
    for (var fdoAttribute : fdoAttributes) {
      if (DIGITAL_OBJECT_TYPE.index() == fdoAttribute.getIndex()) {
        return FdoType.fromString(fdoAttribute.getValue());
      }
    }
    log.error("Unable to determine Fdo Type for record of handle {}", id);
    throw new IllegalStateException();
  }

  private String getLocalId(JsonNode jsonRecord, FdoType fdoType) {
    if (FdoType.DIGITAL_SPECIMEN.equals(fdoType)) {
      return jsonRecord.get(NORMALISED_SPECIMEN_OBJECT_ID.get()).asText();
    }
    if (FdoType.MEDIA_OBJECT.equals(fdoType)) {
      return jsonRecord.get(PRIMARY_MEDIA_ID.get()).asText();
    }
    return null;
  }
}
