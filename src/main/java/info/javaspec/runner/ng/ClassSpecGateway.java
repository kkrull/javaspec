package info.javaspec.runner.ng;

import info.javaspec.dsl.It;
import info.javaspec.util.ReflectionUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Reads specs from no-arg lambdas assigned to It fields in a hierarchy of context classes. */
public final class ClassSpecGateway implements SpecGateway {
  private final Class<?> rootContext;

  public ClassSpecGateway(Class<?> rootContext) {
    this.rootContext = rootContext;
  }

  @Override
  public String rootContextId() {
    return rootContext.getName();
  }

  @Override
  public boolean hasSpecs() {
    return hasSpecs(rootContext);
  }

  private boolean hasSpecs(Class<?> context) {
    boolean hasOwnExamples = readDeclaredItFields(context).findAny().isPresent();
    return hasOwnExamples || readInnerClasses(context).anyMatch(this::hasSpecs);
  }

  @Override
  public long countSpecs() {
    return countSpecs(rootContext);
  }

  private long countSpecs(Class<?> context) {
    long declaredInSelf = readDeclaredItFields(context).count();
    long declaredInDescendants = readInnerClasses(context)
      .map(this::countSpecs)
      .collect(Collectors.summingLong(x -> x));

    return declaredInSelf + declaredInDescendants;
  }

  private static Stream<Field> readDeclaredItFields(Class<?> context) {
    Predicate<Field> isInstanceField = x -> !Modifier.isStatic(x.getModifiers());
    return ReflectionUtil.fieldsOfType(It.class, context).filter(isInstanceField);
  }

  private static Stream<Class<?>> readInnerClasses(Class<?> parent) {
    Predicate<Class<?>> isNonStatic = x -> !Modifier.isStatic(x.getModifiers());
    return Stream.of(parent.getDeclaredClasses()).filter(isNonStatic);
  }
}
