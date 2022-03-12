package info.javaspec.engine;

import static info.javaspec.engine.DiscoverySelectorFactory.nullDiscoverySelector;
import static info.javaspec.engine.EngineDiscoveryRequestFactory.classEngineDiscoveryRequest;
import static info.javaspec.engine.EngineDiscoveryRequestFactory.nullEngineDiscoveryRequest;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.junit.platform.testkit.engine.EventConditions.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import info.javaspec.api.JavaSpec;
import info.javaspec.api.SpecClass;
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
	@Disabled("wait until it can run specs, because enabling this will make it run for this project too")
	public void isRegisteredWithServiceLoader() throws Exception {
	}

	@DisplayName("#discover")
	@Nested
	class discover {
		@Test
		@DisplayName("returns a top-level container for itself, using the given UniqueId")
		public void returnsAContainerForTheEngine() throws Exception {
			JavaSpecEngine subject = new JavaSpecEngine();
			UniqueId engineId = UniqueId.forEngine(subject.getId());

			TestDescriptor returned = subject.discover(nullEngineDiscoveryRequest(), engineId);
			assertEquals(engineId, returned.getUniqueId());
			assertEquals("JavaSpec", returned.getDisplayName());
			assertEquals(Optional.empty(), returned.getParent());
			assertTrue(returned.isContainer());
			assertTrue(returned.isRoot());
		}

		@Test
		@DisplayName("discovers no containers or tests, given no selectors")
		public void selectNoneYieldsNoChildren() throws Exception {
			JavaSpecEngine subject = new JavaSpecEngine();

			TestDescriptor returned = subject.discover(nullEngineDiscoveryRequest(),
					UniqueId.forEngine(subject.getId()));
			assertEquals(Collections.emptySet(), returned.getChildren());
			assertFalse(returned.isTest());
		}

		@Test
		@DisplayName("ignores selectors for classes that are not SpecClass")
		public void ignoresNonSpecClassSelectors() throws Exception {
			JavaSpecEngine subject = new JavaSpecEngine();
			TestDescriptor returned = subject.discover(classEngineDiscoveryRequest(NotASpecClass.class),
					UniqueId.forEngine(subject.getId()));
			assertEquals(Collections.emptySet(), returned.getChildren());
		}

		@Test
		@DisplayName("discovers a container for each selected spec class")
		public void selectOneClassYieldsOneContainer() throws Exception {
			JavaSpecEngine subject = new JavaSpecEngine();
			TestDescriptor returned = subject.discover(classEngineDiscoveryRequest(NullSpecClass.class),
					UniqueId.forEngine(subject.getId()));

			List<TestDescriptor> specClassDescriptors = new ArrayList<>(returned.getChildren());
			assertEquals(1, returned.getChildren().size());

			TestDescriptor onlyChild = specClassDescriptors.get(0);
			UniqueId.Segment idSegment = onlyChild.getUniqueId().getLastSegment();
			assertEquals("class", idSegment.getType());
			assertEquals("info.javaspec.engine.JavaSpecEngineTest$NullSpecClass", idSegment.getValue());

			assertEquals("info.javaspec.engine.JavaSpecEngineTest$NullSpecClass", onlyChild.getDisplayName());
			assertEquals(returned, onlyChild.getParent().orElseThrow());
			assertTrue(onlyChild.isContainer());
			assertFalse(onlyChild.isRoot());
			assertFalse(onlyChild.isTest());
		}

		@Test
		@DisplayName("discovers a test for each spec in a spec class")
		public void discoversATestForEachSpec() throws Exception {
			JavaSpecEngine subject = new JavaSpecEngine();
			TestDescriptor returned = subject.discover(classEngineDiscoveryRequest(OneSpecClass.class),
					UniqueId.forEngine(subject.getId()));

			TestDescriptor specClassDescriptor = returned.getChildren().iterator().next();
			assertEquals(1, specClassDescriptor.getChildren().size());

			TestDescriptor specDescriptor = specClassDescriptor.getChildren().iterator().next();
			UniqueId.Segment idSegment = specDescriptor.getUniqueId().getLastSegment();
			assertEquals("test", idSegment.getType());
			assertEquals("one spec", idSegment.getValue());

			assertEquals(specClassDescriptor, specDescriptor.getParent().orElseThrow());
			assertFalse(specDescriptor.isContainer());
			assertFalse(specDescriptor.isRoot());
			assertTrue(specDescriptor.isTest());
		}
	}

	@DisplayName("#execute")
	@Nested
	class execute {
		@Test
		@DisplayName("reports execution events for the engine")
		public void reportsEngineEvents() throws Exception {
			EngineExecutionResults results = EngineTestKit.engine(new JavaSpecEngine())
					.selectors(nullDiscoverySelector()).execute();
			results.containerEvents().assertEventsMatchExactly(event(engine(), started()),
					event(engine(), finishedSuccessfully()));
		}

		@Test
		@DisplayName("skips spec class containers that don't have any specs in them")
		public void skipsSpecClassContainersWithoutAnySpecs() throws Exception {
			EngineExecutionResults results = EngineTestKit.engine(new JavaSpecEngine())
					.selectors(selectClass(NullSpecClass.class)).execute();
			results.containerEvents().assertEventsMatchExactly(event(engine(), started()),
					event(engine(), finishedSuccessfully()));
		}

		@Test
		@DisplayName("reports execution events for spec class containers")
		public void reportsSpecClassContainerEvents() throws Exception {
			EngineExecutionResults results = EngineTestKit.engine(new JavaSpecEngine())
					.selectors(selectClass(OneSpecClass.class)).execute();
			results.containerEvents().assertEventsMatchExactly(event(engine(), started()),
				event(container(OneSpecClass.class), started()),
				event(container(OneSpecClass.class), finishedSuccessfully()),
				event(engine(), finishedSuccessfully()));
		}

		@Test
		@DisplayName("reports start and successful finish events for passing specs")
		public void reportsSpecEventsForPassingSpecs() throws Exception {
			EngineExecutionResults results = EngineTestKit.engine(new JavaSpecEngine())
					.selectors(selectClass(OneSpecClass.class)).execute();
			results.allEvents().assertEventsMatchLooselyInOrder(
				event(container(OneSpecClass.class), started()),
				event(test(), started()),
				event(test(), finishedSuccessfully()),
				event(container(OneSpecClass.class), finishedSuccessfully())
			);
		}

		@Test
		@DisplayName("reports start and failed finish events for failing specs")
		public void reportsSpecEventsForFailingSpecs() throws Exception {
			EngineExecutionResults results = EngineTestKit.engine(new JavaSpecEngine())
					.selectors(selectClass(OneSpecWithRuntimeException.class)).execute();
			results.allEvents().assertEventsMatchLooselyInOrder(
				event(container(OneSpecWithRuntimeException.class), started()),
				event(test(), started()),
				event(test(), finishedWithFailure()),
				event(container(OneSpecWithRuntimeException.class), finishedSuccessfully())
			);
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

	static final class NotASpecClass {
	}

	static final class NullSpecClass implements SpecClass {
		@Override
		public void declareSpecs(JavaSpec javaspec) {
			//Do nothing
		}
	}

	static final class OneSpecClass implements SpecClass {
		public void declareSpecs(JavaSpec javaSpec) {
			javaSpec.it("one spec", () -> {
				assertEquals(2, 1 + 1);
			});
		}
	}

	static final class OneSpecWithRuntimeException implements SpecClass {
		public void declareSpecs(JavaSpec javaSpec) {
			javaSpec.it("throws", () -> {
				throw new RuntimeException("bang!");
			});
		}
	}
}
