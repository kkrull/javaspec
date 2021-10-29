package info.javaspec.engine;

import org.junit.platform.engine.*;
import org.junit.platform.engine.support.descriptor.EngineDescriptor;

public class SpecTestEngine implements TestEngine {
  @Override
  public TestDescriptor discover(EngineDiscoveryRequest discoveryRequest, UniqueId engineId) {
    EngineDescriptor engineDescriptor = new EngineDescriptor(engineId, "JavaSpec");
    JupiterSpecContainer container = new GreeterSpecs().declareSpecs();
    container.addDescriptorsTo(engineDescriptor); //TODO KDK: [2] Create an intermediate descriptor for what will become JavaSpec#describe(Class<>)
    return engineDescriptor;
  }

  @Override
  public void execute(ExecutionRequest request) {
    execute(
      request.getRootTestDescriptor(),
      request.getEngineExecutionListener()
    );
  }

  private void execute(TestDescriptor descriptor, EngineExecutionListener listener) {
    listener.executionStarted(descriptor);

    if (descriptor.isTest()) {
      SpecDescriptor specDescriptor = (SpecDescriptor) descriptor;
      specDescriptor.runSpec();
    } else if (descriptor.isContainer()) {
      for (TestDescriptor child : descriptor.getChildren()) {
        execute(child, listener);
      }
    } else {
      System.out.println(String.format("*** Unsupported descriptor type: %s ***", descriptor.getClass().getName()));
    }

    listener.executionFinished(descriptor, TestExecutionResult.successful());
  }

  @Override
  public String getId() {
    return "javaspec-engine";
  }
}
