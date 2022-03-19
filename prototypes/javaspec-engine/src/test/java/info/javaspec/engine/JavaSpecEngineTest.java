package info.javaspec.engine;

import static info.javaspec.engine.DiscoverySelectorFactory.nullDiscoverySelector;
import static info.javaspec.engine.EngineDiscoveryRequestFactory.classEngineDiscoveryRequest;
import static info.javaspec.engine.EngineDiscoveryRequestFactory.nullEngineDiscoveryRequest;
import static info.javaspec.engine.TestDescriptorAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.junit.platform.testkit.engine.EventConditions.*;

import info.javaspec.api.JavaSpec;
import info.javaspec.api.SpecClass;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.assertj.core.api.Condition;
import org.assertj.core.util.Lists;
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
			EngineTestKit.engine("javaspec-engine")
				.selectors(selectClass(AnonymousSpecClasses.emptySpecClass()))
				.execute();
		});

		javaspec.describe("#discover", () -> {
			javaspec.it("reports to a provided EngineDiscoveryRequestListener", () -> {
				MockEngineDiscoveryRequestListener listener = new MockEngineDiscoveryRequestListener();
				JavaSpecEngine subject = new JavaSpecEngine(() -> Optional.of(listener));

				subject.discover(nullEngineDiscoveryRequest(), UniqueId.forEngine(subject.getId()));
				listener.onDiscoverExpected();
			});

			javaspec.it("returns a top-level container for itself, using the given UniqueId", () -> {
				JavaSpecEngine subject = new JavaSpecEngine();
				UniqueId engineId = UniqueId.forEngine(subject.getId());

				TestDescriptor returned = subject.discover(nullEngineDiscoveryRequest(), engineId);
				assertThat(returned.getUniqueId()).isEqualTo(engineId);
				assertThat(returned).hasDisplayName("JavaSpec");
				assertThat(returned.getParent()).isEmpty();
				assertThat(returned).isRootContainer();
			});

			javaspec.it("discovers no containers or tests, given no selectors", () -> {
				JavaSpecEngine subject = new JavaSpecEngine();
				TestDescriptor returned = subject.discover(
					nullEngineDiscoveryRequest(),
					UniqueId.forEngine(subject.getId())
				);

				assertThat(returned).hasNoChildren();
			});

			javaspec.it("ignores selectors for classes that are not SpecClass", () -> {
				JavaSpecEngine subject = new JavaSpecEngine();
				TestDescriptor returned = subject.discover(
					classEngineDiscoveryRequest(AnonymousSpecClasses.notASpecClass()),
					UniqueId.forEngine(subject.getId())
				);

				assertThat(returned).hasNoChildren();
			});

			javaspec.it("discovers a container for each selected spec class", () -> {
				JavaSpecEngine subject = new JavaSpecEngine();
				Class<?> nullSpecClass = AnonymousSpecClasses.emptySpecClass();
				TestDescriptor returned = subject.discover(
					classEngineDiscoveryRequest(nullSpecClass),
					UniqueId.forEngine(subject.getId())
				);

				// TODO KDK: Add assertion for children's display names
				List<TestDescriptor> specClassDescriptors = new ArrayList<>(returned.getChildren());
				assertEquals(1, returned.getChildren().size());

				TestDescriptor onlyChild = specClassDescriptors.get(0);
				assertThat(onlyChild)
					.hasDisplayName(nullSpecClass.getName())
					.hasIdEndingIn("class", nullSpecClass.getName())
					.isRegularContainer()
					.hasParent(returned);
			});

			javaspec.it("discovers a describe block", () -> {
				JavaSpecEngine subject = new JavaSpecEngine();
				TestDescriptor returned = subject.discover(
					classEngineDiscoveryRequest(AnonymousSpecClasses.emptyDescribe()),
					UniqueId.forEngine(subject.getId())
				);

				assertEquals(1, returned.getChildren().size());
				TestDescriptor specClassDescriptor = returned.getChildren().iterator().next();

				assertEquals(1, specClassDescriptor.getChildren().size());
				TestDescriptor describeDescriptor = specClassDescriptor.getChildren().iterator().next();
				assertThat(describeDescriptor).hasIdEndingIn(
					"describe-block",
					"something"
				);

				assertThat(describeDescriptor).hasDisplayName("something");
				assertThat(describeDescriptor).hasParent(specClassDescriptor);
				assertTrue(describeDescriptor.isContainer());
				assertFalse(describeDescriptor.isRoot());
				assertFalse(describeDescriptor.isTest());
			});

			javaspec.it("discovers a describe block with a spec in it", () -> {
				JavaSpecEngine subject = new JavaSpecEngine();
				TestDescriptor returned = subject.discover(
					classEngineDiscoveryRequest(AnonymousSpecClasses.describeWithOneSpec()),
					UniqueId.forEngine(subject.getId())
				);

				TestDescriptor specClassDescriptor = returned.getChildren().iterator().next();
				assertEquals(1, specClassDescriptor.getChildren().size());

				TestDescriptor describeDescriptor = specClassDescriptor.getChildren().iterator().next();
				assertEquals(1, describeDescriptor.getChildren().size());

				TestDescriptor specDescriptor = describeDescriptor.getChildren().iterator().next();
				assertThat(specDescriptor).hasDisplayName("works");
				assertTrue(specDescriptor.isTest());
			});

			javaspec.pending("discovers specs declared after nested describe blocks, realizing the beauty of a Stack");

			javaspec.it("discovers a pending spec", () -> {
				JavaSpecEngine subject = new JavaSpecEngine();
				TestDescriptor returned = subject.discover(
					classEngineDiscoveryRequest(AnonymousSpecClasses.pendingSpec()),
					UniqueId.forEngine(subject.getId())
				);

				TestDescriptor specClassDescriptor = returned.getChildren().iterator().next();
				assertEquals(1, specClassDescriptor.getChildren().size());

				TestDescriptor specDescriptor = specClassDescriptor.getChildren().iterator().next();
				assertThat(specDescriptor).hasIdEndingIn("test", "pending spec");
			});

			javaspec.it("discovers a test for each spec in a spec class", () -> {
				JavaSpecEngine subject = new JavaSpecEngine();
				TestDescriptor returned = subject.discover(
					classEngineDiscoveryRequest(AnonymousSpecClasses.oneSpec()),
					UniqueId.forEngine(subject.getId())
				);

				TestDescriptor specClassDescriptor = returned.getChildren().iterator().next();
				assertEquals(1, specClassDescriptor.getChildren().size());

				TestDescriptor specDescriptor = specClassDescriptor.getChildren().iterator().next();
				assertThat(specDescriptor).hasIdEndingIn("test", "one spec");

				assertThat(specDescriptor).hasParent(specClassDescriptor);
				assertFalse(specDescriptor.isContainer());
				assertFalse(specDescriptor.isRoot());
				assertTrue(specDescriptor.isTest());
			});

			javaspec.it("discovers specs declared after a describe block in the same level of nesting", () -> {
				JavaSpecEngine subject = new JavaSpecEngine();
				TestDescriptor returned = subject.discover(
					classEngineDiscoveryRequest(AnonymousSpecClasses.describeThenSpec()),
					UniqueId.forEngine(subject.getId())
				);

				TestDescriptor specClass = returned.getChildren().iterator().next();
				List<String> displayNames = specClass.getChildren().stream()
					.map(TestDescriptor::getDisplayName)
					.collect(Collectors.toList());
				assertEquals(Lists.newArrayList("something", "spec"), displayNames);
			});
		});

		javaspec.describe("#execute", () -> {
			javaspec.it("reports execution events for the engine", () -> {
				EngineExecutionResults results = EngineTestKit.engine(new JavaSpecEngine())
					.selectors(nullDiscoverySelector())
					.execute();
				results.containerEvents()
					.assertEventsMatchExactly(event(engine(), started()), event(engine(), finishedSuccessfully()));
			});

			javaspec.it("skips spec class containers that don't have any specs in them", () -> {
				EngineExecutionResults results = EngineTestKit.engine(new JavaSpecEngine())
					.selectors(selectClass(AnonymousSpecClasses.emptySpecClass()))
					.execute();
				results.containerEvents()
					.assertEventsMatchExactly(event(engine(), started()), event(engine(), finishedSuccessfully()));
			});

			javaspec.it("reports execution events for spec class containers", () -> {
				EngineExecutionResults results = EngineTestKit.engine(new JavaSpecEngine())
					.selectors(selectClass(AnonymousSpecClasses.oneSpec()))
					.execute();

				results.containerEvents()
					.assertEventsMatchExactly(
						event(engine(), started()),
						event(container(), started()),
						event(container(), finishedSuccessfully()),
						event(engine(), finishedSuccessfully())
					);
			});

			javaspec.it("reports start and skipped events for pending specs", () -> {
				EngineExecutionResults results = EngineTestKit.engine(new JavaSpecEngine())
					.selectors(selectClass(AnonymousSpecClasses.pendingSpec()))
					.execute();

				results.allEvents()
					.assertEventsMatchLooselyInOrder(
						event(test(), started()),
						event(test(), skippedWithReason("pending"))
					);
			});

			javaspec.it("reports start and successful finish events for passing specs", () -> {
				EngineExecutionResults results = EngineTestKit.engine(new JavaSpecEngine())
					.selectors(selectClass(AnonymousSpecClasses.oneSpec()))
					.execute();

				results.allEvents()
					.assertEventsMatchLooselyInOrder(
						event(container(), started()),
						event(test(), started()),
						event(test(), finishedSuccessfully()),
						event(container(), finishedSuccessfully())
					);
			});

			javaspec.it("reports start and failed finish events for failing specs", () -> {
				EngineExecutionResults results = EngineTestKit.engine(new JavaSpecEngine())
					.selectors(selectClass(AnonymousSpecClasses.oneSpecThrowingRuntimeException()))
					.execute();

				results.allEvents()
					.assertEventsMatchLooselyInOrder(
						event(container(), started()),
						event(test(), started()),
						event(
							test(),
							finishedWithFailure(new Condition<>(RuntimeException.class::isInstance, "RuntimeException"))
						),
						event(container(), finishedSuccessfully())
					);
			});

			javaspec.it("catches specs that fail by throwing AssertionError", () -> {
				EngineExecutionResults results = EngineTestKit.engine(new JavaSpecEngine())
					.selectors(selectClass(AnonymousSpecClasses.oneSpecThrowingAssertionError()))
					.execute();

				results.allEvents()
					.assertEventsMatchLooselyInOrder(
						event(container(), started()),
						event(test(), started()),
						event(
							test(),
							finishedWithFailure(new Condition<>(AssertionError.class::isInstance, "AssertionError"))
						),
						event(container(), finishedSuccessfully())
					);
			});
		});

		javaspec.describe("#getId", () -> {
			javaspec.it("returns a unique ID", () -> {
				TestEngine subject = new JavaSpecEngine();
				assertEquals("javaspec-engine", subject.getId());
			});
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
