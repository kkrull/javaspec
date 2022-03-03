package info.javaspec.engine;

import org.junit.platform.engine.EngineDiscoveryRequest;

//Receives an EngineDiscoveryRequest, upon discovery
public interface EngineDiscoveryRequestListener {
  void onDiscover(EngineDiscoveryRequest discoveryRequest); //TODO KDK: Implement this in a separate Gradle module, then include as testRuntimeOnly dependency
}
