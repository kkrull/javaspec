package info.javaspec.runner;

import info.javaspec.dsl.It;
import info.javaspec.util.ReflectionUtil;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

class ClassContext extends Context {
  private final List<Spec> specs;
  private final List<Context> subContexts;

  public static ClassContext create(Class<?> source) {
    List<Spec> specs = readDeclaredItFields(source)
      .map(x -> FieldSpec.create(
        "ClassContext::create",
        null,
        new ArrayList<>(0),
        new ArrayList<>(0)))
      .collect(toList());
    List<Context> subContexts = readInnerClasses(source)
      .map(ClassContext::create)
      .collect(toList());
    return new ClassContext("", specs, subContexts);
  }

  protected ClassContext(String id, List<Spec> specs, List<Context> subContexts) {
    super(id);
    this.subContexts = subContexts;
    this.specs = specs;
  }

  @Override
  public Description getDescription() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean hasSpecs() {
    boolean hasOwnExamples = getSpecs().findAny().isPresent();
    boolean childrenHaveExamples = getSubContexts().anyMatch(Context::hasSpecs);
    return hasOwnExamples || childrenHaveExamples;
  }

  @Override
  public long numSpecs() {
    long declaredInSelf = getSpecs().count();
    long declaredInDescendants = getSubContexts()
      .map(Context::numSpecs)
      .collect(Collectors.summingLong(x -> x));

    return declaredInSelf + declaredInDescendants;
  }

  @Override
  public void run(RunNotifier notifier) {
    getSpecs().forEach(x -> x.run(notifier));
    getSubContexts().forEach(x -> x.run(notifier));
  }

  private Stream<Spec> getSpecs() { return specs.stream(); }

  private Stream<Context> getSubContexts() { return subContexts.stream(); }

  private static Stream<Field> readDeclaredItFields(Class<?> context) {
    return readDeclaredFields(context, It.class);
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
