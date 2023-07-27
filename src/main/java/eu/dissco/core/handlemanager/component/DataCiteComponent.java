package eu.dissco.core.handlemanager.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@RequiredArgsConstructor
@Component
@Slf4j
public class DataCiteComponent {

  @Qualifier("datacite")
  WebClient webClient;

}
