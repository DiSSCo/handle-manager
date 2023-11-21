package eu.dissco.core.handlemanager.repository;

import eu.dissco.core.handlemanager.domain.repsitoryobjects.HandleAttribute;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.HandleFullRow;
import eu.dissco.core.handlemanager.exceptions.PidCreationException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.copy.CopyManager;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BatchInserter {

  private final CopyManager copyManager;

  public void batchCopy(long recordTimestamp, List<HandleAttribute> handleAttributes)
      throws PidCreationException {
    var rows = handleAttributes.stream()
        .map(attribute -> new HandleFullRow(attribute, recordTimestamp)).toList();
    try (var outputStream = new ByteArrayOutputStream()) {
      for (var row : rows) {
        outputStream.write(row.getCsvRow());
      }
      var inputStream = new ByteArrayInputStream(outputStream.toByteArray());
      copyManager.copyIn("COPY handles FROM stdin DELIMITER ','", inputStream);
    } catch (IOException | SQLException e) {
      log.error("Sql error: ", e);
      throw new PidCreationException("Unable to insert handles into database.");
    }
  }
}
