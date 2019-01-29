package info.javaspec.lang.lambda;

import info.javaspec.Spec;
import info.javaspec.SequentialSuite;
import info.javaspec.Suite;

import java.util.Optional;
import java.util.Stack;

/** Groups recently-declared specs into a suite of specs that can be run together */
final class SpecDeclaration {
  private static SpecDeclaration _instance;
  private final Stack<SequentialSuite> currentSuite;

  public static SpecDeclaration getInstance() {
    return _instance;
  }

  public static void beginDeclaration() {
    if(_instance != null)
      throw new IllegalStateException("Specs are already being declared");

    _instance = new SpecDeclaration();
    SequentialSuite rootSuite = new SequentialSuite();
    _instance.currentSuite.push(rootSuite); //TODO KDK: Stop pushing a root suite onto the stack?  Need a way to tell when the root suite is the only one left.  Maybe look at the type of the suite, or have RootSuite#addSpec throw NoSubjectDefinedError instead of doing it in here.
  }

  public static Suite endDeclaration() {
    Suite suite = _instance.currentSuite.pop();
    if(!_instance.currentSuite.isEmpty())
      throw new IllegalStateException("Spec declaration ended prematurely");

    _instance = null;
    return suite;
  }

  public SpecDeclaration() {
    this.currentSuite = new Stack<>();
  }

  public void declareSpecsFor(String subject, BehaviorDeclaration describeBehavior) {
    SequentialSuite subjectSuite = new SequentialSuite(subject);
    currentSuite().addChildSuite(subjectSuite); //Add the child suite in line with any other declared specs
    this.currentSuite.push(subjectSuite); //Push on to the stack in case there are nested describes
    describeBehavior.declareSpecs();
    this.currentSuite.pop();
  }

  public void createSpec(String intendedBehavior, BehaviorVerification verification) {
    Spec spec = new DescriptiveSpec(intendedBehavior, verification);
    maybeCurrentSuite()
      .orElseThrow(() -> NoSubjectDefinedException.forSpec(intendedBehavior))
      .addSpec(spec);
  }

  private Optional<SequentialSuite> maybeCurrentSuite() {
    return this.currentSuite.empty()
      ? Optional.empty()
      : Optional.of(this.currentSuite.peek());
  }

  private SequentialSuite currentSuite() {
    return this.currentSuite.peek();
  }

  static class NoSubjectDefinedException extends RuntimeException {
    static NoSubjectDefinedException forSpec(String intendedBehavior) {
      String message = String.format("No subject defined for spec: %s", intendedBehavior);
      return new NoSubjectDefinedException(message);
    }

    private NoSubjectDefinedException(String message) {
      super(message);
    }
  }
}
