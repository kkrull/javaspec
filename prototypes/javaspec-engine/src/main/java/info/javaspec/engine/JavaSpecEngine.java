package info.javaspec.engine;

import info.javaspec.api.SpecClass;
import org.junit.platform.engine.*;
import org.junit.platform.engine.discovery.ClassSelector;
import org.junit.platform.engine.support.descriptor.EngineDescriptor;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * TODO KDK: Register the engine artifact it as a custom engine at runtime
 * https://junit.org/junit5/docs/current/user-guide/#launcher-api-engines-custom
 *
 * This gradle snippet may come in handy:
 *
 * test {
 *     useJUnitPlatform {
 *         includeEngines 'junit-jupiter', 'junit-vintage'
 *     }
 * }
 */

public class JavaSpecEngine implements TestEngine {
  @Override
  public TestDescriptor discover(EngineDiscoveryRequest discoveryRequest, UniqueId engineId) {
    EngineDescriptor engineDescriptor = new EngineDescriptor(engineId, "JavaSpec");

    discoveryRequest.getSelectorsByType(ClassSelector.class).stream()
      .map(ClassSelector::getJavaClass)
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
      request.getEngineExecutionListener()
    );
  }

  private void execute(TestDescriptor descriptor, EngineExecutionListener listener) {
    listener.executionStarted(descriptor);

    if (descriptor.isTest()) {
      JupiterSpec.SpecDescriptor specDescriptor = (JupiterSpec.SpecDescriptor) descriptor;
      specDescriptor.runSpec();
    } else if (descriptor.isContainer()) {
      for (TestDescriptor child : descriptor.getChildren()) {
        execute(child, listener);
      }
    } else {
      //Throwing exceptions from here didn't seem to cause any warnings, error messages, or failures.
      System.out.println(String.format("*** Unsupported descriptor type: %s ***", descriptor.getClass().getName()));
    }

    listener.executionFinished(descriptor, TestExecutionResult.successful());
  }

  @Override
  public String getId() {
    return "javaspec-engine";
  }
}
