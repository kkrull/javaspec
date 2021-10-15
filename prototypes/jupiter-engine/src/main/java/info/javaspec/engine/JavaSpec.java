package info.javaspec.engine;

public class JavaSpec {
  public LambdaSpec it(String behavior, Executable verification) {
    return new LambdaSpec(behavior, verification);
  }
}
