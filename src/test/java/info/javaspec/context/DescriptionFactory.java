package info.javaspec.context;

import org.junit.runner.Description;

public class DescriptionFactory {
  public static Description descriptionWithId(String id) {
    return Description.createTestDescription(id, "descriptionWithId", id);
  }
}
