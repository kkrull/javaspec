package info.javaspec.engine;

import info.javaspec.api.SpecClass;
import org.junit.platform.engine.*;
import org.junit.platform.engine.TestDescriptor.Type;
import org.junit.platform.engine.discovery.*;
import org.junit.platform.engine.support.descriptor.EngineDescriptor;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ServiceLoader;

//Discovers specs, turns them into something Jupiter can run, and runs them.
public class JavaSpecEngine implements TestEngine {
  @Override
  public TestDescriptor discover(EngineDiscoveryRequest discoveryRequest, UniqueId engineId) {
    ServiceLoader<EngineDiscoveryRequestListener> loader = ServiceLoader.load(EngineDiscoveryRequestListener.class);
    loader.findFirst().ifPresent(x -> x.onDiscover(discoveryRequest));

    EngineDescriptor engineDescriptor = new EngineDescriptor(engineId, "JavaSpec");
    discoveryRequest.getSelectorsByType(ClassSelector.class).stream()
        .map(ClassSelector::getJavaClass)
        .filter(selectedClass -> SpecClass.class.isAssignableFrom(selectedClass))
        .map(selectedClass -> (Class<SpecClass>) selectedClass)
        .map(specClass -> makeDeclaringInstance(specClass))
        .forEach(declaringInstance -> {
          JavaSpecForJupiter javaspec = JavaSpecForJupiter.forSpecClass(engineId, declaringInstance.getClass());
          declaringInstance.declareSpecs(javaspec);
          javaspec.addDescriptorsTo(engineDescriptor);
        });

    return engineDescriptor;
  }

  private SpecClass makeDeclaringInstance(Class<SpecClass> specClass) {
    Constructor<SpecClass> constructor = null;
    try {
      constructor = specClass.getDeclaredConstructor();
    } catch (NoSuchMethodException e) {
      throw new RuntimeException("Failed to access no-arg SpecClass constructor", e);
    }

    try {
      return constructor.newInstance();
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException("Failed to instantiate SpecClass", e);
    }
  }

  @Override
  public void execute(ExecutionRequest request) {
    execute(
        request.getRootTestDescriptor(),
        request.getEngineExecutionListener());
  }

  private void execute(TestDescriptor descriptor, EngineExecutionListener listener) {
    switch (descriptor.getType()) {
      case CONTAINER:
        listener.executionStarted(descriptor);
        for (TestDescriptor child : descriptor.getChildren())
          execute(child, listener);

        listener.executionFinished(descriptor, TestExecutionResult.successful());
        return;

      case TEST:
        listener.executionStarted(descriptor);
        try {
          JupiterSpec spec = (JupiterSpec) descriptor;
          spec.run();
        } catch (AssertionError | Exception e) {
          listener.executionFinished(descriptor, TestExecutionResult.failed(e));
          return;
        }

        listener.executionFinished(descriptor, TestExecutionResult.successful());
        return;

      case CONTAINER_AND_TEST:
        listener.executionStarted(descriptor);
        for (TestDescriptor child : descriptor.getChildren())
          execute(child, listener);

        try {
          JupiterSpec spec = (JupiterSpec) descriptor;
          spec.run();
        } catch (AssertionError | Exception e) {
          listener.executionFinished(descriptor, TestExecutionResult.failed(e));
          return;
        }

        listener.executionFinished(descriptor, TestExecutionResult.successful());
        return;
    }
  }

  @Override
  public String getId() {
    return "javaspec-engine";
  }
}
