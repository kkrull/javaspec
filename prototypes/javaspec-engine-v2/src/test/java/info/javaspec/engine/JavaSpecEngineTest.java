package info.javaspec.engine;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.platform.engine.DiscoverySelector;
import org.junit.platform.engine.TestEngine;
import org.junit.platform.testkit.engine.EngineExecutionResults;
import org.junit.platform.testkit.engine.EngineTestKit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.platform.testkit.engine.EventConditions.*;

@Nested
public class JavaSpecEngineTest {
	@Test
	@DisplayName("can be loaded with ServiceLoader and located by ID")
	@Disabled
	public void isRegisteredWithServiceLoader() throws Exception {
	}

	@Test
	@DisplayName("returns an unique ID")
	public void identifiesItselfAsTheEngineForJavaSpec() throws Exception {
		TestEngine subject = new JavaSpecEngine();
		assertEquals("javaspec-engine-v2", subject.getId());
	}

	@Test
	@DisplayName("runs as a TestEngine")
	public void runsAsATestEngine() throws Exception {
		DiscoverySelector nullSelector = new DiscoverySelector() {
		};
		EngineExecutionResults results = EngineTestKit.engine(new JavaSpecEngine()).selectors(nullSelector).execute();
		results.containerEvents().assertEventsMatchExactly(event(engine(), started()),
				event(engine(), finishedSuccessfully()));
	}
}
