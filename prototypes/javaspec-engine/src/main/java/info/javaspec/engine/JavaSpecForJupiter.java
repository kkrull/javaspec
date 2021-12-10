package info.javaspec.engine;

import info.javaspec.api.JavaSpec;
import info.javaspec.api.Verification;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;

public class JavaSpecForJupiter implements JavaSpec {
  private final Class<?> specClass;
  private JupiterSpec spec;

  public JavaSpecForJupiter(Class<?> specClass) {
    this.specClass = specClass;
  }

  @Override
  public void it(String behavior, Verification verification) {
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

  private static class ContainerDescriptor extends AbstractTestDescriptor {
    public static ContainerDescriptor forClass(UniqueId parentId, Class<?> specClass) {
      return new ContainerDescriptor(
        parentId.append("class", specClass.getName()),
        specClass.getName()
      );
    }

    private ContainerDescriptor(UniqueId uniqueId, String displayName) {
      super(uniqueId, displayName);
    }

    @Override
    public Type getType() {
      return Type.CONTAINER;
    }
  }
}
