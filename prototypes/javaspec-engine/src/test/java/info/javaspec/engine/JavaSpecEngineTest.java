package info.javaspec.engine;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
      .execute();

    results.containerEvents().assertEventsMatchExactly(
      event(engine(), started()),
      event(engine(), finishedSuccessfully())
    );
  }

  @Nested
  @DisplayName("given a SpecClass without any specs in it")
  class givenASpecClassWithNoSpecs {
    @Test
    @DisplayName("does not run any containers for the spec class")
    public void runsNoContainerForTheClass() throws Exception {
      EngineExecutionResults results = EngineTestKit.engine("javaspec-engine")
        .selectors(selectClass(EmptySpecs.class))
        .execute();

      results.containerEvents().assertEventsMatchExactly(
        event(engine(), started()),
        event(engine(), finishedSuccessfully())
      );
    }

    @Test
    @DisplayName("does not run any tests for specs")
    public void runsNoTests() throws Exception {
      EngineExecutionResults results = EngineTestKit.engine("javaspec-engine")
        .selectors(selectClass(EmptySpecs.class))
        .execute();

      results.testEvents().assertEventsMatchExactly();
    }
  }

  @Nested
  @DisplayName("given a SpecClass with 1 or more specs")
  class givenASpecClassWithSpecs {
    @Test
    @DisplayName("runs a test for the class itself, within the engine's container")
    public void runsATestForTheClass() throws Exception {
      EngineExecutionResults results = EngineTestKit.engine("javaspec-engine")
        .selectors(selectClass(OneFlatSpecs.class))
        .execute();

      results.containerEvents().assertEventsMatchExactly(
        event(engine(), started()),
        event(container("class:info.javaspec.engine.OneFlatSpecs"), started()),
        event(container("class:info.javaspec.engine.OneFlatSpecs"), finishedSuccessfully()),
        event(engine(), finishedSuccessfully())
      );
    }

    @Test
    @DisplayName("runs a test for the spec, within the class's container")
    public void runsATestForTheSpec() throws Exception {
      EngineExecutionResults results = EngineTestKit.engine("javaspec-engine")
        .selectors(selectClass(OneFlatSpecs.class))
        .execute();

      results.allEvents().assertEventsMatchLooselyInOrder(
        event(container("class:info.javaspec.engine.OneFlatSpecs"), started()),
        event(test("spec:works"), started()),
        event(test("spec:works"), finishedSuccessfully()),
        event(container("class:info.javaspec.engine.OneFlatSpecs"), finishedSuccessfully())
      );
    }
  }

  @Test
  @Disabled
  @DisplayName("accepts spec classes that do not implement SpecClass")
  public void acceptsAnyClassWithSpecsInIt() throws Exception {
  }
}
