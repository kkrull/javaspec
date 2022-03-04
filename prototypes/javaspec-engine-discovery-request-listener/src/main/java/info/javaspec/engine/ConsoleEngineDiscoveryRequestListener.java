package info.javaspec.engine;

import org.junit.platform.engine.EngineDiscoveryRequest;

public class ConsoleEngineDiscoveryRequestListener implements EngineDiscoveryRequestListener {
  @Override
  public void onDiscover(EngineDiscoveryRequest discoveryRequest) {
    throw new UnsupportedOperationException("bang!");
  }
}
