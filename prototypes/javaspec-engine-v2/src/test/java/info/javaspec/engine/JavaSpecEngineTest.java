package info.javaspec.engine;

import static info.javaspec.engine.DiscoverySelectorFactory.nullDiscoverySelector;
import static info.javaspec.engine.EngineDiscoveryRequestFactory.classEngineDiscoveryRequest;
import static info.javaspec.engine.EngineDiscoveryRequestFactory.nullEngineDiscoveryRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.platform.testkit.engine.EventConditions.*;

import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.platform.engine.*;
import org.junit.platform.testkit.engine.EngineExecutionResults;
import org.junit.platform.testkit.engine.EngineTestKit;

public class JavaSpecEngineTest {
	@Test
	@DisplayName("can be loaded with ServiceLoader and located by ID")
	@Disabled
	public void isRegisteredWithServiceLoader() throws Exception {
	}

	@DisplayName("#discover")
	@Nested
	class discover {
		@Test
		@DisplayName("returns a top-level TestDescriptor for the engine, with the given UniqueId")
		public void returnsARootTestDescriptorForTheEngine() throws Exception {
			JavaSpecEngine subject = new JavaSpecEngine();

			UniqueId engineId = UniqueId.forEngine(subject.getId());
			TestDescriptor rootDescriptor = subject.discover(classEngineDiscoveryRequest(NullSpecClass.class),
					engineId);

			assertEquals(TestDescriptor.Type.CONTAINER, rootDescriptor.getType());
			assertEquals("JavaSpec", rootDescriptor.getDisplayName());
			assertEquals(engineId, rootDescriptor.getUniqueId());
			assertEquals(Optional.empty(), rootDescriptor.getParent());
		}

		@Test
		@DisplayName("the engine descriptor has no children, given no selectors")
		public void selectNoneYieldsNoChildren() throws Exception {
			JavaSpecEngine subject = new JavaSpecEngine();

			UniqueId engineId = UniqueId.forEngine(subject.getId());
			TestDescriptor rootDescriptor = subject.discover(nullEngineDiscoveryRequest(), engineId);
			assertEquals(Collections.emptySet(), rootDescriptor.getChildren());
		}

		@Test
		@DisplayName("the engine descriptor has a child for a selected spec class")
		@Disabled
		public void selectOneClassYieldsOneChild() throws Exception {
		}
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
					.selectors(nullDiscoverySelector()).execute();
			results.containerEvents().assertEventsMatchExactly(event(engine(), started()),
					event(engine(), finishedSuccessfully()));
		}
	}

	static final class NullSpecClass {
	}
}
