package info.javaspec.engine;

import static info.javaspec.engine.DiscoverySelectorFactory.nullDiscoverySelector;
import static info.javaspec.engine.EngineDiscoveryRequestFactory.classEngineDiscoveryRequest;
import static info.javaspec.engine.EngineDiscoveryRequestFactory.nullEngineDiscoveryRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.platform.testkit.engine.EventConditions.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestEngine;
import org.junit.platform.engine.UniqueId;
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
			TestDescriptor rootDescriptor = subject.discover(nullEngineDiscoveryRequest(), engineId);

			assertEquals(TestDescriptor.Type.CONTAINER, rootDescriptor.getType());
			assertEquals("JavaSpec", rootDescriptor.getDisplayName());
			assertEquals(engineId, rootDescriptor.getUniqueId());
			assertEquals(Optional.empty(), rootDescriptor.getParent());
		}

		@Test
		@DisplayName("the engine descriptor has no children, given no selectors")
		public void selectNoneYieldsNoChildren() throws Exception {
			JavaSpecEngine subject = new JavaSpecEngine();

			TestDescriptor rootDescriptor = subject.discover(nullEngineDiscoveryRequest(),
					UniqueId.forEngine(subject.getId()));
			assertEquals(Collections.emptySet(), rootDescriptor.getChildren());
		}

		@Test
		@DisplayName("the engine descriptor has a child for a selected spec class")
		public void selectOneClassYieldsOneChild() throws Exception {
			JavaSpecEngine subject = new JavaSpecEngine();
			TestDescriptor rootDescriptor = subject.discover(classEngineDiscoveryRequest(NullSpecClass.class),
				UniqueId.forEngine(subject.getId()));

			List<TestDescriptor> specClassDescriptors = new ArrayList(rootDescriptor.getChildren());
			assertEquals(1, rootDescriptor.getChildren().size());

			TestDescriptor specClassDescriptor = specClassDescriptors.get(0);
			assertEquals(rootDescriptor, specClassDescriptor.getParent().orElseThrow());
			assertEquals(TestDescriptor.Type.CONTAINER, specClassDescriptor.getType());
			assertEquals("NullSpecClass", specClassDescriptor.getDisplayName());
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
