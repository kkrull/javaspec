package info.javaspec.engine;

import org.junit.platform.engine.DiscoverySelector;

//Test data factory for different kinds of DiscoverySelector.
public final class DiscoverySelectorFactory {
	private DiscoverySelectorFactory() { /* static class */ }

	public static DiscoverySelector nullDiscoverySelector() {
		return new NullDiscoverySelector();
	}

	private static final class NullDiscoverySelector implements DiscoverySelector {}
}
