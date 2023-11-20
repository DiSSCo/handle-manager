package eu.dissco.core.handlemanager.configuration;

import eu.dissco.core.handlemanager.properties.DatabaseProperties;
import java.sql.DriverManager;
import java.sql.SQLException;
import lombok.RequiredArgsConstructor;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BatchInserterConfig {

  private final DatabaseProperties properties;

  @Bean
  public CopyManager copyManager() throws SQLException {
    var connection = DriverManager.getConnection(properties.getUrl(), properties.getUsername(),
        properties.getPassword());
    return new CopyManager((BaseConnection) connection);
  }

}
