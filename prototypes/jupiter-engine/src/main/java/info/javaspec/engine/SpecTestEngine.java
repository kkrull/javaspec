package info.javaspec.engine;

import org.junit.platform.engine.*;
import org.junit.platform.engine.support.descriptor.EngineDescriptor;

public class SpecTestEngine implements TestEngine {
  @Override
  public TestDescriptor discover(EngineDiscoveryRequest discoveryRequest, UniqueId engineId) {
    EngineDescriptor engineDescriptor = new EngineDescriptor(engineId, "JavaSpec");
    TestDescriptor specDescriptor = SpecDescriptor.forSpec(engineId, "greets the world", () -> GreeterSpecs.incrementRunCount());
    engineDescriptor.addChild(specDescriptor);
    return engineDescriptor;
  }

  @Override
  public void execute(ExecutionRequest request) {
    TestDescriptor engineDescriptor = request.getRootTestDescriptor();
    EngineExecutionListener listener = request.getEngineExecutionListener();
    listener.executionStarted(engineDescriptor);

    for (TestDescriptor childDescriptor : engineDescriptor.getChildren()) {
      listener.executionStarted(childDescriptor);
      SpecDescriptor specDescriptor = (SpecDescriptor) childDescriptor;
      specDescriptor.runSpec();
      listener.executionFinished(childDescriptor, TestExecutionResult.successful());
    }

    listener.executionFinished(engineDescriptor, TestExecutionResult.successful());
  }

  @Override
  public String getId() {
    return "javaspec-engine";
  }
}
