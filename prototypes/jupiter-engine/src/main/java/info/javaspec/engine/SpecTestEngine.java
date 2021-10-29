package info.javaspec.engine;

import org.junit.platform.engine.*;
import org.junit.platform.engine.discovery.ClassSelector;
import org.junit.platform.engine.support.descriptor.EngineDescriptor;

public class SpecTestEngine implements TestEngine {
  @Override
  public TestDescriptor discover(EngineDiscoveryRequest discoveryRequest, UniqueId engineId) {
    EngineDescriptor engineDescriptor = new EngineDescriptor(engineId, "JavaSpec");

    discoveryRequest.getSelectorsByType(ClassSelector.class).stream()
      .forEach(x -> System.out.printf("Selected class: %s%n", x.getClassName()));

    JupiterSpecContainer container = new GreeterSpecs().declareSpecs(); //TODO KDK [1]: Look for Spec classes, given in the runtime configuration
    container.addDescriptorsTo(engineDescriptor);
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
