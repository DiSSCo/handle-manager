package eu.dissco.core.handlemanager.controller;

import com.fasterxml.jackson.databind.JsonNode;
import eu.dissco.core.handlemanager.service.DataCiteService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dois")
@RequiredArgsConstructor
@ControllerAdvice
@Slf4j
public class DataCiteController {
  private final DataCiteService dataCiteService;

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<JsonNode>> registerDoi(@RequestBody List<String> handles){
    return ResponseEntity.status(HttpStatus.OK).body(dataCiteService.registerDoi(handles));
  }


}
