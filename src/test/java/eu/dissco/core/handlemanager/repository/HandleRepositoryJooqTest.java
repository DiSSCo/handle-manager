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


}
