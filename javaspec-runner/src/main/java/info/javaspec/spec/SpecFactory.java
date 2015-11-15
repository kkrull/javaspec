package info.javaspec.spec;

import info.javaspec.context.Context;
import info.javaspec.dsl.Because;
import info.javaspec.dsl.Cleanup;
import info.javaspec.dsl.Establish;
import info.javaspec.dsl.It;
import info.javaspec.util.ReflectionBasedFactory;
import info.javaspec.util.ReflectionUtil;
import org.junit.runner.Description;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
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

  public Spec create(Field it) {
    String id = String.format("%s#%s", context.getId(), it.getName());
    Description description = context.describeSpec(id, identifierToDisplayName(it.getName()));
    List<Field> beforeFields = readBeforeSpecFields(it.getDeclaringClass());
    List<Field> afterFields = readAfterSpecFields(it.getDeclaringClass());
    return new FieldSpec(id, description, it, beforeFields, afterFields);
  }

  private List<Field> readBeforeSpecFields(Class<?> assertionClass) {
    LinkedList<Field> fields = new LinkedList<>();
    Consumer<Field> prependToFields = x -> fields.add(0, x);
    for(Class<?> c = assertionClass; c != null; c = c.getEnclosingClass()) {
      onlyDeclaredField(c, Because.class).ifPresent(prependToFields);
      onlyDeclaredField(c, Establish.class).ifPresent(prependToFields);
    }

    return fields;
  }

  private List<Field> readAfterSpecFields(Class<?> assertionClass) {
    List<Field> fields = new LinkedList<>();
    for(Class<?> c = assertionClass; c != null; c = c.getEnclosingClass())
      onlyDeclaredField(c, Cleanup.class).ifPresent(fields::add);

    return fields;
  }

  private static Optional<Field> onlyDeclaredField(Class<?> context, Class<?> fieldType) {
    List<Field> fields = readDeclaredFields(context, fieldType).limit(2).collect(toList());
    switch(fields.size()) {
      case 0: return Optional.empty();
      case 1: return Optional.of(fields.get(0));
      default: throw AmbiguousFixture.forFieldOfType(fieldType, context);
    }
  }

  private static Stream<Field> readDeclaredItFields(Class<?> contextClass) {
    return readDeclaredFields(contextClass, It.class);
  }

  private static Stream<Field> readDeclaredFields(Class<?> contextClass, Class<?> fieldType) {
    Predicate<Field> isInstanceField = x -> !Modifier.isStatic(x.getModifiers());
    return ReflectionUtil.fieldsOfType(fieldType, contextClass).filter(isInstanceField);
  }

  static final class AmbiguousFixture extends RuntimeException {
    public static AmbiguousFixture forFieldOfType(Class<?> fieldClass, Class<?> contextClass) {
      String message = String.format("Only 1 field of type %s is allowed in context class %s",
        fieldClass.getSimpleName(), contextClass);
      return new AmbiguousFixture(message);
    }

    private AmbiguousFixture(String message) {
      super(message);
    }
  }
}
