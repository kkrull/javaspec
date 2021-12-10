package info.javaspec.engine;

import info.javaspec.api.Verification;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;

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

  //TODO KDK: Does it make sense for this to be separate from JupiterSpec?
  public static class SpecDescriptor extends AbstractTestDescriptor {
    private final Verification verification;

    public static SpecDescriptor forSpec(UniqueId parentId, String behavior, Verification verification) {
      return new SpecDescriptor(
        parentId.append("spec", behavior),
        behavior,
        verification
      );
    }

    private SpecDescriptor(UniqueId uniqueId, String displayName, Verification verification) {
      super(uniqueId, displayName);
      this.verification = verification;
    }

    @Override
    public Type getType() {
      return Type.TEST;
    }

    public void runSpec() {
      this.verification.execute();
    }
  }
}
