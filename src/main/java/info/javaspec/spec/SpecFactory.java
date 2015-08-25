package info.javaspec.spec;

import info.javaspec.context.Context;
import info.javaspec.dsl.*;
import info.javaspec.util.ReflectionBasedFactory;
import info.javaspec.util.ReflectionUtil;
import org.junit.runner.Description;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class SpecFactory extends ReflectionBasedFactory {
  private final Context context;

  public SpecFactory(Context context) {
    this.context = context;
  }

  public void addSpecsFromClass(Class<?> source) {
    readDeclaredItFields(source)
      .map(this::create)
      .forEach(context::addSpec);
  }

  Spec create(Field it) {
    String id = String.format("%s#%s", context.getId(), it.getName());
    Description description = context.describeSpec(id, identifierToDisplayName(it.getName()));

    List<Field> beforeFields = Stream.concat(
      readDeclaredFields(it.getDeclaringClass(), Establish.class),
      readDeclaredFields(it.getDeclaringClass(), Because.class))
      .collect(toList());
    List<Field> afterFields = readDeclaredFields(it.getDeclaringClass(), Cleanup.class).collect(toList());

    return getAssignedValue(it)
      .map(x -> new FieldSpec(id, description, it, beforeFields, afterFields))
      .map(Spec.class::cast)
      .orElseGet(() -> new PendingSpec(id, description));
  }

  private static Stream<Field> readDeclaredItFields(Class<?> contextClass) {
    return readDeclaredFields(contextClass, It.class);
  }

  private static Stream<Field> readDeclaredFields(Class<?> contextClass, Class<?> fieldType) {
    Predicate<Field> isInstanceField = x -> !Modifier.isStatic(x.getModifiers());
    return ReflectionUtil.fieldsOfType(fieldType, contextClass).filter(isInstanceField);
  }

  private Optional<?> getAssignedValue(Field it) {
    SpecExecutionContext executionContext = SpecExecutionContext.forDeclaringClass(it.getDeclaringClass());
    return Optional.ofNullable(executionContext.getAssignedValue(it));
  }
}
