package info.javaspec.spec;

import org.junit.runner.Description;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class SpecBuilder {
  private final Class<?> contextClass;
  private final List<Field> beforeFields = new LinkedList<>();
  private final List<Field> afterFields = new LinkedList<>();

  public static SpecBuilder forClass(Class<?> contextClass) {
    return new SpecBuilder(contextClass);
  }

  private SpecBuilder(Class<?> contextClass) {
    this.contextClass = contextClass;
  }

  public SpecBuilder withBeforeFieldsNamed(String... names) {
    Stream.of(names).map(this::readField).forEach(beforeFields::add);
    return this;
  }

  public SpecBuilder withAfterFieldsNamed(String... names) {
    Stream.of(names).map(this::readField).forEach(afterFields::add);
    return this;
  }

  public Spec buildForItFieldNamed(String name) {
    return new FieldSpec(
      name,
      Description.createTestDescription(contextClass, name),
      readField(name),
      beforeFields,
      afterFields);
  }

  private Field readField(String name) {
    return readField(contextClass, name);
  }

  public static Field readField(Class<?> context, String name) {
    try {
      return context.getDeclaredField(name);
    } catch(Exception e) {
      String message = String.format("Failed to read field %s from %s", name, context);
      throw new RuntimeException(message, e);
    }
  }
}
