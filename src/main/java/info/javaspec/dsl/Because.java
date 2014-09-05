package info.javaspec.dsl;

/** A thunk that executes the Act step of a test.  Runs between <code>Establish</code> and <code>It</code>. */
@FunctionalInterface
public interface Because extends Before { }