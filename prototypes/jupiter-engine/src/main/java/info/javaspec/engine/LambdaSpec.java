package info.javaspec.engine;

import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.support.descriptor.EngineDescriptor;

public class LambdaSpec {
  private final String behavior;
  private final Executable verification;

  public LambdaSpec(String behavior, Executable verification) {
    this.behavior = behavior;
    this.verification = verification;
  }

  public void addTestDescriptorTo(EngineDescriptor parentDescriptor) {
    TestDescriptor specDescriptor = SpecDescriptor.forSpec(
      parentDescriptor.getUniqueId(),
      this.behavior,
      this.verification
    );

    parentDescriptor.addChild(specDescriptor);
  }
}
