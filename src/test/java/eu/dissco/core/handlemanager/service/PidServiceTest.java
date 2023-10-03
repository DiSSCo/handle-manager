package eu.dissco.core.handlemanager.service;

import static eu.dissco.core.handlemanager.testUtils.TestUtils.CREATED;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE_ALT;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

import eu.dissco.core.handlemanager.properties.ProfileProperties;
import eu.dissco.core.handlemanager.repository.HandleRepository;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
abstract class PidServiceTest {

  @Mock
  protected HandleRepository handleRep;
  @Mock
  protected FdoRecordService fdoRecordService;
  @Mock
  protected HandleGeneratorService hgService;
  @Mock
  protected ProfileProperties profileProperties;
  protected PidService service;
  protected List<byte[]> handles;
  private MockedStatic<Instant> mockedStatic;
  private MockedStatic<Clock> mockedClock;

  @BeforeEach
  void setup() {
    initTime();
    initHandleList();
  }

  private void initTime() {
    Clock clock = Clock.fixed(CREATED, ZoneOffset.UTC);
    Instant instant = Instant.now(clock);
    mockedStatic = mockStatic(Instant.class);
    mockedStatic.when(Instant::now).thenReturn(instant);
    mockedStatic.when(() -> Instant.from(any())).thenReturn(instant);
    mockedClock = mockStatic(Clock.class);
    mockedClock.when(Clock::systemUTC).thenReturn(clock);
  }

  private void initHandleList() {
    handles = new ArrayList<>();
    handles.add(HANDLE.getBytes(StandardCharsets.UTF_8));
    handles.add(HANDLE_ALT.getBytes(StandardCharsets.UTF_8));
  }

  @AfterEach
  void destroy() {
    mockedStatic.close();
    mockedClock.close();
  }


}
