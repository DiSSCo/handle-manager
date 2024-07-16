package eu.dissco.core.handlemanager.service;

import static eu.dissco.core.handlemanager.testUtils.TestUtils.PREFIX;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;

import eu.dissco.core.handlemanager.properties.ApplicationProperties;
import eu.dissco.core.handlemanager.repository.PidRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PidNameGeneratorServiceTest {

  @Mock
  private PidRepository pidRepository;

  @Mock
  private Random random;

  @Mock
  ApplicationProperties applicationProperties;
  private static final int MAX_HANDLES = 1000;

  private PidNameGeneratorService pidNameGeneratorService;

  @BeforeEach
  void setup() {
    this.pidNameGeneratorService = new PidNameGeneratorService(applicationProperties, pidRepository,
        random);
    lenient().when(applicationProperties.getMaxHandles()).thenReturn(MAX_HANDLES);
  }

  @Test
  void testSingleBatchGen() {
    // Given
    String expectedHandle = PREFIX + "/AAA-AAA-AAA";
    given(random.nextInt(anyInt())).willReturn(0);
    given(applicationProperties.getPrefix()).willReturn(PREFIX);

    // When
    String generatedHandle = pidNameGeneratorService.genHandleList(1).get(0);

    // Then
    assertThat(generatedHandle).isEqualTo(expectedHandle);
  }

  @Test
  void testBatchGen() {
    // Given
    var expected = List.of(PREFIX + "/ABB-BBB-BBB", PREFIX + "/BBB-BBB-BBB");

    given(random.nextInt(anyInt())).willReturn(0, 1);
    given(applicationProperties.getPrefix()).willReturn(PREFIX);

    // When
    var handleList = pidNameGeneratorService.genHandleList(2);

    // Then
    assertThat(handleList).isEqualTo(expected);
  }

  @Test
  void testInternalCollision() {
    var expected = List.of(PREFIX + "/AAA-AAA-AAA", PREFIX + "/BBB-BBB-BBB");

    given(random.nextInt(anyInt())).willReturn(0, 0, 0, 0, 0, 0, 0, 0, 0)// First
        .willReturn(0, 0, 0, 0, 0, 0, 0, 0, 0) // Collision
        .willReturn(1);
    given(applicationProperties.getPrefix()).willReturn(PREFIX);

    // When
    var result = pidNameGeneratorService.genHandleList(2);

    // Then
    assertThat(expected).isEqualTo(result);
  }

  @Test
  void testDbCollision() {
    // Given
    var expectedHandle1 = PREFIX + "/BBB-BBB-BBB";
    var expectedHandle2 = PREFIX + "/ABB-BBB-BBB";
    given(random.nextInt(anyInt())).willReturn(0, 1);
    given(pidRepository.getExistingHandles(anyList()))
        .willReturn(List.of(expectedHandle1))
        .willReturn(new ArrayList<>());
    given(applicationProperties.getPrefix()).willReturn(PREFIX);

    // When
    var result = pidNameGeneratorService.genHandleList(2);

    // Then
    assertThat(result).hasSameElementsAs(List.of(expectedHandle1, expectedHandle2));
  }

  @Test
  void testInvalidNumberOfHandles() {
    // When
    var tooFew = pidNameGeneratorService.genHandleList(-1);

    // Then
    assertThat(tooFew).isEmpty();
  }

}


