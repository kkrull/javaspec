package info.javaspec.engine;

import org.junit.platform.engine.TestDescriptor;

public class JupiterSpecContainer {
  private JupiterSpec spec;

  public void addSpec(JupiterSpec spec) {
    this.spec = spec;
  }

  public void addDescriptorsTo(TestDescriptor parentDescriptor) {
    spec.addTestDescriptorTo(parentDescriptor);
  }
}
