package info.javaspec.runner;

import java.util.stream.Stream;

/** Provides access to specs that are organized into a hierarchy of context. */
interface SpecGateway<C extends Context> {
  C rootContext();
  String rootContextId();
  Stream<C> getSubcontexts(C context);

  boolean hasSpecs();
  long countSpecs();
  Stream<Spec> getSpecs(C context);
}
