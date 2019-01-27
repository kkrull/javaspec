package info.javaspec.console.helpers;

public class SpecHelper {
  private Class<?> declaringClass;

  Class<?> getDeclaringClass() {
    return this.declaringClass;
  }

  public void setDeclaringClass(Class<?> declaringClass) {
    this.declaringClass = declaringClass;
  }
}
