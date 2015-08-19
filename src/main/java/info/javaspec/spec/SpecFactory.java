package info.javaspec.spec;

import info.javaspec.context.Context;
import info.javaspec.util.ReflectionBasedFactory;
import org.junit.runner.Description;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class SpecFactory extends ReflectionBasedFactory {
  private final Context context;

  public SpecFactory(Context context) {
    this.context = context;
  }

  public FieldSpec create(Field it) {
    String id = String.format("%s#%s", context.getId(), it.getName());
    Description description = context.describeSpec(id, identifierToDisplayName(it.getName()));
    return new FieldSpec(id, description, it, new ArrayList<>(0), new ArrayList<>(0));
  }
}
