package info.javaspec.util;

public class ReflectionBasedFactory {
  protected String identifierToDisplayName(String identifier) {
    return identifier.replace('_', ' ');
  }
}
