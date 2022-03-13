package info.javaspec.engine;

import org.junit.platform.engine.EngineDiscoveryRequest;

//Receives an EngineDiscoveryRequest, upon discovery
public interface EngineDiscoveryRequestListener {
	void onDiscover(EngineDiscoveryRequest discoveryRequest);
}
