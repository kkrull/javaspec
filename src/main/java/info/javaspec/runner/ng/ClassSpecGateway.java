package info.javaspec.runner.ng;

import info.javaspec.dsl.It;
import info.javaspec.util.ReflectionUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.function.Predicate;
import java.util.stream.Stream;

/** Reads specs from no-arg lambdas assigned to It fields in a hierarchy of context classes. */
public class ClassSpecGateway implements SpecGateway {
  private final Class<?> rootContext;

  public ClassSpecGateway(Class<?> rootContext) {
    this.rootContext = rootContext;
  }

  @Override
  public String rootContextId() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean hasSpecs() {
    return readDeclaredItFields(rootContext).findAny().isPresent();
  }

  @Override
  public long countSpecs() {
    return readDeclaredItFields(rootContext).count();
  }

  private static Stream<Field> readDeclaredItFields(Class<?> context) {
//    Predicate<Field> isInstanceField = x -> !Modifier.isStatic(x.getModifiers());
    return ReflectionUtil.fieldsOfType(It.class, context);
//      .filter(isInstanceField);
  }
}
