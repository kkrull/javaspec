package info.javaspec.engine;

import org.junit.platform.engine.ConfigurationParameters;
import org.junit.platform.engine.DiscoveryFilter;
import org.junit.platform.engine.DiscoverySelector;
import org.junit.platform.engine.EngineDiscoveryRequest;

import java.util.Collections;
import java.util.List;

import static info.javaspec.engine.ConfigurationParametersFactory.nullConfigurationParameters;

//Test data factory for different kinds of EngineDiscoveryRequest.
public final class EngineDiscoveryRequestFactory {
	private EngineDiscoveryRequestFactory() {
		/* static class */
	}

	public static EngineDiscoveryRequest classEngineDiscoveryRequest(Class<?> specClass) {
		return new ClassEngineDiscoveryRequest(specClass);
	}

	public static EngineDiscoveryRequest nullEngineDiscoveryRequest() {
		return new NullEngineDiscoveryRequest();
	}

	private static final class ClassEngineDiscoveryRequest implements EngineDiscoveryRequest {
		public ClassEngineDiscoveryRequest(Class<?> specClass) {
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
			throw new UnsupportedOperationException(
				"work here - return the one selector for the one class if it's a class selector; otherwise empty list");
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
