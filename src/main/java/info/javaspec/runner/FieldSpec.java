package info.javaspec.runner;

import info.javaspec.dsl.Before;
import info.javaspec.dsl.Cleanup;
import info.javaspec.dsl.It;
import org.junit.runner.Description;

import java.lang.reflect.Field;
import java.util.List;

import static java.util.stream.Collectors.toList;

final class FieldSpec extends Spec {
  private final String displayName;
  private final Field assertionField;
  private final List<Field> befores;
  private final List<Field> afters;
  private TestFunction testFunction;

  public static FieldSpec create(String contextId, Field it, List<Field> befores, List<Field> afters) {
    String id = String.format("%s#%s", contextId, it.getName());
    return new FieldSpec(id, humanize(it.getName()), it, befores, afters);
  }

  private static String humanize(String identifier) { return identifier.replace('_', ' '); }

  private FieldSpec(String id, String displayName, Field it, List<Field> befores, List<Field> afters) {
    super(id);
    this.displayName = displayName;
    this.assertionField = it;
    this.befores = befores;
    this.afters = afters;
  }

  private String getDisplayName() { return displayName; }

  @Override
  public void addDescriptionTo(Description suite) {
    Description description = Description.createTestDescription(suite.getClassName(), getDisplayName(), getId());
    suite.addChild(description);
  }

  @Override
  public boolean isIgnored() {
    return theTestFunction().hasUnassignedFunctions();
  }

  @Override
  public void run() throws Exception {
    TestFunction f = theTestFunction();
    try {
      for(Before before : f.befores) { before.run(); }
      f.assertion.run();
    } finally {
      for(Cleanup after : f.afters) { after.run(); }
    }
  }

  private TestFunction theTestFunction() {
    if(testFunction == null) {
      TestContext context = new TestContext();
      context.init(assertionField.getDeclaringClass());
      try {
        List<Before> beforeValues = befores.stream().map(x -> (Before)context.getAssignedValue(x)).collect(toList());
        List<Cleanup> afterValues = afters.stream().map(x -> (Cleanup)context.getAssignedValue(x)).collect(toList());
        It assertion = (It)context.getAssignedValue(assertionField);
        testFunction = new TestFunction(assertion, beforeValues, afterValues);
      } catch(Throwable t) {
        throw new TestSetupFailed(assertionField.getDeclaringClass(), t);
      }
    }

    return testFunction;
  }

  public static final class TestSetupFailed extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public TestSetupFailed(Class<?> context, Throwable cause) {
      super(String.format("Failed to create test context %s", context.getName()), cause);
    }
  }

  public static final class UnsupportedConstructor extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public UnsupportedConstructor(Class<?> context, Throwable cause) {
      super(String.format("Unable to find a no-argument constructor for class %s", context.getName()), cause);
    }
  }
}
