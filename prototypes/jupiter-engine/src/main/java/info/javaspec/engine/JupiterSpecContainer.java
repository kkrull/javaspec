package info.javaspec.engine;

import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;

public class JupiterSpecContainer {
  private Class<?> specClass;
  private JupiterSpec spec;

  public JupiterSpecContainer(Class<?> specClass) {
    this.specClass = specClass;
  }

  public void addDescriptorsTo(TestDescriptor parentDescriptor) {
    ContainerDescriptor containerDescriptor = ContainerDescriptor.forClass(
      parentDescriptor.getUniqueId(),
      specClass
    );
    parentDescriptor.addChild(containerDescriptor);

    spec.addTestDescriptorTo(containerDescriptor);
  }

  public void addSpec(JupiterSpec spec) {
    this.spec = spec;
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
