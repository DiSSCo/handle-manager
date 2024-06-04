package eu.dissco.core.handlemanager.domain;

import static org.assertj.core.api.Assertions.assertThat;

import eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.TopicCategory;
import eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.TopicDiscipline;
import org.junit.jupiter.api.Test;

class TopicDisciplineTest {

  @Test
  void testAnthro() {
    // Given
    var targetDisc = TopicDiscipline.ANTHRO;
    var targetCategory = TopicCategory.HUMAN;
    var notTargetCategory = TopicCategory.ALGAE;

    // When
    assertThat(targetDisc.isCorrectCategory(targetCategory)).isTrue();
    assertThat(targetDisc.isCorrectCategory(notTargetCategory)).isFalse();
  }

  @Test
  void testBotany() {
    // Given
    var targetDisc = TopicDiscipline.BOTANY;
    var targetCategory = TopicCategory.MYCOLOGY;
    var notTargetCategory = TopicCategory.FINDS;

    // When
    assertThat(targetDisc.isCorrectCategory(targetCategory)).isTrue();
    assertThat(targetDisc.isCorrectCategory(notTargetCategory)).isFalse();
  }

  @Test
  void testGeology() {
    // Given
    var targetDisc = TopicDiscipline.GEOLOGY;
    var targetCategory = TopicCategory.MINERALS;
    var notTargetCategory = TopicCategory.FINDS;

    // When
    assertThat(targetDisc.isCorrectCategory(targetCategory)).isTrue();
    assertThat(targetDisc.isCorrectCategory(notTargetCategory)).isFalse();
  }

  @Test
  void testZoology() {
    // Given
    var targetDisc = TopicDiscipline.ZOO;
    var targetCategory = TopicCategory.AMPHIBIANS;
    var notTargetCategory = TopicCategory.FINDS;

    // When
    assertThat(targetDisc.isCorrectCategory(targetCategory)).isTrue();
    assertThat(targetDisc.isCorrectCategory(notTargetCategory)).isFalse();
  }

  @Test
  void testMicro() {
    // Given
    var targetDisc = TopicDiscipline.MICRO;
    var targetCategory = TopicCategory.PHAGES;
    var notTargetCategory = TopicCategory.FINDS;

    // When
    assertThat(targetDisc.isCorrectCategory(targetCategory)).isTrue();
    assertThat(targetDisc.isCorrectCategory(notTargetCategory)).isFalse();
  }

  @Test
  void testPaleo() {
    // Given
    var targetDisc = TopicDiscipline.PALEO;
    var targetCategory = TopicCategory.BOTANY_FOSSILS;
    var notTargetCategory = TopicCategory.FINDS;

    // When
    assertThat(targetDisc.isCorrectCategory(targetCategory)).isTrue();
    assertThat(targetDisc.isCorrectCategory(notTargetCategory)).isFalse();
  }

  @Test
  void testAstro() {
    // Given
    var targetDisc = TopicDiscipline.ASTRO;
    var targetCategory = TopicCategory.FINDS;
    var notTargetCategory = TopicCategory.LOOSE_SEDIMENT;

    // When
    assertThat(targetDisc.isCorrectCategory(targetCategory)).isTrue();
    assertThat(targetDisc.isCorrectCategory(notTargetCategory)).isFalse();
  }

  @Test
  void testDefault() {
    // Given
    var targetDisc = TopicDiscipline.ECO;

    // When
    assertThat(targetDisc.getTopicCategories()).isEmpty();
  }

}
