package eu.dissco.core.handlemanager.service;

import static eu.dissco.core.handlemanager.testUtils.TestUtils.PREFIX;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;

import eu.dissco.core.handlemanager.properties.ApplicationProperties;
import eu.dissco.core.handlemanager.repository.PidRepository;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HandleGeneratorServiceTest {

  @Mock
  private PidRepository pidRepository;

  @Mock
  private Random random;

  @Mock
  ApplicationProperties applicationProperties;
  private static final int MAX_HANDLES = 1000;

  private HandleGeneratorService hgService;

  @BeforeEach
  void setup() {
    this.hgService = new HandleGeneratorService(applicationProperties, pidRepository, random);
    lenient().when(applicationProperties.getMaxHandles()).thenReturn(MAX_HANDLES);
  }

  @Test
  void testSingleBatchGen() {
    // Given
    String expectedHandle = PREFIX + "/AAA-AAA-AAA";
    given(random.nextInt(anyInt())).willReturn(0);
    given(applicationProperties.getPrefix()).willReturn(PREFIX);

    // When
    String generatedHandle = new String(hgService.genHandleList(1).get(0), StandardCharsets.UTF_8);

    // Then
    assertThat(generatedHandle).isEqualTo(expectedHandle);
  }

  @Test
  void testBatchGen() {
    // Given
    String expectedHandle1 = PREFIX + "/BBB-BBB-BBB";
    String expectedHandle2 = PREFIX + "/ABB-BBB-BBB";

    given(random.nextInt(anyInt())).willReturn(0, 1);
    given(applicationProperties.getPrefix()).willReturn(PREFIX);

    // When
    List<byte[]> handleList = hgService.genHandleList(2);
    String generatedHandle1 = new String(handleList.get(0));
    String generatedHandle2 = new String(handleList.get(1));

    // Then
    assertThat(generatedHandle1).isEqualTo(expectedHandle1);
    assertThat(generatedHandle2).isEqualTo(expectedHandle2);
  }

  @Test
  void testInternalCollision() {
    String expectedHandle1 = PREFIX + "/BBB-BBB-BBB";
    String expectedHandle2 = PREFIX + "/AAA-AAA-AAA";

    given(random.nextInt(anyInt())).willReturn(0, 0, 0, 0, 0, 0, 0, 0, 0)// First
        .willReturn(0, 0, 0, 0, 0, 0, 0, 0, 0) // Collision
        .willReturn(1);
    given(applicationProperties.getPrefix()).willReturn(PREFIX);

    // When
    List<byte[]> handleList = hgService.genHandleList(2);
    String generatedHandle1 = new String(handleList.get(0));
    String generatedHandle2 = new String(handleList.get(1));

    // Then
    assertThat(generatedHandle1).isEqualTo(expectedHandle1);
    assertThat(generatedHandle2).isEqualTo(expectedHandle2);
  }

  @Test
  void testDbCollision() {
    // Given
    byte[] expectedHandle1 = (PREFIX + "/BBB-BBB-BBB").getBytes(StandardCharsets.UTF_8);
    byte[] expectedHandle2 = (PREFIX + "/ABB-BBB-BBB").getBytes(StandardCharsets.UTF_8);

    List<byte[]> handleListInternalDuplicate = new ArrayList<>();
    handleListInternalDuplicate.add(expectedHandle1);

    given(random.nextInt(anyInt())).willReturn(0, 1);
    given(pidRepository.getHandlesExist(anyList()))
        .willReturn(handleListInternalDuplicate)
        .willReturn(new ArrayList<>());
    given(applicationProperties.getPrefix()).willReturn(PREFIX);

    // When
    List<byte[]> generatedHandleList = hgService.genHandleList(2);
    byte[] generatedHandle1 = generatedHandleList.get(0);
    byte[] generatedHandle2 = generatedHandleList.get(1);

    // Then
    assertThat(generatedHandle1).isEqualTo(expectedHandle1);
    assertThat(generatedHandle2).isEqualTo(expectedHandle2);
  }

  @Test
  void testInvalidNumberOfHandles() {
    // When
    var tooFew = hgService.genHandleList(-1);

    // Then
    assertThat(tooFew).isEmpty();
  }


}


