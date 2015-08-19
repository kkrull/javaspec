package info.javaspec.spec;

import info.javaspec.context.Context;
import org.junit.runner.Description;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class SpecFactory {
  public static FieldSpec create(Context context, Field it) {
    String id = String.format("%s#%s", context.getId(), it.getName());
    Description testDescription = Description.createTestDescription(context.getDescription().getClassName(), humanize(it.getName()), id);
    return new FieldSpec(id, testDescription, it, new ArrayList<>(0), new ArrayList<>(0));
  }

  private static String humanize(String identifier) { return identifier.replace('_', ' '); }
}
