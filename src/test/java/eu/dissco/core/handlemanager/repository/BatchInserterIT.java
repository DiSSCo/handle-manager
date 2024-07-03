package eu.dissco.core.handlemanager.repository;

import static eu.dissco.core.handlemanager.database.jooq.Tables.HANDLES;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.CREATED;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genDigitalSpecimenAttributes;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import eu.dissco.core.handlemanager.database.jooq.tables.Handles;
import eu.dissco.core.handlemanager.domain.fdo.FdoProfile;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.HandleAttribute;
import eu.dissco.core.handlemanager.exceptions.DatabaseCopyException;
import java.nio.charset.StandardCharsets;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;

class BatchInserterIT extends BaseRepositoryIT {

  private BatchInserter batchInserter;

  @BeforeEach
  void setup() throws SQLException {
    var connection = DriverManager.getConnection(dataSource.getJdbcUrl(), dataSource.getUsername(),
        dataSource.getPassword());
    var copyManager = new CopyManager((BaseConnection) connection);
    batchInserter = new BatchInserter(copyManager);
  }

  @AfterEach
  void destroy() {
    context.truncate(HANDLES).execute();
  }

  @Test
  void testBatchInsert() throws Exception {
    // Given
    var attributes = genDigitalSpecimenAttributes(HANDLE.getBytes(StandardCharsets.UTF_8));

    // When
    batchInserter.batchCopy(CREATED.getEpochSecond(), attributes);
    var response = context.select(Handles.HANDLES.IDX, Handles.HANDLES.HANDLE,
        Handles.HANDLES.TYPE, Handles.HANDLES.DATA).from(HANDLES).fetch(this::mapToAttribute);

    // Then
    assertThat(response).hasSameElementsAs(attributes);
  }

  @Test
  void testBatchInsertIllegalChar() {
    // Given
    var attributes = List.of(
        new HandleAttribute(FdoProfile.SPECIMEN_HOST,
            HANDLE.getBytes(StandardCharsets.UTF_8),
            "this is \n bad data")
    );
    var created = CREATED.getEpochSecond();

    // Then
    assertThrows(DatabaseCopyException.class,
        () -> batchInserter.batchCopy(created, attributes));
  }

  @Test
  void testDelimiterInData() throws Exception {
    var attributes = List.of(new HandleAttribute(
        FdoProfile.SPECIMEN_HOST, HANDLE.getBytes(StandardCharsets.UTF_8),
        "this, has a comma"
    ));

    // When
    batchInserter.batchCopy(CREATED.getEpochSecond(), attributes);
    var response = context.select(Handles.HANDLES.IDX, Handles.HANDLES.HANDLE,
        Handles.HANDLES.TYPE, Handles.HANDLES.DATA).from(HANDLES).fetch(this::mapToAttribute);

    // Then
    assertThat(response).isEqualTo(attributes);
  }

}
