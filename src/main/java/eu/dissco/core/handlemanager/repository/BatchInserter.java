package eu.dissco.core.handlemanager.repository;

import eu.dissco.core.handlemanager.domain.repsitoryobjects.HandleAttribute;
import eu.dissco.core.handlemanager.exceptions.DatabaseCopyException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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

  public void batchCopy(long recordTimestamp, List<HandleAttribute> handleAttributes) {
    try (var outputStream = new ByteArrayOutputStream()) {
      for (var row : handleAttributes) {
        outputStream.write(getCsvRow(recordTimestamp, row));
      }
      var inputStream = new ByteArrayInputStream(outputStream.toByteArray());
      copyManager.copyIn("COPY handles FROM stdin DELIMITER ','", inputStream);
    } catch (IOException | SQLException e) {
      log.error("Sql error: ", e);
      throw new DatabaseCopyException("Unable to insert new handles into database.");
    }
  }

  private static byte[] getCsvRow(Long recordTimestamp, HandleAttribute handleAttribute) {
    return (new String(handleAttribute.getHandle(), StandardCharsets.UTF_8) + ","
        + handleAttribute.getIndex() + ","
        + handleAttribute.getType() + ","
        + new String(handleAttribute.getData(), StandardCharsets.UTF_8).replace(",", "\\,")
        + ","
        + "0,"
        + "86400,"
        + recordTimestamp + ","
        + "\\N," // Leave refs null
        + true + ","
        + true + ","
        + true + ","
        + false + "\n").getBytes(StandardCharsets.UTF_8);
  }
}
