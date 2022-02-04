package info.javaspec.engine;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.testkit.engine.EngineTestKit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

@DisplayName("JavaSpecEngine")
public class JavaSpecEngineTest {
  @Test
  @DisplayName("loads EngineTestKit like a boss")
  public void loadsEngineTestKit() throws Exception {
    EngineTestKit.engine("javaspec-engine");
  }

  @Test
  @DisplayName("complies with the Jupiter Core API for TestEngines")
  public void compliesWithJupiterCore() throws Exception {
    EngineTestKit.engine("javaspec-engine")
      .selectors(selectClass(EmptySpecs.class))
      .execute()
      .containerEvents()
      .assertStatistics(stats -> stats.started(0).finished(0));
  }
}
