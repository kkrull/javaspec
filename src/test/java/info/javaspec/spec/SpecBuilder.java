package info.javaspec.spec;

import info.javaspec.context.Context;
import info.javaspec.context.FakeContext;
import info.javaspecproto.ContextClasses;
import org.junit.runner.Description;

import java.lang.reflect.Field;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toList;
import static org.junit.runner.Description.createSuiteDescription;

public class SpecBuilder {

  public static Spec exampleWithFullFixture() {
    return exampleWith(ContextClasses.FullFixture.class, "asserts",
      newArrayList("arranges", "acts"), newArrayList("cleans"));
  }

  public static Spec exampleWithIt(Class<?> context, String name) {
    return exampleWith(context, name, newArrayList(), newArrayList());
  }

  public static Spec exampleWithNestedFullFixture() {
    Context context = FakeContext.withDescription(createSuiteDescription(ContextClasses.NestedFullFixture.class));
    SpecFactory specFactory = new SpecFactory(context);
    return specFactory.create(readField(ContextClasses.NestedFullFixture.innerContext.class, "asserts"));
  }

  public static Spec exampleWith(Class<?> contextClass, String it, List<String> befores, List<String> afters) {
    try {
      return new FieldSpec(
        it,
        Description.createTestDescription(contextClass, it),
        it == null ? null : readField(contextClass, it),
        befores.stream().map(x -> readField(contextClass, x)).collect(toList()),
        afters.stream().map(x -> readField(contextClass, x)).collect(toList()));
    } catch(Exception e) {
      throw new RuntimeException("Test setup failed", e);
    }
  }

  private static Field readField(Class<?> context, String name) {
    try {
      return context.getDeclaredField(name);
    } catch(Exception e) {
      throw new RuntimeException(e);
    }
  }
}
