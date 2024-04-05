package eu.dissco.core.handlemanager.configuration;

import java.sql.DriverManager;
import java.sql.SQLException;
import lombok.RequiredArgsConstructor;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BatchInserterConfig {

  private final DataSourceProperties properties;

  @Bean
  public CopyManager copyManager() throws SQLException {
    try (var connection = DriverManager.getConnection(properties.getUrl(), properties.getUsername(),
        properties.getPassword())) {
      return new CopyManager((BaseConnection) connection);
    }
  }
}
