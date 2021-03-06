package info.javaspec;

/** A behavior-verifying procedure, described by natural language for the intended behavior. */
public interface Spec {
  String intendedBehavior();

  void run(RunObserver observer);
}
