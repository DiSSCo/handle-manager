package eu.dissco.core.handlemanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;


@SpringBootApplication
@EnableCaching
public class HandleManagerApplication {

  public static void main(String[] args) {
    SpringApplication.run(HandleManagerApplication.class, args);
  }
}
