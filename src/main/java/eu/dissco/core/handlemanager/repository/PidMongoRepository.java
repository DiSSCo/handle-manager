package eu.dissco.core.handlemanager.repository;

import static com.mongodb.client.model.Filters.eq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PidMongoRepository {

  private final MongoCollection<Document> collection;
  private final ObjectMapper mapper;

  public JsonNode getHandleRecord(String id)
      throws PidResolutionException, JsonProcessingException {
    var result = collection.find(eq("_id", id));
    if (result.first() == null) {
      throw new PidResolutionException("Unable to find handle " + id);
    }
    return mapper.readValue(result.first().toJson(), JsonNode.class);
  }

  public void postSingleHandleRecord(JsonNode handleRecord, String id)
      throws JsonProcessingException {
    var doc = new Document("_id", id)
        .append("values", mapper.writeValueAsString(handleRecord));
    collection.insertOne(doc);
  }

  public void postBatchHandleRecord(List<Document> handleRecords) {
    collection.insertMany(handleRecords);
  }


}
