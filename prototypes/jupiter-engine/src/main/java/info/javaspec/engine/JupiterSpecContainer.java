package info.javaspec.engine;

import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;

public class JupiterSpecContainer {
  private JupiterSpec spec;

  public void addSpec(JupiterSpec spec) {
    this.spec = spec;
  }

  public void addDescriptorsTo(TestDescriptor parentDescriptor) {
    ContainerDescriptor containerDescriptor = ContainerDescriptor.forClass(
      parentDescriptor.getUniqueId(),
      GreeterSpecs.class //TODO KDK [1]: Lift parameter to constructor
    );
    parentDescriptor.addChild(containerDescriptor);

    spec.addTestDescriptorTo(containerDescriptor);
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
