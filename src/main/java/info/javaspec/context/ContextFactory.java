package info.javaspec.context;

import info.javaspec.spec.SpecFactory;
import info.javaspec.util.ReflectionBasedFactory;
import org.junit.runner.Description;

import java.lang.reflect.Modifier;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ContextFactory extends ReflectionBasedFactory {
  public static ClassContext createRootContext(Class<?> source) {
    return new ContextFactory().create(source, source.getSimpleName());
  }

  private ClassContext createSubContext(Class<?> source) {
    return create(source, identifierToDisplayName(source.getSimpleName()));
  }

  private ClassContext create(Class<?> source, String displayName) {
    String contextId = source.getCanonicalName();
    ClassContext context = new ClassContext(contextId, Description.createSuiteDescription(displayName, contextId));

    SpecFactory specFactory = new SpecFactory(context);
    specFactory.addSpecsFromClass(source);

    readInnerClasses(source)
      .map(this::createSubContext)
      .forEach(context::addSubContext);

    return context;
  }

  private static Stream<Class<?>> readInnerClasses(Class<?> parent) {
    Predicate<Class<?>> isNonStatic = x -> !Modifier.isStatic(x.getModifiers());
    return Stream.of(parent.getDeclaredClasses()).filter(isNonStatic);
  }
}
