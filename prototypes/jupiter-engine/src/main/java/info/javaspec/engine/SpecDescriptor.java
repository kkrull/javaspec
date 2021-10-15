package info.javaspec.engine;

import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;

public class SpecDescriptor extends AbstractTestDescriptor {
  private final Executable verification;

  public static SpecDescriptor forSpec(UniqueId parentId, String behavior, Executable verification) {
    return new SpecDescriptor(
      parentId.append("spec", behavior),
      behavior,
      verification
    );
  }

  private SpecDescriptor(UniqueId uniqueId, String displayName, Executable verification) {
    super(uniqueId, displayName);
    this.verification = verification;
  }

  private String behavior() {
    return this.getDisplayName();
  }

  @Override
  public Type getType() {
    return Type.TEST;
  }

  public void runSpec() {
    this.verification.execute();
  }
}
