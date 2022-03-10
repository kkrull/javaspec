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

public class JavaSpecEngineTest {
	@Test
	@DisplayName("can be loaded with ServiceLoader and located by ID")
	@Disabled
	public void isRegisteredWithServiceLoader() throws Exception {
	}

	@DisplayName("#getId")
	@Nested
	class getId {
		@Test
		@DisplayName("returns an unique ID")
		public void identifiesItselfAsTheEngineForJavaSpec() throws Exception {
			TestEngine subject = new JavaSpecEngine();
			assertEquals("javaspec-engine-v2", subject.getId());
		}
	}

	@DisplayName("TestEngine discovery and execution")
	@Nested
	class testEngineExecution {
		@Test
		@DisplayName("reports execution events for the engine")
		public void runsAsATestEngine() throws Exception {
			EngineExecutionResults results = EngineTestKit.engine(new JavaSpecEngine())
					.selectors(new NullDiscoverySelector()).execute();
			results.containerEvents().assertEventsMatchExactly(event(engine(), started()),
					event(engine(), finishedSuccessfully()));
		}
	}

	static final class NullDiscoverySelector implements DiscoverySelector {
	}
}
