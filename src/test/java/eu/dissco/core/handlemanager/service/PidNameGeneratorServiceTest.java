package eu.dissco.core.handlemanager.service;

import static eu.dissco.core.handlemanager.testUtils.TestUtils.PREFIX;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import eu.dissco.core.handlemanager.properties.ApplicationProperties;
import eu.dissco.core.handlemanager.repository.PidRepository;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

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
    lenient().when(applicationProperties.getPrefix()).thenReturn(PREFIX);

  }

  @Test
  void testBatchGen() {
    // Given
    given(random.nextInt(anyInt())).willReturn(0, 1);
    var expected = List.of(PREFIX + "/BBB-BBB-BBB", PREFIX + "/ABB-BBB-BBB");

    // When
    List<byte[]> handleList = pidNameGeneratorService.genHandleList(2);
    var result = handleList.stream().map(h -> new String(h, StandardCharsets.UTF_8));

    // Then
    assertThat(result).isEqualTo(expected);
  }

  @Test
  void testInternalCollision() {
    var expected = List.of(PREFIX + "/BBB-BBB-BBB", PREFIX + "/AAA-AAA-AAA");
    when(random.nextInt(anyInt())).thenAnswer(new Answer<>() {
      private int count = 0;

      public Object answer(InvocationOnMock invocation) {
        count = count + 1;
        if (count < 19) {
          return 0;
        }
        return 1;
      }
    });
    given(applicationProperties.getPrefix()).willReturn(PREFIX);

    // When
    List<byte[]> handleList = pidNameGeneratorService.genHandleList(2);
    var result = handleList.stream().map(h -> new String(h, StandardCharsets.UTF_8)).toList();

    // Then
    assertThat(result).isEqualTo(expected);
  }

  @Test
  void testDbCollision() {
    // Given
    var existingHandle = (PREFIX + "/BBB-BBB-BBB").getBytes(StandardCharsets.UTF_8);
    var expected = List.of(PREFIX + "/AAA-AAA-AAA", PREFIX + "/CCC-CCC-CCC");
    when(random.nextInt(anyInt())).thenAnswer(new Answer<>() {
      private int count = 0;

      public Object answer(InvocationOnMock invocation) {
        count = count + 1;
        if (count < 10) {
          return 0;
        } else if (count < 19) {
          return 1;
        }
        return 2;
      }
    });
    given(applicationProperties.getPrefix()).willReturn(PREFIX);
    given(pidRepository.getExistingHandles(anyList()))
        .willReturn(List.of(existingHandle))
        .willReturn(Collections.emptyList());

    // When
    var handleList = pidNameGeneratorService.genHandleList(2);
    var result = handleList.stream().map(h -> new String(h, StandardCharsets.UTF_8));

    // Then
    assertThat(result).hasSameElementsAs(expected);
  }

  @Test
  void testDbAndInternalCollision() {
    // Given
    var existingHandle = (PREFIX + "/BBB-BBB-BBB").getBytes(StandardCharsets.UTF_8);
    var expected = List.of(PREFIX + "/AAA-AAA-AAA", PREFIX + "/CCC-CCC-CCC");
    when(random.nextInt(anyInt())).thenAnswer(new Answer<>() {
      private int count = 0;

      public Object answer(InvocationOnMock invocation) {
        count = count + 1;
        if (count < 10) {
          return 0;
        } else if (count < 19) {
          return 1;
        } else if (count < 28) {
          return 0;
        }
        return 2;
      }
    });
    given(pidRepository.getExistingHandles(anyList()))
        .willReturn(List.of(existingHandle))
        .willReturn(Collections.emptyList());

    // When
    var handleList = pidNameGeneratorService.genHandleList(2);
    var result = handleList.stream().map(h -> new String(h, StandardCharsets.UTF_8));

    // Then
    assertThat(result).hasSameElementsAs(expected);
  }

  @Test
  void testInvalidNumberOfHandles() {
    // When
    var tooFew = pidNameGeneratorService.genHandleList(-1);

    // Then
    assertThat(tooFew).isEmpty();
  }

  @Test
  void testExceedsMax() {
    // Then
    assertThrows(InvalidRequestException.class,
        () -> pidNameGeneratorService.genHandleList(MAX_HANDLES + 1));
  }


}


