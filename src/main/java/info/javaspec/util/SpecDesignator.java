package info.javaspec.util;

//It's ironic how the class that names things has trouble finding a good name for itself.
public class SpecDesignator {
  public static String identifierToDisplayName(String identifier) {
    return identifier.replace('_', ' ');
  }
}
