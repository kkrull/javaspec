package info.javaspec.runner.ng;

import info.javaspec.dsl.It;
import info.javaspec.util.ReflectionUtil;
import org.junit.runner.Description;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.function.Predicate;
import java.util.stream.Collectors;
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

  private boolean hasExamples(Class<?> context) {
    boolean hasOwnExamples = readDeclaredItFields(context).findAny().isPresent();
    return hasOwnExamples || readNestedInnerClasses(context).anyMatch(this::hasExamples);
  }

  @Override
  public long totalNumExamples() {
    return totalNumExamples(rootContext);
  }

  private long totalNumExamples(Class<?> context) {
    long declaredInSelf = readDeclaredItFields(context).count();
    long declaredInDescendants = readNestedInnerClasses(context)
      .map(this::totalNumExamples)
      .collect(Collectors.summingLong(x -> x));

    return declaredInSelf + declaredInDescendants;
  }

  @Override
  public Description junitDescriptionTree() {
    Field onlyExample = readDeclaredItFields(rootContext).findAny().get();
    return Description.createTestDescription(
      rootContext.getSimpleName(),
      onlyExample.getName().replace('_', ' '));
  }

  private Stream<Field> readDeclaredItFields(Class<?> context) {
    Predicate<Field> isInstanceField = x -> !Modifier.isStatic(x.getModifiers());
    return ReflectionUtil.fieldsOfType(It.class, context)
      .filter(isInstanceField);
  }

  private static Stream<Class<?>> readNestedInnerClasses(Class<?> parent) {
    Predicate<Class<?>> isNonStaticClass = x -> !Modifier.isStatic(x.getModifiers());
    return Stream.of(parent.getDeclaredClasses())
      .filter(isNonStaticClass);
  }
}
