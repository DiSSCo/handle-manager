package eu.dissco.core.handlemanager.configuration;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import eu.dissco.core.handlemanager.properties.MongoProperties;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class MongoConfig {

  private final MongoProperties properties;

  @Bean
  public MongoCollection<Document> getHandleCollection() {
    var client = MongoClients.create(properties.getConnectionString());
    var database = client.getDatabase(properties.getDatabase());
    return database.getCollection(properties.getCollection());
  }
}
