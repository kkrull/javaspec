package info.javaspec.engine;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.testkit.engine.EngineExecutionResults;
import org.junit.platform.testkit.engine.EngineTestKit;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.junit.platform.testkit.engine.EventConditions.*;

@DisplayName("JavaSpecEngine")
public class JavaSpecEngineTest {
  @Test
  @DisplayName("runs a test container for the engine itself")
  public void runsASpecForTheContainingClass() throws Exception {
    EngineExecutionResults results = EngineTestKit.engine("javaspec-engine")
      .selectors(selectClass(EmptySpecs.class))
      .execute();

    results.containerEvents().assertEventsMatchExactly(
      event(engine(), started()),
      event(engine(), finishedSuccessfully())
    );

    results.testEvents().assertEventsMatchExactly();
  }

  @Test
  @Disabled
  @DisplayName("accepts spec classes that do not implement SpecClass")
  public void acceptsAnyClassWithSpecsInIt() throws Exception {
  }
}
