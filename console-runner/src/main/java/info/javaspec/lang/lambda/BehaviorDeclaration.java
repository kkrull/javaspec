package info.javaspec.lang.lambda;

/** A lambda declaring 0 or more specs that describe the same thing */
@FunctionalInterface
public interface BehaviorDeclaration {
  void declareSpecs();
}
