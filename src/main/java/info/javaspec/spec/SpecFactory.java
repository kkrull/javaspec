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
import java.util.function.Consumer;
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

    List<Field> beforeFields = readBeforeSpecFields(it.getDeclaringClass());
    List<Field> afterFields = readDeclaredFields(it.getDeclaringClass(), Cleanup.class).collect(toList());

    return getAssignedValue(it)
      .map(x -> new FieldSpec(id, description, it, beforeFields, afterFields))
      .map(Spec.class::cast)
      .orElseGet(() -> new PendingSpec(id, description));
  }

  private List<Field> readBeforeSpecFields(Class<?> declaringClass) {
    LinkedList<Field> fields = new LinkedList<>();
    Consumer<Field> prependToFields = x -> fields.add(0, x);
    for(Class<?> c = declaringClass; c != null; c = c.getEnclosingClass()) {
      onlyDeclaredField(c, Because.class).ifPresent(prependToFields);
      onlyDeclaredField(c, Establish.class).ifPresent(prependToFields);
    }

    return fields;
  }

  private static Optional<Field> onlyDeclaredField(Class<?> context, Class<?> fieldType) {
    List<Field> fields = readDeclaredFields(context, fieldType).limit(2).collect(toList());
    switch(fields.size()) {
      case 0: return Optional.empty();
      case 1: return Optional.of(fields.get(0));
      default: throw new AmbiguousSpecFixture(context, fieldType);
    }
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

  public static final class AmbiguousSpecFixture extends RuntimeException {
    public AmbiguousSpecFixture(Class<?> contextClass, Class<?> fieldClass) {
      super(String.format("Only 1 field of type %s is allowed in context class %s",
        fieldClass.getSimpleName(),
        contextClass));
    }
  }
}
