package info.javaspec.engine;

import info.javaspec.api.Verification;
import org.junit.platform.engine.TestDescriptor;

public class JupiterSpec {
  private final String behavior;
  private final Verification verification;

  public JupiterSpec(String behavior, Verification verification) {
    this.behavior = behavior;
    this.verification = verification;
  }

  public void addTestDescriptorTo(TestDescriptor parentDescriptor) {
    TestDescriptor specDescriptor = SpecDescriptor.forSpec(
      parentDescriptor.getUniqueId(),
      this.behavior,
      this.verification
    );

    parentDescriptor.addChild(specDescriptor);
  }
}
