package info.javaspec.lang.lambda;

import info.javaspec.Spec;
import info.javaspec.SequentialSuite;
import info.javaspec.Suite;

import java.util.Optional;
import java.util.Stack;

/** Groups recently-declared specs into a suite of specs that can be run together */
final class SpecDeclaration {
  private static SpecDeclaration _instance;
  private final Stack<SequentialSuite> declarationSuites;

  public static SpecDeclaration getInstance() {
    return _instance;
  }

  public static void beginDeclaration() {
    if(_instance != null)
      throw new IllegalStateException("Specs are already being declared");

    _instance = new SpecDeclaration();
  }

  public static Suite endDeclaration() {
    Suite suite = _instance.declarationSuites.pop();
    if(!_instance.declarationSuites.isEmpty())
      throw new IllegalStateException("Spec declaration ended prematurely");

    _instance = null;
    return suite;
  }

  public SpecDeclaration() {
    this.declarationSuites = new Stack<>();
    this.declarationSuites.push(new RootSuite());
  }

  public void declareSpecsFor(String subject, BehaviorDeclaration describeBehavior) {
    SequentialSuite subjectSuite = new SequentialSuite(subject);
    currentSuite().get().addChildSuite(subjectSuite); //Add the child suite in line with any other declared specs
    this.declarationSuites.push(subjectSuite); //Push on to the stack in case there are nested describes
    describeBehavior.declareSpecs();
    this.declarationSuites.pop();
  }

  public void createSpec(String intendedBehavior, BehaviorVerification verification) {
    Spec spec = new DescriptiveSpec(intendedBehavior, verification);
    currentSubjectSuite()
      .orElseThrow(() -> NoSubjectDefinedException.forSpec(intendedBehavior))
      .addSpec(spec);
  }

  private Optional<SequentialSuite> currentSubjectSuite() {
    return this.currentSuite()
      .filter(x -> !RootSuite.class.equals(x.getClass()));
  }

  private Optional<SequentialSuite> currentSuite() {
    return this.declarationSuites.empty()
      ? Optional.empty()
      : Optional.of(this.declarationSuites.peek());
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
