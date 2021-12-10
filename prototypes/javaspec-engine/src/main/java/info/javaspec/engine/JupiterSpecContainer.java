package info.javaspec.engine;

import info.javaspec.api.SpecContainer;
import info.javaspec.api.Verification;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;

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
