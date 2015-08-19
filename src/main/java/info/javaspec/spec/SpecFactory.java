package info.javaspec.spec;

import info.javaspec.context.Context;
import org.junit.runner.Description;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static info.javaspec.util.SpecDesignator.identifierToDisplayName;

public class SpecFactory {
  public static FieldSpec create(Context context, Field it) {
    String id = String.format("%s#%s", context.getId(), it.getName());
    Description description = context.describeSpec(id, identifierToDisplayName(it.getName()));
    return new FieldSpec(id, description, it, new ArrayList<>(0), new ArrayList<>(0));
  }
}
