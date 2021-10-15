package info.javaspec.engine;

import org.junit.platform.engine.*;

public class SpecTestEngine implements TestEngine {
  @Override
  public TestDescriptor discover(EngineDiscoveryRequest discoveryRequest, UniqueId uniqueId) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void execute(ExecutionRequest request) {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getId() {
    return "javaspec-engine";
  }
}
