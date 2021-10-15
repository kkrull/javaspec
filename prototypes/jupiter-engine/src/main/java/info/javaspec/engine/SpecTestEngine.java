package info.javaspec.engine;

import org.junit.platform.engine.*;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.platform.engine.support.descriptor.EngineDescriptor;

public class SpecTestEngine implements TestEngine {
  @Override
  public TestDescriptor discover(EngineDiscoveryRequest discoveryRequest, UniqueId engineId) {
    EngineDescriptor engineDescriptor = new EngineDescriptor(engineId, "JavaSpec");
    TestDescriptor specDescriptor = SpecDescriptor.forSpec(engineId, "greets the world");
    engineDescriptor.addChild(specDescriptor);
    return engineDescriptor;
  }

  @Override
  public void execute(ExecutionRequest request) {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getId() {
    return "javaspec-engine";
  }

  private static class SpecDescriptor extends AbstractTestDescriptor {
    public static SpecDescriptor forSpec(UniqueId engineId, String behavior) {
      return new SpecDescriptor(
        engineId.append("spec", "lambda %s".formatted(behavior)),
        behavior
      );
    }

    private SpecDescriptor(UniqueId uniqueId, String displayName) {
      super(uniqueId, displayName);
    }

    @Override
    public Type getType() {
      return Type.TEST;
    }
  }
}
