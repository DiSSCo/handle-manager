package eu.dissco.core.handlemanager.repository;

import eu.dissco.core.handlemanager.domain.repsitoryobjects.HandleAttribute;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.HandleFullRow;
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
  private static final byte[] HEADER = "handle,idx,type,data,ttl,timestamp,admin_read,admin_write,pub_read,pub_write".getBytes(
      StandardCharsets.UTF_8);

  public void batchCopy(List<HandleAttribute> handleAttributes, long recordTimestamp)
      throws IOException {
    var rows = handleAttributes.stream()
        .map(attribute -> new HandleFullRow(attribute, recordTimestamp)).toList();
    try (var outputStream = new ByteArrayOutputStream();) {
      outputStream.write(HEADER);
      for (var row : rows) {
        outputStream.write(row.getCsvRow());
      }
      var outputBytes = outputStream.toByteArray();
      var inputStream = new ByteArrayInputStream(outputBytes);
      copyManager.copyIn("COPY handles FROM STDIN (FORMAT csv, HEADER)", inputStream);
    } catch (SQLException e) {
      log.error("Sql error: ", e);
      throw new RuntimeException("Unable to create pid");
    }


  }


}
