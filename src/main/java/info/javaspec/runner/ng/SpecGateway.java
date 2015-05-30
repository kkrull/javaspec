package info.javaspec.runner.ng;

/** Provides access to specs that are organized into a hierarchy of context. */
interface SpecGateway {
  String rootContextId();

  boolean hasSpecs();
  long countSpecs();
}
