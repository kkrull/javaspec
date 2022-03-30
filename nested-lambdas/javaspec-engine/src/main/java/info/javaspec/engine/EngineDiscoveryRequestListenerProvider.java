package info.javaspec.engine;

import java.util.Optional;

//Loads optional runtime dependencies, just like ServiceLoader::load.
@FunctionalInterface
interface EngineDiscoveryRequestListenerProvider {
	Optional<EngineDiscoveryRequestListener> findFirst();
}
