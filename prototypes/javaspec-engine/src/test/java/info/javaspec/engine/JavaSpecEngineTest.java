package info.javaspec.engine;

import static info.javaspec.engine.AnonymousSpecClasses.*;
import static info.javaspec.engine.DiscoverySelectorFactory.nullDiscoverySelector;
import static info.javaspec.engine.EngineDiscoveryRequestFactory.classEngineDiscoveryRequest;
import static info.javaspec.engine.EngineDiscoveryRequestFactory.nullEngineDiscoveryRequest;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.junit.platform.testkit.engine.EventConditions.*;

import info.javaspec.api.JavaSpec;
import info.javaspec.api.SpecClass;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Condition;
import org.junit.platform.commons.annotation.Testable;
import org.junit.platform.engine.EngineDiscoveryRequest;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestEngine;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.testkit.engine.EngineExecutionResults;
import org.junit.platform.testkit.engine.EngineTestKit;

@Testable
public class JavaSpecEngineTest implements SpecClass {
	@Override
	public void declareSpecs(JavaSpec javaspec) {
		javaspec.it("can be loaded with ServiceLoader and located by ID", () -> {
			EngineTestKit.engine("javaspec-engine").selectors(selectClass(nullSpecClass())).execute();
		});

		javaspec.it("#discover reports to a provided EngineDiscoveryRequestListener", () -> {
			MockEngineDiscoveryRequestListener listener = new MockEngineDiscoveryRequestListener();
			JavaSpecEngine subject = new JavaSpecEngine(() -> Optional.of(listener));

			subject.discover(nullEngineDiscoveryRequest(), UniqueId.forEngine(subject.getId()));
			listener.onDiscoverExpected();
		});

		javaspec.it("#discover returns a top-level container for itself, using the given UniqueId", () -> {
			JavaSpecEngine subject = new JavaSpecEngine();
			UniqueId engineId = UniqueId.forEngine(subject.getId());

			TestDescriptor returned = subject.discover(nullEngineDiscoveryRequest(), engineId);
			assertEquals(engineId, returned.getUniqueId());
			assertEquals("JavaSpec", returned.getDisplayName());
			assertEquals(Optional.empty(), returned.getParent());
			assertTrue(returned.isContainer());
			assertTrue(returned.isRoot());
		});

		javaspec.it("#discover discovers no containers or tests, given no selectors", () -> {
			JavaSpecEngine subject = new JavaSpecEngine();

			TestDescriptor returned = subject.discover(nullEngineDiscoveryRequest(), UniqueId.forEngine(subject.getId()));
			assertEquals(Collections.emptySet(), returned.getChildren());
			assertFalse(returned.isTest());
		});

		javaspec.it("#discover ignores selectors for classes that are not SpecClass", () -> {
			JavaSpecEngine subject = new JavaSpecEngine();
			TestDescriptor returned = subject
				.discover(classEngineDiscoveryRequest(notASpecClass()), UniqueId.forEngine(subject.getId()));
			assertEquals(Collections.emptySet(), returned.getChildren());
		});

		javaspec.it("#discover discovers a container for each selected spec class", () -> {
			JavaSpecEngine subject = new JavaSpecEngine();
			Class<?> nullSpecClass = nullSpecClass();
			TestDescriptor returned = subject
				.discover(classEngineDiscoveryRequest(nullSpecClass), UniqueId.forEngine(subject.getId()));

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
		});

		javaspec.it("#discover discovers a describe block", () -> {
			JavaSpecEngine subject = new JavaSpecEngine();
			Class<?> describeSpecClass = specClassWithEmptyDescribeBlock();
			TestDescriptor returned = subject
				.discover(classEngineDiscoveryRequest(describeSpecClass), UniqueId.forEngine(subject.getId()));

			assertEquals(1, returned.getChildren().size());
			TestDescriptor specClassDescriptor = returned.getChildren().iterator().next();

			assertEquals(1, specClassDescriptor.getChildren().size());
			TestDescriptor describeDescriptor = specClassDescriptor.getChildren().iterator().next();
			UniqueId.Segment idSegment = describeDescriptor.getUniqueId().getLastSegment();
			assertEquals("describe-block", idSegment.getType());
			assertEquals("something", idSegment.getValue());

			assertEquals("something", describeDescriptor.getDisplayName());
			assertEquals(specClassDescriptor, describeDescriptor.getParent().orElseThrow());
			assertTrue(describeDescriptor.isContainer());
			assertFalse(describeDescriptor.isRoot());
			assertFalse(describeDescriptor.isTest());
		});

		javaspec.it("#discover discovers a pending spec", () -> {
			JavaSpecEngine subject = new JavaSpecEngine();
			TestDescriptor returned = subject
				.discover(classEngineDiscoveryRequest(pendingSpecClass()), UniqueId.forEngine(subject.getId()));

			TestDescriptor specClassDescriptor = returned.getChildren().iterator().next();
			assertEquals(1, specClassDescriptor.getChildren().size());

			TestDescriptor specDescriptor = specClassDescriptor.getChildren().iterator().next();
			UniqueId.Segment idSegment = specDescriptor.getUniqueId().getLastSegment();
			assertEquals("test", idSegment.getType());
			assertEquals("pending spec", idSegment.getValue());
		});

		javaspec.it("#discover discovers a test for each spec in a spec class", () -> {
			JavaSpecEngine subject = new JavaSpecEngine();
			TestDescriptor returned = subject
				.discover(classEngineDiscoveryRequest(oneSpecClass()), UniqueId.forEngine(subject.getId()));

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
		});

		javaspec.it("#execute reports execution events for the engine", () -> {
			EngineExecutionResults results = EngineTestKit.engine(new JavaSpecEngine())
				.selectors(nullDiscoverySelector())
				.execute();
			results.containerEvents()
				.assertEventsMatchExactly(event(engine(), started()), event(engine(), finishedSuccessfully()));
		});

		javaspec.it("#execute skips spec class containers that don't have any specs in them", () -> {
			EngineExecutionResults results = EngineTestKit.engine(new JavaSpecEngine())
				.selectors(selectClass(nullSpecClass()))
				.execute();
			results.containerEvents()
				.assertEventsMatchExactly(event(engine(), started()), event(engine(), finishedSuccessfully()));
		});

		javaspec.it("#execute reports execution events for spec class containers", () -> {
			EngineExecutionResults results = EngineTestKit.engine(new JavaSpecEngine())
				.selectors(selectClass(oneSpecClass()))
				.execute();

			results.containerEvents()
				.assertEventsMatchExactly(
					event(engine(), started()),
					event(container(), started()),
					event(container(), finishedSuccessfully()),
					event(engine(), finishedSuccessfully())
				);
		});

		javaspec.it("#execute reports start and skipped events for pending specs", () -> {
			EngineExecutionResults results = EngineTestKit.engine(new JavaSpecEngine())
				.selectors(selectClass(pendingSpecClass()))
				.execute();

			results.allEvents()
				.assertEventsMatchLooselyInOrder(
					event(test(), started()),
					event(test(), skippedWithReason("pending"))
				);
		});

		javaspec.it("#execute reports start and successful finish events for passing specs", () -> {
			EngineExecutionResults results = EngineTestKit.engine(new JavaSpecEngine())
				.selectors(selectClass(oneSpecClass()))
				.execute();

			results.allEvents()
				.assertEventsMatchLooselyInOrder(
					event(container(), started()),
					event(test(), started()),
					event(test(), finishedSuccessfully()),
					event(container(), finishedSuccessfully())
				);
		});

		javaspec.it("#execute reports start and failed finish events for failing specs", () -> {
			EngineExecutionResults results = EngineTestKit.engine(new JavaSpecEngine())
				.selectors(selectClass(oneSpecThrowingRuntimeException()))
				.execute();

			results.allEvents()
				.assertEventsMatchLooselyInOrder(
					event(container(), started()),
					event(test(), started()),
					event(
						test(),
						finishedWithFailure(new Condition<Throwable>(t -> RuntimeException.class.isInstance(t), "RuntimeException"))
					),
					event(container(), finishedSuccessfully())
				);
		});

		javaspec.it("#execute catches specs that fail by throwing AssertionError", () -> {
			EngineExecutionResults results = EngineTestKit.engine(new JavaSpecEngine())
				.selectors(selectClass(oneSpecThrowingAssertionError()))
				.execute();

			results.allEvents()
				.assertEventsMatchLooselyInOrder(
					event(container(), started()),
					event(test(), started()),
					event(
						test(),
						finishedWithFailure(new Condition<Throwable>(t -> AssertionError.class.isInstance(t), "AssertionError"))
					),
					event(container(), finishedSuccessfully())
				);
		});

		javaspec.it("#getId returns a unique ID", () -> {
			TestEngine subject = new JavaSpecEngine();
			assertEquals("javaspec-engine", subject.getId());
		});
	}

	private static final class MockEngineDiscoveryRequestListener implements EngineDiscoveryRequestListener {
		EngineDiscoveryRequest onDiscoveryReceived;

		@Override
		public void onDiscover(EngineDiscoveryRequest discoveryRequest) {
			this.onDiscoveryReceived = discoveryRequest;
		}

		public void onDiscoverExpected() {
			assertNotNull(this.onDiscoveryReceived, "Expected #onDiscover to have been called");
		}
	}
}
