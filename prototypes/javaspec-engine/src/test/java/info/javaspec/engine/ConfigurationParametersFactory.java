package info.javaspec.engine;

import java.util.Optional;
import org.junit.platform.engine.ConfigurationParameters;

//Test data factory for different kinds of ConfigurationParameters.
public final class ConfigurationParametersFactory {
	private ConfigurationParametersFactory() { /* static class */ }

	public static ConfigurationParameters nullConfigurationParameters() {
		return new NullConfigurationParameters();
	}

	private static final class NullConfigurationParameters implements ConfigurationParameters {
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
}
