package info.javaspec.runner.ng;

import info.javaspec.dsl.It;
import info.javaspec.util.ReflectionUtil;
import org.junit.runner.Description;

import java.util.stream.Stream;

public final class ClassExampleGateway implements NewExampleGateway {
  private final Class<?> root;

  public ClassExampleGateway(Class<?> rootContextClass) {
    this.root = rootContextClass;
  }

  @Override
  public String rootContextName() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean hasExamples() {
    return hasExamples(root);
  }

  private boolean hasExamples(Class<?> contextClass) {
    boolean rootContextHasExamples = ReflectionUtil.hasFieldsOfType(It.class, contextClass);
    return rootContextHasExamples || readInnerClasses(contextClass).anyMatch(this::hasExamples);
  }

  private static Stream<Class<?>> readInnerClasses(Class<?> parent) {
    return Stream.of(parent.getDeclaredClasses());
//    return Stream.of(parent.getDeclaredClasses()).filter(x -> !Modifier.isStatic(x.getModifiers()));
  }

  @Override
  public int totalNumExamples() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Description junitDescriptionTree() {
    throw new UnsupportedOperationException();
  }
}
