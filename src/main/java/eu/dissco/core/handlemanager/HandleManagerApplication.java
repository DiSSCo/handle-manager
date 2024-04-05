package eu.dissco.core.handlemanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@ConfigurationPropertiesScan
@EnableScheduling
public class HandleManagerApplication {

  public static void main(String[] args) {
    SpringApplication.run(HandleManagerApplication.class, args);
  }
}
