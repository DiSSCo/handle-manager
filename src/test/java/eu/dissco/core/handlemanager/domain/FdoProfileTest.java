package eu.dissco.core.handlemanager.domain;

import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.PID_ISSUER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import eu.dissco.core.handlemanager.domain.fdo.FdoProfile;
import eu.dissco.core.handlemanager.exceptions.UnrecognizedFdoAttributeException;
import org.junit.jupiter.api.Test;

class FdoProfileTest {

  @Test
  void testSearchFdoProfileSuccess() {
    // Given
    var targetStr = PID_ISSUER.get();
    var expected = PID_ISSUER.index();

    // When
    var result = FdoProfile.retrieveIndex(targetStr);

    // Then
    assertThat(result).isEqualTo(expected);
  }

  @Test
  void testUnrecognizedProperty() {
    assertThrows(UnrecognizedFdoAttributeException.class,
        () -> FdoProfile.retrieveIndex("aaa"));
  }

}
