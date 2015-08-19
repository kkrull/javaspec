package info.javaspec.spec;

import info.javaspec.context.Context;
import org.junit.runner.Description;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class SpecFactory {
  public static FieldSpec create(Context context, Field it) {
    return create(
      context.getId(),
      context.getDescription().getClassName(),
      it,
      new ArrayList<>(0),
      new ArrayList<>(0));
  }

  public static FieldSpec create(String contextId, String descriptionClassName, Field it, List<Field> befores, List<Field> afters) {
    String id = String.format("%s#%s", contextId, it.getName());
    Description testDescription = Description.createTestDescription(descriptionClassName, humanize(it.getName()), id);
    return new FieldSpec(id, testDescription, it, befores, afters);
  }

  private static String humanize(String identifier) { return identifier.replace('_', ' '); }
}
