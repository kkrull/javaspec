package info.javaspec.engine;

import org.junit.platform.engine.ConfigurationParameters;
import org.junit.platform.engine.DiscoveryFilter;
import org.junit.platform.engine.DiscoverySelector;
import org.junit.platform.engine.EngineDiscoveryRequest;
import org.junit.platform.engine.discovery.ClassSelector;
import org.junit.platform.engine.discovery.DiscoverySelectors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static info.javaspec.engine.ConfigurationParametersFactory.nullConfigurationParameters;
import static java.util.stream.Collectors.toList;

//Test data factory for different kinds of EngineDiscoveryRequest.
public final class EngineDiscoveryRequestFactory {
	private EngineDiscoveryRequestFactory() { /* static class */ }

	public static EngineDiscoveryRequest classEngineDiscoveryRequest(Class<?> specClass) {
		return new ClassEngineDiscoveryRequest(specClass);
	}

	public static EngineDiscoveryRequest nullEngineDiscoveryRequest() {
		return new NullEngineDiscoveryRequest();
	}

	private static final class ClassEngineDiscoveryRequest implements EngineDiscoveryRequest {
		private final List<DiscoverySelector> selectors;

		public ClassEngineDiscoveryRequest(Class<?> specClass) {
			this.selectors = new LinkedList<>();
			this.selectors.add(DiscoverySelectors.selectClass(specClass));
		}

		@Override
		public ConfigurationParameters getConfigurationParameters() {
			return nullConfigurationParameters();
		}

		@Override
		public <T extends DiscoveryFilter<?>> List<T> getFiltersByType(Class<T> filterType) {
			return Collections.emptyList();
		}

		@Override
		public <T extends DiscoverySelector> List<T> getSelectorsByType(Class<T> selectorType) {
			if (!ClassSelector.class.equals(selectorType)) {
				return Collections.emptyList();
			}

			return this.selectors.stream()
				.map(selectorType::cast)
				.collect(toList());
		}
	}

	private static final class NullEngineDiscoveryRequest implements EngineDiscoveryRequest {
		@Override
		public ConfigurationParameters getConfigurationParameters() {
			return nullConfigurationParameters();
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
}
