/**
 * MIT License
 *
 * Copyright (c) 2014–2022 Kyle Krull
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package info.javaspec.engine;

import static info.javaspec.engine.DiscoverySelectorFactory.nullDiscoverySelector;
import static info.javaspec.engine.EngineDiscoveryRequestFactory.classEngineDiscoveryRequest;
import static info.javaspec.engine.EngineDiscoveryRequestFactory.nullEngineDiscoveryRequest;
import static info.javaspec.engine.TestDescriptorAssert.assertThat;
import static org.assertj.core.api.Assertions.allOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.junit.platform.testkit.engine.Event.byTestDescriptor;
import static org.junit.platform.testkit.engine.EventConditions.*;

import info.javaspec.api.JavaSpec;
import info.javaspec.api.SpecClass;
import java.util.Optional;
import org.assertj.core.api.Condition;
import org.junit.platform.commons.annotation.Testable;
import org.junit.platform.engine.EngineDiscoveryRequest;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestEngine;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.testkit.engine.EngineExecutionResults;
import org.junit.platform.testkit.engine.EngineTestKit;
import org.junit.platform.testkit.engine.Event;

@Testable
public class JavaSpecEngineTest implements SpecClass {
	@Override
	public void declareSpecs(JavaSpec javaspec) {
		javaspec.describe(JavaSpecEngine.class, () -> {
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
					assertThat(returned)
						.hasDisplayName("JavaSpec")
						.hasUniqueId(engineId)
						.isRootContainer();
					assertThat(returned.getParent()).isEmpty();
				});

				javaspec.describe("edge cases", () -> {
					javaspec.it("discovers nothing, given no selectors", () -> {
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
				});

				javaspec.given("selectors for 1 or more SpecClasses", () -> {
					javaspec.it("discovers a container for each SpecClass", () -> {
						JavaSpecEngine subject = new JavaSpecEngine();
						Class<?> nullSpecClass = AnonymousSpecClasses.emptySpecClass();
						TestDescriptor returned = subject.discover(
							classEngineDiscoveryRequest(nullSpecClass),
							UniqueId.forEngine(subject.getId())
						);

						assertThat(returned).hasChildrenNamed(nullSpecClass.getName());
						assertThat(returned.getChildren().iterator().next())
							.hasIdEndingIn("class", nullSpecClass.getName())
							.isRegularContainer()
							.hasParent(returned);
					});

					javaspec.it("discovers a test for each pending spec", () -> {
						JavaSpecEngine subject = new JavaSpecEngine();
						TestDescriptor returned = subject.discover(
							classEngineDiscoveryRequest(AnonymousSpecClasses.pendingSpec()),
							UniqueId.forEngine(subject.getId())
						);

						TestDescriptor specClassDescriptor = returned.getChildren().iterator().next();
						assertThat(specClassDescriptor).hasChildren(1);

						TestDescriptor specDescriptor = specClassDescriptor.getChildren().iterator().next();
						assertThat(specDescriptor).hasIdEndingIn("test", "pending spec");
					});

					javaspec.it("discovers a test for each skipped spec", () -> {
						JavaSpecEngine subject = new JavaSpecEngine();
						TestDescriptor returned = subject.discover(
							classEngineDiscoveryRequest(AnonymousSpecClasses.skippedSpec()),
							UniqueId.forEngine(subject.getId())
						);

						TestDescriptor specClassDescriptor = returned.getChildren().iterator().next();
						assertThat(specClassDescriptor).hasChildren(1);

						TestDescriptor specDescriptor = specClassDescriptor.getChildren().iterator().next();
						assertThat(specDescriptor).hasIdEndingIn("test", "skipped spec");
					});

					javaspec.it("discovers a test for each spec", () -> {
						JavaSpecEngine subject = new JavaSpecEngine();
						TestDescriptor returned = subject.discover(
							classEngineDiscoveryRequest(AnonymousSpecClasses.oneSpec()),
							UniqueId.forEngine(subject.getId())
						);

						TestDescriptor specClassDescriptor = returned.getChildren().iterator().next();
						assertThat(specClassDescriptor).hasChildren(1);

						TestDescriptor specDescriptor = specClassDescriptor.getChildren().iterator().next();
						assertThat(specDescriptor)
							.hasIdEndingIn("test", "one spec")
							.hasParent(specClassDescriptor)
							.isJustATest();
					});
				});

				javaspec.given("a selected SpecClass with a describe block", () -> {
					javaspec.it("discovers a container for a string-describe block", () -> {
						JavaSpecEngine subject = new JavaSpecEngine();
						TestDescriptor returned = subject.discover(
							classEngineDiscoveryRequest(AnonymousSpecClasses.emptyDescribe()),
							UniqueId.forEngine(subject.getId())
						);

						assertThat(returned).hasChildren(1);

						TestDescriptor specClassDescriptor = returned.getChildren().iterator().next();
						assertThat(specClassDescriptor).hasChildren(1);

						TestDescriptor describeDescriptor = specClassDescriptor.getChildren().iterator().next();
						assertThat(describeDescriptor)
							.hasDisplayName("something")
							.hasIdEndingIn("describe-block", "something")
							.hasParent(specClassDescriptor)
							.isRegularContainer();
					});

					javaspec.it("discovers a container for a class-describe block", () -> {
						JavaSpecEngine subject = new JavaSpecEngine();
						TestDescriptor returned = subject.discover(
							classEngineDiscoveryRequest(AnonymousSpecClasses.emptyDescribeAClass()),
							UniqueId.forEngine(subject.getId())
						);

						TestDescriptor specClassDescriptor = returned.getChildren().iterator().next();
						TestDescriptor describeDescriptor = specClassDescriptor.getChildren().iterator().next();
						assertThat(describeDescriptor)
							.hasDisplayName("TheOracle")
							.hasIdEndingIn("describe-block", "TheOracle")
							.hasParent(specClassDescriptor)
							.isRegularContainer();
					});

					javaspec.it("discovers specs declared inside a describe block", () -> {
						JavaSpecEngine subject = new JavaSpecEngine();
						TestDescriptor returned = subject.discover(
							classEngineDiscoveryRequest(AnonymousSpecClasses.describeWithOneSpec()),
							UniqueId.forEngine(subject.getId())
						);

						TestDescriptor specClassDescriptor = returned.getChildren().iterator().next();
						assertThat(specClassDescriptor).hasChildren(1);

						TestDescriptor describeDescriptor = specClassDescriptor.getChildren().iterator().next();
						assertThat(describeDescriptor).hasChildren(1);

						TestDescriptor specDescriptor = describeDescriptor.getChildren().iterator().next();
						assertThat(specDescriptor)
							.hasDisplayName("works")
							.isJustATest();
					});

					javaspec.it("discovers specs declared next to a describe block", () -> {
						JavaSpecEngine subject = new JavaSpecEngine();
						TestDescriptor returned = subject.discover(
							classEngineDiscoveryRequest(AnonymousSpecClasses.describeThenSpec()),
							UniqueId.forEngine(subject.getId())
						);

						TestDescriptor specClass = returned.getChildren().iterator().next();
						assertThat(specClass).hasChildrenNamed("something", "spec");
					});

					javaspec.it("doesn't get fooled by nested describe blocks", () -> {
						JavaSpecEngine subject = new JavaSpecEngine();
						TestDescriptor returned = subject.discover(
							classEngineDiscoveryRequest(AnonymousSpecClasses.nestedDescribe()),
							UniqueId.forEngine(subject.getId())
						);

						assertThat(returned).hasChildren(1);

						TestDescriptor specClass = returned.getChildren().iterator().next();
						assertThat(specClass).hasChildrenNamed("describe-outer");

						TestDescriptor describeOuter = specClass.getChildren().iterator().next();
						assertThat(describeOuter).hasChildrenNamed(
							"describe-inner",
							"describe-outer-spec"
						);
					});
				});

				javaspec.given("a selected SpecClass with a given block", () -> {
					javaspec.it("discovers a container for a given block", () -> {
						JavaSpecEngine subject = new JavaSpecEngine();
						TestDescriptor returned = subject.discover(
							classEngineDiscoveryRequest(AnonymousSpecClasses.emptyGiven()),
							UniqueId.forEngine(subject.getId())
						);

						assertThat(returned).hasChildren(1);

						TestDescriptor specClass = returned.getChildren().iterator().next();
						assertThat(specClass).hasChildren(1);

						TestDescriptor given = specClass.getChildren().iterator().next();
						assertThat(given)
							.hasIdEndingIn("given-block", "a precondition")
							.hasParent(specClass)
							.isRegularContainer();
					});

					javaspec.it("automatically includes the word 'given' in the container's display name", () -> {
						JavaSpecEngine subject = new JavaSpecEngine();
						TestDescriptor returned = subject.discover(
							classEngineDiscoveryRequest(AnonymousSpecClasses.emptyGiven()),
							UniqueId.forEngine(subject.getId())
						);

						TestDescriptor specClass = returned.getChildren().iterator().next();
						assertThat(specClass).hasChildrenNamed("given a precondition");
					});

					javaspec.it("discovers specs declared inside a given block", () -> {
						JavaSpecEngine subject = new JavaSpecEngine();
						TestDescriptor returned = subject.discover(
							classEngineDiscoveryRequest(AnonymousSpecClasses.givenWithOneSpec()),
							UniqueId.forEngine(subject.getId())
						);

						assertThat(returned).hasChildren(1);

						TestDescriptor specClass = returned.getChildren().iterator().next();
						assertThat(specClass).hasChildren(1);

						TestDescriptor given = specClass.getChildren().iterator().next();
						assertThat(given).hasChildrenNamed("spec");

						TestDescriptor spec = given.getChildren().iterator().next();
						assertThat(spec)
							.hasIdEndingIn("test", "spec")
							.hasParent(given)
							.isJustATest();
					});
				});
			});

			javaspec.describe("#execute", () -> {
				javaspec.it("reports execution events for the engine", () -> {
					EngineExecutionResults results = EngineTestKit.engine(new JavaSpecEngine())
						.selectors(nullDiscoverySelector())
						.execute();

					results.containerEvents()
						.assertEventsMatchExactly(
							event(anyEngine(), started()),
							event(anyEngine(), finishedSuccessfully())
						);
				});

				javaspec.it("skips spec class containers that don't have any specs in them", () -> {
					EngineExecutionResults results = EngineTestKit.engine(new JavaSpecEngine())
						.selectors(selectClass(AnonymousSpecClasses.emptySpecClass()))
						.execute();

					results.containerEvents()
						.assertEventsMatchExactly(
							event(anyEngine(), started()),
							event(anyEngine(), finishedSuccessfully())
						);
				});

				javaspec.it("reports execution events for spec class containers", () -> {
					EngineExecutionResults results = EngineTestKit.engine(new JavaSpecEngine())
						.selectors(selectClass(AnonymousSpecClasses.oneSpec()))
						.execute();

					results.containerEvents()
						.assertEventsMatchExactly(
							event(anyEngine(), started()),
							event(container(), started()),
							event(container(), finishedSuccessfully()),
							event(anyEngine(), finishedSuccessfully())
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

				javaspec.it("reports start and skipped events for skipped specs", () -> {
					EngineExecutionResults results = EngineTestKit.engine(new JavaSpecEngine())
						.selectors(selectClass(AnonymousSpecClasses.skippedSpec()))
						.execute();

					results.allEvents()
						.assertEventsMatchLooselyInOrder(
							event(test(), started()),
							event(test(), skippedWithReason("skipped"))
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
							event(test(), finishedWithFailure(isInstanceOf(RuntimeException.class))),
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
							event(test(), finishedWithFailure(isInstanceOf(AssertionError.class))),
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
		});
	}

	// Accepts an event for any engine's TestDescriptor.
	// EventConditions::engine only accepts EngineDescriptor.
	private Condition<Event> anyEngine() {
		Condition<Event> isRoot = new Condition<>(
			byTestDescriptor(TestDescriptor::isRoot),
			"is the root container"
		);

		Condition<Event> isEngine = new Condition<>(
			byTestDescriptor(x ->
			{
				UniqueId.Segment firstIdSegment = x.getUniqueId().getSegments().get(0);
				return "engine".equals(firstIdSegment.getType());
			}),
			"is an engine"
		);

		return allOf(isRoot, isEngine);
	}

	private Condition<Throwable> isInstanceOf(Class<?> aClass) {
		return new Condition<>(aClass::isInstance, String.format("is an instance of %s", aClass.getName()));
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
