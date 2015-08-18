package info.javaspec.context;

import info.javaspec.dsl.It;
import info.javaspec.spec.FieldSpec;
import info.javaspec.util.ReflectionUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ClassContextFactory {
  public static ClassContext createRootContext(Class<?> source) {
    return create(source, source.getSimpleName());
  }

  private static ClassContext createSubContext(Class<?> source) {
    return create(source, humanize(source.getSimpleName()));
  }

  private static ClassContext create(Class<?> source, String displayName) {
    String contextId = source.getCanonicalName();
    ClassContext context = new ClassContext(contextId, displayName);

    readDeclaredItFields(source)
      .map(it -> createFieldSpec(contextId, it))
      .forEach(context::addSpec);

    readInnerClasses(source)
      .map(ClassContextFactory::createSubContext)
      .forEach(context::addSubContext);

    return context;
  }

  private static FieldSpec createFieldSpec(String contextId, Field it) {
    return FieldSpec.create(
      contextId,
      it,
      new ArrayList<>(0),
      new ArrayList<>(0));
  }

  private static Stream<Field> readDeclaredItFields(Class<?> context) {
    return readDeclaredFields(context, It.class);
  }

  private static Stream<Field> readDeclaredFields(Class<?> context, Class<?> fieldType) {
    Predicate<Field> isInstanceField = x -> !Modifier.isStatic(x.getModifiers());
    return ReflectionUtil.fieldsOfType(fieldType, context).filter(isInstanceField);
  }

  private static Stream<Class<?>> readInnerClasses(Class<?> parent) {
    Predicate<Class<?>> isNonStatic = x -> !Modifier.isStatic(x.getModifiers());
    return Stream.of(parent.getDeclaredClasses()).filter(isNonStatic);
  }

  private static String humanize(String identifier) {
    return identifier.replace('_', ' ');
  }
}
