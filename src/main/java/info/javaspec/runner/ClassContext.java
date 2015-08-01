package info.javaspec.runner;

import info.javaspec.dsl.It;
import info.javaspec.util.ReflectionUtil;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

class ClassContext extends Context {
  private final Class<?> source;

  public ClassContext(String id, String displayName, Class<?> source) {
    super(id, displayName);
    this.source = source;
  }

  public ClassContext(String id, String displayName, Class<?> source, List<ClassContext> subcontexts) {
    super(id, displayName);
    this.source = source;
    throw new UnsupportedOperationException();
  }

  public Class<?> getSource() { return source; }

  @Override
  public Description getDescription() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean hasSpecs() {
    boolean hasOwnExamples = getSpecs().findAny().isPresent();
    boolean childrenHaveExamples = getSubContexts().anyMatch(ClassContext::hasSpecs);
    return hasOwnExamples || childrenHaveExamples;
  }

  @Override
  public long numSpecs() {
    long declaredInSelf = getSpecs().count();
    long declaredInDescendants = getSubContexts()
      .map(ClassContext::numSpecs)
      .collect(Collectors.summingLong(x -> x));

    return declaredInSelf + declaredInDescendants;
  }

  @Override
  public void run(RunNotifier notifier) {
  }

  private Stream<Field> getSpecs() {
    return readDeclaredItFields(source);
  }

  protected Stream<ClassContext> getSubContexts() {
    return readInnerClasses(source)
      .map(child -> new ClassContext("id", "display", child));
  }

  private static Stream<Field> readDeclaredItFields(Class<?> context) {
    return readDeclaredFields(context, It.class);
  }

  private static Optional<Field> onlyDeclaredField(Class<?> context, Class<?> fieldType) {
    List<Field> fields = readDeclaredFields(context, fieldType).limit(2).collect(toList());
    switch(fields.size()) {
      case 0: return Optional.empty();
      case 1: return Optional.of(fields.get(0));
      default: throw new ClassSpecGateway.AmbiguousSpecFixture(context, fieldType);
    }
  }

  private static Stream<Field> readDeclaredFields(Class<?> context, Class<?> fieldType) {
    Predicate<Field> isInstanceField = x -> !Modifier.isStatic(x.getModifiers());
    return ReflectionUtil.fieldsOfType(fieldType, context)
      .filter(isInstanceField);
  }

  private static Stream<Class<?>> readInnerClasses(Class<?> parent) {
    Predicate<Class<?>> isNonStatic = x -> !Modifier.isStatic(x.getModifiers());
    return Stream.of(parent.getDeclaredClasses())
      .filter(isNonStatic);
  }
}
