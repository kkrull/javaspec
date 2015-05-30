package info.javaspec.runner.ng;

import java.util.List;

/** Provides access to specs that are organized into a hierarchy of context. */
interface SpecGateway<C extends Context> {
  C rootContext();
  String rootContextId();
  List<C> getSubcontexts(C context);

  boolean hasSpecs();
  long countSpecs();
  List<Spec> getSpecs(C context);
}
