package info.javaspec.engine;

import org.junit.platform.engine.EngineDiscoveryRequest;

//Receives an EngineDiscoveryRequest, upon JavaSpecEngine#discover.
public interface EngineDiscoveryRequestListener {
	void onDiscover(EngineDiscoveryRequest request);
}
