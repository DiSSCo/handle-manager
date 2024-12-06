package eu.dissco.core.handlemanager.configuration;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import eu.dissco.core.handlemanager.Profiles;
import eu.dissco.core.handlemanager.properties.MongoProperties;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@RequiredArgsConstructor
public class MongoConfig {

  private final MongoProperties properties;
  private final Environment environment;

  @Bean
  public MongoCollection<Document> getHandleCollection() {
    var client = MongoClients.create(properties.getConnectionString());
    var database = client.getDatabase(properties.getDatabase());
    var collection = environment.matchesProfiles(Profiles.DOI) ? "dois" : "handles";
    return database.getCollection(collection);
  }
}
