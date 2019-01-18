package info.javaspec.console;

public class SpecHelper {
  private Class<?> declaringClass;

  public Class<?> declaringClass() {
    return declaringClass;
  }

  public void setDeclaringClass(Class<?> declaringClass) {
    this.declaringClass = declaringClass;
  }
}
