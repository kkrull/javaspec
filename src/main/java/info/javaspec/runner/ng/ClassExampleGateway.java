package info.javaspec.runner.ng;

import info.javaspec.dsl.It;
import info.javaspec.util.ReflectionUtil;
import org.junit.runner.Description;

import java.lang.reflect.Modifier;
import java.util.stream.Stream;

public final class ClassExampleGateway implements NewExampleGateway {
  private final Class<?> rootContext;

  public ClassExampleGateway(Class<?> rootContextClass) {
    this.rootContext = rootContextClass;
  }

  @Override
  public String rootContextName() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean hasExamples() {
    return hasExamples(rootContext);
  }

  private boolean hasExamples(Class<?> contextClass) {
    boolean rootContextHasExamples = ReflectionUtil.fieldsOfType(It.class, contextClass)
//      .filter(x -> !Modifier.isStatic(x.getModifiers()))
      .findAny()
      .isPresent();
//    boolean rootContextHasExamples = ReflectionUtil.hasFieldsOfType(It.class, contextClass);
    return rootContextHasExamples || readInnerClasses(contextClass).anyMatch(this::hasExamples);
  }

  private static Stream<Class<?>> readInnerClasses(Class<?> parent) {
//    return Stream.of(parent.getDeclaredClasses());
    return Stream.of(parent.getDeclaredClasses()).filter(x -> !Modifier.isStatic(x.getModifiers()));
  }

  @Override
  public int totalNumExamples() {
    return numDeclaredTests(this.rootContext);
  }

  private int numDeclaredTests(Class<?> context) {
    return (int) ReflectionUtil.fieldsOfType(It.class, context).count();
  }

  @Override
  public Description junitDescriptionTree() {
    throw new UnsupportedOperationException();
  }
}
