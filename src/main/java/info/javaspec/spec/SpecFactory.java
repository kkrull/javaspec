package info.javaspec.spec;

import info.javaspec.context.Context;
import org.junit.runner.Description;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static info.javaspec.util.SpecDesignator.identifierToDisplayName;

public class SpecFactory {
  public static FieldSpec create(Context context, Field it) {
    String id = String.format("%s#%s", context.getId(), it.getName());
    return new FieldSpec(id, describeSpec(id, it, context), it, new ArrayList<>(0), new ArrayList<>(0));
  }

  private static Description describeSpec(String id, Field it, Context context) {
    return Description.createTestDescription(
      context.getDescription().getClassName(),
      identifierToDisplayName(it.getName()),
      id);
  }
}
