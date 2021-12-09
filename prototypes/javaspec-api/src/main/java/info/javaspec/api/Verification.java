package info.javaspec.api;

//A procedure that verifies the behavior under test
@FunctionalInterface
public interface Verification {
  void execute() throws Throwable;
}
