package info.javaspec.engine;

import info.javaspec.api.JavaSpec;
import info.javaspec.api.Verification;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;

import java.util.Optional;

//A JavaSpec declaration that runs 1 or more specs on JUnit
public class JavaSpecForJupiter extends AbstractTestDescriptor implements JavaSpec {
  private JupiterSpec spec;

  public static JavaSpecForJupiter forSpecClass(UniqueId parentId, Class<?> specClass) {
    return new JavaSpecForJupiter(
      parentId.append("class", specClass.getName()),
      specClass.getName()
    );
  }

  private JavaSpecForJupiter(UniqueId uniqueId, String displayName) {
    super(uniqueId, displayName);
  }

  /* Jupiter */

  @Override
  public Type getType() {
    return Type.CONTAINER;
  }

  public void addDescriptorsTo(TestDescriptor parentDescriptor) {
    parentDescriptor.addChild(this);
    Optional.ofNullable(this.spec)
      .ifPresent(x -> x.addTestDescriptorTo(this));
  }

  /* JavaSpec syntax */

  @Override
  public void it(String behavior, Verification verification) {
    this.spec = JupiterSpec.forBehavior(getUniqueId(), behavior, verification);
  }
}