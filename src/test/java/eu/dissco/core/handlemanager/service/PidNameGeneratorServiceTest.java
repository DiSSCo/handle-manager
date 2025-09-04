package eu.dissco.core.handlemanager.service;

import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE_ALT;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PREFIX;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;

import eu.dissco.core.handlemanager.properties.ApplicationProperties;
import eu.dissco.core.handlemanager.repository.MongoRepository;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PidNameGeneratorServiceTest {

  @Mock
  private MongoRepository mongoRepository;

  @Mock
  private Random random;

  @Mock
  ApplicationProperties applicationProperties;

  private static final String FILE_NAME = "reserved-pids/dois.txt";
  private static final String LAST_PID = PREFIX + "/" + "111-111-111";
  private static final int MAX_HANDLES = 1000;

  private PidNameGeneratorService pidNameGeneratorService;

  @BeforeEach
  void setup() {
    this.pidNameGeneratorService = new PidNameGeneratorService(applicationProperties,
        mongoRepository,
        random);
    lenient().when(applicationProperties.getMaxHandles()).thenReturn(MAX_HANDLES);
  }

  private static void writeFile() throws IOException {
    var f = new File(FILE_NAME);
    f.createNewFile();
    try (var writer = new FileWriter(FILE_NAME)) {
      writer.write(HANDLE + "\n");
      writer.write(HANDLE_ALT + "\n");
      writer.write(LAST_PID + "\n");
    }
  }

  private static void cleanupFile() throws IOException {
    Files.delete(Paths.get(FILE_NAME));
  }

  @Test
  void testSingleBatchGen() {
    // Given
    var expected = Set.of(PREFIX + "/AAA-AAA-AAA");
    given(random.nextInt(anyInt())).willReturn(0);
    given(applicationProperties.getPrefix()).willReturn(PREFIX);

    // When
    var result = pidNameGeneratorService.generateNewHandles(1);

    // Then
    assertThat(result).isEqualTo(expected);
  }

  @Test
  void testGenerateManualPids() throws IOException {
    // Given
    writeFile();
    var expected = Set.of(HANDLE, HANDLE_ALT);
    given(applicationProperties.getManualPidFile()).willReturn(FILE_NAME);

    // When
    var result = pidNameGeneratorService.generateNewHandles(2);

    // Then
    assertThat(result).isEqualTo(expected);
    assertThat(fileHasExpectedSize(1)).isTrue();
    cleanupFile();
  }

  @Test
  void testGenerateManualPidsAndRandom() throws IOException {
    // Given
    writeFile();
    var expected = Set.of(HANDLE, HANDLE_ALT, LAST_PID, PREFIX + "/AAA-AAA-AAA");
    given(applicationProperties.getManualPidFile()).willReturn(FILE_NAME);
    given(random.nextInt(anyInt())).willReturn(0);
    given(applicationProperties.getPrefix()).willReturn(PREFIX);

    // When
    var result = pidNameGeneratorService.generateNewHandles(4);

    // Then
    assertThat(result).isEqualTo(expected);
    assertThat(fileHasExpectedSize(0)).isTrue();
    cleanupFile();
  }

  private static boolean fileHasExpectedSize(int size) throws IOException {
    try (var fileStream = Files.lines(Paths.get(FILE_NAME))) {
      return (int) fileStream.count() == size;
    }
  }

  @Test
  void testBatchGen() {
    // Given
    var expected = Set.of(PREFIX + "/ABB-BBB-BBB", PREFIX + "/BBB-BBB-BBB");

    given(random.nextInt(anyInt())).willReturn(0, 1);
    given(applicationProperties.getPrefix()).willReturn(PREFIX);

    // When
    var handleList = pidNameGeneratorService.generateNewHandles(2);

    // Then
    assertThat(handleList).isEqualTo(expected);
  }

  @Test
  void testInternalCollision() {
    var expected = Set.of(PREFIX + "/AAA-AAA-AAA", PREFIX + "/BBB-BBB-BBB");

    given(random.nextInt(anyInt())).willReturn(0, 0, 0, 0, 0, 0, 0, 0, 0)// First
        .willReturn(0, 0, 0, 0, 0, 0, 0, 0, 0) // Collision
        .willReturn(1);
    given(applicationProperties.getPrefix()).willReturn(PREFIX);

    // When
    var result = pidNameGeneratorService.generateNewHandles(2);

    // Then
    assertThat(expected).isEqualTo(result);
  }

  @Test
  void testDbCollision() {
    // Given
    var expectedHandle1 = PREFIX + "/BBB-BBB-BBB";
    var expectedHandle2 = PREFIX + "/ABB-BBB-BBB";
    given(random.nextInt(anyInt())).willReturn(0, 1);
    given(mongoRepository.getExistingHandles(anyList()))
        .willReturn(List.of(expectedHandle1))
        .willReturn(new ArrayList<>());
    given(applicationProperties.getPrefix()).willReturn(PREFIX);

    // When
    var result = pidNameGeneratorService.generateNewHandles(2);

    // Then
    assertThat(result).hasSameElementsAs(List.of(expectedHandle1, expectedHandle2));
  }

  @Test
  void testInvalidNumberOfHandles() {
    // When
    var tooFew = pidNameGeneratorService.generateNewHandles(-1);

    // Then
    assertThat(tooFew).isEmpty();
  }

}


