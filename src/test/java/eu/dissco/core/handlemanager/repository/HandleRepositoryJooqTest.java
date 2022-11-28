package eu.dissco.core.handlemanager.repository;

import java.util.Arrays;
import java.util.List;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class HandleRepositoryJooqTest {

  @Mock
  private DSLContext context;

  private HandleRepository jooqHandleRep;

  @BeforeEach
  void setup(){
    jooqHandleRep = new HandleRepository(context);

  }

  @Test
  void testMapToResponse(){
    byte[] h1 = "20.5000.1025/Z7Q-VKU-D3C".getBytes();
    byte[] h2 = "20.5000.1025/WT3-2DH-B59".getBytes();
    List<byte[]> handles = Arrays.asList(h1, h2);



  }



}
