package info.javaspec.context;

import info.javaspec.dsl.It;
import info.javaspec.spec.FieldSpec;
import info.javaspec.spec.Spec;
import info.javaspec.util.ReflectionUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class ClassContextFactory {
  public static ClassContext createRootContext(Class<?> source) {
    return create(source, source.getSimpleName());
  }

  private static ClassContext createSubContext(Class<?> source) {
    return create(source, humanize(source.getSimpleName()));
  }

  private static ClassContext create(Class<?> source, String displayName) {
    String contextId = source.getCanonicalName();
    List<Spec> specs = readDeclaredItFields(source)
      .map(it -> FieldSpec.create(
        contextId,
        it,
        new ArrayList<>(0), //TODO KDK: Work here too
        new ArrayList<>(0)))
      .collect(toList());
    List<Context> subContexts = readInnerClasses(source)
      .map(ClassContextFactory::createSubContext)
      .collect(toList());
    return new ClassContext(contextId, displayName, specs, subContexts);
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
