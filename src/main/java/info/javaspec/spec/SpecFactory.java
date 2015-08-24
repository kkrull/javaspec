package info.javaspec.spec;

import info.javaspec.context.Context;
import info.javaspec.dsl.It;
import info.javaspec.util.ReflectionBasedFactory;
import info.javaspec.util.ReflectionUtil;
import org.junit.runner.Description;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

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

  private static Stream<Field> readDeclaredItFields(Class<?> contextClass) {
    return readDeclaredFields(contextClass, It.class);
  }

  private static Stream<Field> readDeclaredFields(Class<?> contextClass, Class<?> fieldType) {
    Predicate<Field> isInstanceField = x -> !Modifier.isStatic(x.getModifiers());
    return ReflectionUtil.fieldsOfType(fieldType, contextClass).filter(isInstanceField);
  }

  public Spec create(Field it) {
    String id = String.format("%s#%s", context.getId(), it.getName());
    Description description = context.describeSpec(id, identifierToDisplayName(it.getName()));

    List<Field> beforeFields = new LinkedList<>();
    beforeFields.add(readField(clazz, Establish.class);

    return getAssignedValue(it)
      .map(x -> new FieldSpec(id, description, it, beforeFields, new ArrayList<>(0)))
      .map(Spec.class::cast)
      .orElseGet(() -> new PendingSpec(id, description));
  }

  private Optional<?> getAssignedValue(Field it) {
    SpecExecutionContext executionContext = SpecExecutionContext.forDeclaringClass(it.getDeclaringClass());
    return Optional.ofNullable(executionContext.getAssignedValue(it));
  }
}
