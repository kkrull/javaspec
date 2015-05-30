package info.javaspec.runner.ng;

import java.util.List;

/** Provides access to specs that are organized into a hierarchy of context. */
interface SpecGateway {
  Context rootContext();
  String rootContextId();
  List<Context> getSubcontexts(Context context);

  boolean hasSpecs();
  long countSpecs();
  List<Spec> getSpecs(Context context);
}
