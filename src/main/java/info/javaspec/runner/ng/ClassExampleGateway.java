package info.javaspec.runner.ng;

import info.javaspec.dsl.It;
import info.javaspec.util.ReflectionUtil;
import org.junit.runner.Description;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ClassExampleGateway implements NewExampleGateway {
  private final Class<?> rootContext;

  public ClassExampleGateway(Class<?> rootContextClass) {
    this.rootContext = rootContextClass;
  }

  @Override
  public String rootContextName() {
    return rootContext.getSimpleName();
  }

  @Override
  public boolean hasExamples() {
    return hasExamples(rootContext);
  }

  private boolean hasExamples(Class<?> context) {
    boolean hasOwnExamples = readDeclaredItFields(context).findAny().isPresent();
    return hasOwnExamples || readInnerClasses(context).anyMatch(this::hasExamples);
  }

  @Override
  public long totalNumExamples() {
    return totalNumExamples(rootContext);
  }

  private long totalNumExamples(Class<?> context) {
    long declaredInSelf = readDeclaredItFields(context).count();
    long declaredInDescendants = readInnerClasses(context)
      .map(this::totalNumExamples)
      .collect(Collectors.summingLong(x -> x));

    return declaredInSelf + declaredInDescendants;
  }

  @Override
  public Description junitDescriptionTree() {
    //TODO KDK: If it's the root context and it only has 1 test, just return the test description
    //TODO KDK: Name the root context with the *raw* class name
    return junitDescriptionTree(rootContext);
  }

  private Description junitDescriptionTree(Class<?> context) {
    String contextName = context == this.rootContext ? rootContextName() : humanize(context.getSimpleName());
    Description suiteDescription = Description.createSuiteDescription(contextName);

    readDeclaredItFields(context)
      .map(Field::getName)
      .map(this::humanize)
      .map(x -> testDescription(contextName, x))
      .forEach(suiteDescription::addChild);

    readInnerClasses(context)
      .map(this::junitDescriptionTree)
      .forEach(suiteDescription::addChild);

    if(suiteDescription.getChildren().size() == 0)
      return Description.EMPTY;
    else
      return suiteDescription;
  }

  private String humanize(String behaviorOrContext) {
    return behaviorOrContext.replace('_', ' ');
  }

  private static Description testDescription(String contextName, String humanizedBehavior) {
    return Description.createTestDescription(contextName, humanizedBehavior);
  }

  private static Stream<Field> readDeclaredItFields(Class<?> context) {
    Predicate<Field> isInstanceField = x -> !Modifier.isStatic(x.getModifiers());
    return ReflectionUtil.fieldsOfType(It.class, context).filter(isInstanceField);
  }

  private static Stream<Class<?>> readInnerClasses(Class<?> parent) {
    Predicate<Class<?>> isNonStatic = x -> !Modifier.isStatic(x.getModifiers());
    return Stream.of(parent.getDeclaredClasses()).filter(isNonStatic);
  }
}
