package eu.dissco.core.handlemanager.repository;

import static eu.dissco.core.handlemanager.jooqobjects.Tables.MANUAL_PID;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE_ALT;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PREFIX;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import eu.dissco.core.handlemanager.properties.ApplicationProperties;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ManualPidRepositoryIT extends BaseRepositoryIT {

  private ManualPidRepository manualPidRepository;

  @Mock
  private ApplicationProperties applicationProperties;

  @BeforeEach
  void setUp() {
    manualPidRepository = new ManualPidRepository(applicationProperties, context);
    fillDatabase();
  }

  @AfterEach
  void destroy() {
    context.truncate(MANUAL_PID).execute();
  }

  @Test
  void testGetPids() {
    // Given
    given(applicationProperties.getPrefix()).willReturn(PREFIX);

    // When
    var result = manualPidRepository.getPids(2);

    // Then
    assertThat(result).isEqualTo(Set.of(HANDLE));
  }

  @Test
  void testDeleteTakenPids() {
    // When
    manualPidRepository.deleteTakenPids(Set.of(HANDLE));
    var result = context.select(MANUAL_PID.PID).from(MANUAL_PID).where(MANUAL_PID.PID.eq(HANDLE))
        .fetchInto(String.class);

    // Then
    assertThat(result).isEmpty();
  }

  private void fillDatabase() {
    context.insertInto(MANUAL_PID, MANUAL_PID.PID, MANUAL_PID.PREFIX)
        .values(HANDLE, PREFIX)
        .values(HANDLE_ALT, "SANDBOX")
        .execute();
  }


}
