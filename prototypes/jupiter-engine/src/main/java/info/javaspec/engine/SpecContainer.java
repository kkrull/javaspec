package info.javaspec.engine;

import org.junit.platform.engine.TestDescriptor;

public class SpecContainer {
  private LambdaSpec spec;

  public void addSpec(LambdaSpec spec) {
    this.spec = spec;
  }

  public void addDescriptorsTo(TestDescriptor parentDescriptor) {
    spec.addTestDescriptorTo(parentDescriptor);
  }
}
