package info.javaspec.console.helpers;

public class SpecHelper {
  private Class<?> declaringClass;

  Class<?> declaringClass() {
    return declaringClass;
  }

  public void setDeclaringClass(Class<?> declaringClass) {
    this.declaringClass = declaringClass;
  }
}
