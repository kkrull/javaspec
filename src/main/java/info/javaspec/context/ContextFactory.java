package info.javaspec.context;

import info.javaspec.dsl.It;
import info.javaspec.spec.SpecFactory;
import info.javaspec.util.ReflectionUtil;
import org.junit.runner.Description;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ContextFactory {
  public static ClassContext createRootContext(Class<?> source) {
    return create(source, source.getSimpleName());
  }

  private static ClassContext createSubContext(Class<?> source) {
    return create(source, humanize(source.getSimpleName()));
  }

  private static ClassContext create(Class<?> source, String displayName) {
    String contextId = source.getCanonicalName();
    Description suite = Description.createSuiteDescription(displayName, contextId);
    ClassContext context = new ClassContext(contextId, suite);

    readDeclaredItFields(source)
      .map(it -> SpecFactory.create(context, it))
      .forEach(context::addSpec);

    readInnerClasses(source)
      .map(ContextFactory::createSubContext)
      .forEach(context::addSubContext);

    return context;
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
