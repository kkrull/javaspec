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
  private final String displayName;
  private final List<Spec> specs;
  private final List<Context> subContexts;

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
        new ArrayList<>(0),
        new ArrayList<>(0)))
      .collect(toList());
    List<Context> subContexts = readInnerClasses(source)
      .map(ClassContext::createSubContext)
      .collect(toList());
    return new ClassContext(contextId, displayName, specs, subContexts);
  }

  protected ClassContext(String id, String displayName, List<Spec> specs, List<Context> subContexts) {
    super(id);
    this.displayName = displayName;
    this.specs = specs;
    this.subContexts = subContexts;
  }

  private String getDisplayName() { return displayName; }
  private Stream<Spec> getSpecs() { return specs.stream(); }
  private Stream<Context> getSubContexts() { return subContexts.stream(); }

  @Override
  public Description getDescription() {
    Description suite = Description.createSuiteDescription(getDisplayName(), getId());
    getSpecs().forEach(x -> x.addDescriptionTo(suite));
    getSubContexts().map(Context::getDescription).forEach(suite::addChild);
    return suite;
  }

  @Override
  public boolean hasSpecs() {
    boolean hasOwnExamples = getSpecs().findAny().isPresent();
    Stream<Boolean> childrenHaveExamples = getSubContexts().map(Context::hasSpecs);
    return hasOwnExamples || childrenHaveExamples.findAny().isPresent();
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
