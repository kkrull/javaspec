package info.javaspec.engine;

import static info.javaspec.engine.DiscoverySelectorFactory.nullDiscoverySelector;
import static info.javaspec.engine.EngineDiscoveryRequestFactory.classEngineDiscoveryRequest;
import static info.javaspec.engine.EngineDiscoveryRequestFactory.nullEngineDiscoveryRequest;
import static info.javaspec.engine.SpecClasses.notASpecClass;
import static info.javaspec.engine.SpecClasses.nullSpecClass;
import static info.javaspec.engine.SpecClasses.oneSpecClass;
import static info.javaspec.engine.SpecClasses.oneSpecThrowingAssertionError;
import static info.javaspec.engine.SpecClasses.oneSpecThrowingRuntimeException;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.junit.platform.testkit.engine.EventConditions.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestEngine;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.testkit.engine.EngineExecutionResults;
import org.junit.platform.testkit.engine.EngineTestKit;

import info.javaspec.api.JavaSpec;
import info.javaspec.api.SpecClass;

public class JavaSpecEngineV2Test implements SpecClass {
	@Override
	public void declareSpecs(JavaSpec javaspec) {
		javaspec.it("can be loaded with ServiceLoader and located by ID", () -> {
			EngineTestKit.engine("javaspec-engine-v2")
				.selectors(selectClass(nullSpecClass())).execute();
		});

		javaspec.it("#getId returns a unique ID", () -> {
			TestEngine subject = new JavaSpecEngineV2();
			assertEquals("javaspec-engine-v2", subject.getId());
		});
	}

	@DisplayName("#discover")
	@Nested
	class discover {
		@Test
		@DisplayName("returns a top-level container for itself, using the given UniqueId")
		public void returnsAContainerForTheEngine() throws Exception {
			JavaSpecEngineV2 subject = new JavaSpecEngineV2();
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
			JavaSpecEngineV2 subject = new JavaSpecEngineV2();

			TestDescriptor returned = subject.discover(nullEngineDiscoveryRequest(),
					UniqueId.forEngine(subject.getId()));
			assertEquals(Collections.emptySet(), returned.getChildren());
			assertFalse(returned.isTest());
		}

		@Test
		@DisplayName("ignores selectors for classes that are not SpecClass")
		public void ignoresNonSpecClassSelectors() throws Exception {
			JavaSpecEngineV2 subject = new JavaSpecEngineV2();
			TestDescriptor returned = subject.discover(classEngineDiscoveryRequest(notASpecClass()),
					UniqueId.forEngine(subject.getId()));
			assertEquals(Collections.emptySet(), returned.getChildren());
		}

		@Test
		@DisplayName("discovers a container for each selected spec class")
		public void selectOneClassYieldsOneContainer() throws Exception {
			JavaSpecEngineV2 subject = new JavaSpecEngineV2();
			Class<?> nullSpecClass = nullSpecClass();
			TestDescriptor returned = subject.discover(classEngineDiscoveryRequest(nullSpecClass),
					UniqueId.forEngine(subject.getId()));

			List<TestDescriptor> specClassDescriptors = new ArrayList<>(returned.getChildren());
			assertEquals(1, returned.getChildren().size());

			TestDescriptor onlyChild = specClassDescriptors.get(0);
			UniqueId.Segment idSegment = onlyChild.getUniqueId().getLastSegment();
			assertEquals("class", idSegment.getType());
			assertEquals(nullSpecClass.getName(), idSegment.getValue());

			assertEquals(nullSpecClass.getName(), onlyChild.getDisplayName());
			assertEquals(returned, onlyChild.getParent().orElseThrow());
			assertTrue(onlyChild.isContainer());
			assertFalse(onlyChild.isRoot());
			assertFalse(onlyChild.isTest());
		}

		@Test
		@DisplayName("discovers a test for each spec in a spec class")
		public void discoversATestForEachSpec() throws Exception {
			JavaSpecEngineV2 subject = new JavaSpecEngineV2();
			TestDescriptor returned = subject.discover(classEngineDiscoveryRequest(oneSpecClass()),
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
			EngineExecutionResults results = EngineTestKit.engine(new JavaSpecEngineV2())
					.selectors(nullDiscoverySelector()).execute();

			results.containerEvents().assertEventsMatchExactly(event(engine(), started()),
					event(engine(), finishedSuccessfully()));
		}

		@Test
		@DisplayName("skips spec class containers that don't have any specs in them")
		public void skipsSpecClassContainersWithoutAnySpecs() throws Exception {
			EngineExecutionResults results = EngineTestKit.engine(new JavaSpecEngineV2())
					.selectors(selectClass(nullSpecClass())).execute();

			results.containerEvents().assertEventsMatchExactly(event(engine(), started()),
					event(engine(), finishedSuccessfully()));
		}

		@Test
		@DisplayName("reports execution events for spec class containers")
		public void reportsSpecClassContainerEvents() throws Exception {
			EngineExecutionResults results = EngineTestKit.engine(new JavaSpecEngineV2())
					.selectors(selectClass(oneSpecClass())).execute();

			results.containerEvents().assertEventsMatchExactly(event(engine(), started()),
				event(container(), started()),
				event(container(), finishedSuccessfully()),
				event(engine(), finishedSuccessfully()));
		}

		@Test
		@DisplayName("reports start and successful finish events for passing specs")
		public void reportsSpecEventsForPassingSpecs() throws Exception {
			EngineExecutionResults results = EngineTestKit.engine(new JavaSpecEngineV2())
					.selectors(selectClass(oneSpecClass())).execute();

			results.allEvents().assertEventsMatchLooselyInOrder(
				event(container(), started()),
				event(test(), started()),
				event(test(), finishedSuccessfully()),
				event(container(), finishedSuccessfully())
			);
		}

		@Test
		@DisplayName("reports start and failed finish events for failing specs")
		public void reportsSpecEventsForFailingSpecs() throws Exception {
			EngineExecutionResults results = EngineTestKit.engine(new JavaSpecEngineV2())
					.selectors(selectClass(oneSpecThrowingRuntimeException())).execute();

			results.allEvents().assertEventsMatchLooselyInOrder(
				event(container(), started()),
				event(test(), started()),
				event(test(), finishedWithFailure(new Condition<Throwable>(t -> RuntimeException.class.isInstance(t), "RuntimeException"))),
				event(container(), finishedSuccessfully())
			);
		}

		@Test
		@DisplayName("catches specs that fail by throwing AssertionError")
		public void reportsSpecEventsWhenFailingWithAssertionError() throws Exception {
			EngineExecutionResults results = EngineTestKit.engine(new JavaSpecEngineV2())
					.selectors(selectClass(oneSpecThrowingAssertionError())).execute();

			results.allEvents().assertEventsMatchLooselyInOrder(
				event(container(), started()),
				event(test(), started()),
				event(test(), finishedWithFailure(new Condition<Throwable>(t -> AssertionError.class.isInstance(t), "AssertionError"))),
				event(container(), finishedSuccessfully())
			);
		}
	}
}
