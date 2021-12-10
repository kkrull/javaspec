package info.javaspec.engine;

import info.javaspec.api.SpecContainer;
import info.javaspec.api.Verification;
import org.junit.platform.engine.TestDescriptor;

public class JupiterSpecContainer implements SpecContainer {
  private final Class<?> specClass;
  private JupiterSpec spec;

  public JupiterSpecContainer(Class<?> specClass) {
    this.specClass = specClass;
  }

  @Override
  public void addSpec(String behavior, Verification verification) {
    this.spec = new JupiterSpec(behavior, verification);
  }

  public void addDescriptorsTo(TestDescriptor parentDescriptor) {
    ContainerDescriptor containerDescriptor = ContainerDescriptor.forClass(
      parentDescriptor.getUniqueId(),
      specClass
    );
    parentDescriptor.addChild(containerDescriptor);

    this.spec.addTestDescriptorTo(containerDescriptor);
  }
}
