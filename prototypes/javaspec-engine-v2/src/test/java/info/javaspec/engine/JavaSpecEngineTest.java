package info.javaspec.engine;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.platform.engine.*;
import org.junit.platform.testkit.engine.EngineExecutionResults;
import org.junit.platform.testkit.engine.EngineTestKit;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.platform.testkit.engine.EventConditions.*;

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
			TestDescriptor rootDescriptor = subject.discover(new ClassEngineDiscoveryRequest(NullSpecClass.class),
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
			TestDescriptor rootDescriptor = subject.discover(new NullEngineDiscoveryRequest(), engineId);
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
					.selectors(new NullDiscoverySelector()).execute();
			results.containerEvents().assertEventsMatchExactly(event(engine(), started()),
					event(engine(), finishedSuccessfully()));
		}
	}

	static final class ClassEngineDiscoveryRequest implements EngineDiscoveryRequest {
		public ClassEngineDiscoveryRequest(Class<?> specClass) {
		}

		@Override
		public ConfigurationParameters getConfigurationParameters() {
			return new NullConfigurationParameters();
		}

		@Override
		public <T extends DiscoveryFilter<?>> List<T> getFiltersByType(Class<T> filterType) {
			return Collections.emptyList();
		}

		@Override
		public <T extends DiscoverySelector> List<T> getSelectorsByType(Class<T> selectorType) {
			throw new UnsupportedOperationException(
					"work here - return the one selector for the one class if it's a class selector; otherwise empty list");
		}
	}

	static final class NullConfigurationParameters implements ConfigurationParameters {
		@Override
		public Optional<String> get(String key) {
			return Optional.empty();
		}

		@Override
		public Optional<Boolean> getBoolean(String key) {
			return Optional.empty();
		}

		@Override
		public int size() {
			return 0;
		}
	}

	static final class NullDiscoverySelector implements DiscoverySelector {
	}

	static final class NullEngineDiscoveryRequest implements EngineDiscoveryRequest {
		@Override
		public ConfigurationParameters getConfigurationParameters() {
			return new NullConfigurationParameters();
		}

		@Override
		public <T extends DiscoveryFilter<?>> List<T> getFiltersByType(Class<T> filterType) {
			return Collections.emptyList();
		}

		@Override
		public <T extends DiscoverySelector> List<T> getSelectorsByType(Class<T> selectorType) {
			return Collections.emptyList();
		}
	}

	static final class NullSpecClass {
	}
}
