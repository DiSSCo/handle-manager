package eu.dissco.core.handlemanager.repository;


import static eu.dissco.core.handlemanager.database.jooq.Tables.HANDLES;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;


import eu.dissco.core.handlemanager.domain.pidrecords.HandleAttribute;
import eu.dissco.core.handlemanager.domain.responses.HandleRecordResponse;
import eu.dissco.core.handlemanager.exceptions.PidCreationException;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
class HandleRepositoryIT extends BaseRepositoryIT{

  private HandleRepository handleRep;

  @BeforeEach
  void setup(){
    handleRep = new HandleRepository(context);
  }

  @AfterEach
  void destroy(){
    context.truncate(HANDLES).execute();
  }

  @Test
  void testCreateHandle() throws PidCreationException {
    // Given
    byte[] handle = HANDLE.getBytes();
    List<HandleAttribute> attributes = generateTestHandleAttributes(handle);
    HandleRecordResponse responseExpected = generateTestHandleResponse(handle);

    // When
    HandleRecordResponse responseReceived = handleRep.createHandle(handle, CREATED, attributes);

    // Then
    var postedRecord = context.selectFrom(HANDLES).fetch();
    assertThat(responseExpected).isEqualTo(responseReceived);
    assertThat(postedRecord).hasSize(attributes.size());
  }
}
