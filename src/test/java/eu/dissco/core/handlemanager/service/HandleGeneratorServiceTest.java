package eu.dissco.core.handlemanager.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;

import eu.dissco.core.handlemanager.repository.HandleRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@Slf4j
class HandleGeneratorServiceTest {

  @Mock
  private HandleRepository handleRep;

  @Mock
  private Random random;

  @InjectMocks
  private HandleGeneratorService hgService;

  @Test
  void testNewHandle() {
    // Given
    String expectedHandle = "20.5000.1025/AAA-AAA-AAA";
    given(random.nextInt(anyInt())).willReturn(0);

    // When
    String newHandle = hgService.newHandle();

    // Then
    assertThat(newHandle).isEqualTo(expectedHandle);
  }

  @Test
  void testSingleBatchGen() {
    String expectedHandle = "20.5000.1025/AAA-AAA-AAA";
    given(random.nextInt(anyInt())).willReturn(0);

    // When
    String generatedHandle = new String(hgService.genHandleList(1).get(0));

    // Then
    assertThat(generatedHandle).isEqualTo(expectedHandle);
  }

  @Test
  void testBatchGen() {
    // Given
    String expectedHandle1 = "20.5000.1025/BBB-BBB-BBB";
    String expectedHandle2 = "20.5000.1025/ABB-BBB-BBB";

    given(random.nextInt(anyInt())).willReturn(0, 1);

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
    String expectedHandle1 = "20.5000.1025/BBB-BBB-BBB";
    String expectedHandle2 = "20.5000.1025/AAA-AAA-AAA";

    given(random.nextInt(anyInt())).willReturn(0, 0, 0, 0, 0, 0, 0, 0, 0)// First
        .willReturn(0, 0, 0, 0, 0, 0, 0, 0, 0) // Collision
        .willReturn(1);

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
    byte[] expectedHandle1 = "20.5000.1025/BBB-BBB-BBB".getBytes();
    byte[] expectedHandle2 = "20.5000.1025/ABB-BBB-BBB".getBytes();

    List<byte[]> handleListInternalDuplicate = new ArrayList<>();
    handleListInternalDuplicate.add(expectedHandle1);

    given(random.nextInt(anyInt())).willReturn(0, 1);
    given(handleRep.checkDuplicateHandles(any(List.class)))
        .willReturn(handleListInternalDuplicate)
        .willReturn(new ArrayList<>());

    // When
    List<byte[]> generatedHandleList = hgService.genHandleList(2);
    byte[] generatedHandle1 = generatedHandleList.get(0);
    byte[] generatedHandle2 = generatedHandleList.get(1);

    // Then
    assertThat(generatedHandle1).isEqualTo(expectedHandle1);
    assertThat(generatedHandle2).isEqualTo(expectedHandle2);
  }


}


