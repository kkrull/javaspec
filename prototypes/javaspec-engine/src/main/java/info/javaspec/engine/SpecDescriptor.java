package info.javaspec.engine;

import info.javaspec.api.Verification;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;

public class SpecDescriptor extends AbstractTestDescriptor {
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
