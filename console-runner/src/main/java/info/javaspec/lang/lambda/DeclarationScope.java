package info.javaspec.lang.lambda;

import info.javaspec.SequentialSuite;
import info.javaspec.Spec;
import info.javaspec.Suite;

import java.util.Optional;
import java.util.Stack;

/** Groups recently-declared specs into a suite of specs that can be run together */
final class DeclarationScope {
  private final Stack<SequentialSuite> declarationSuites;

  public DeclarationScope() {
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
      .orElseThrow(() -> Exceptions.NoSubjectDefined.forSpec(intendedBehavior))
      .addSpec(spec);
  }

  public Suite completeSuite() {
    Suite suite = this.declarationSuites.pop();
    if(!this.declarationSuites.isEmpty())
      throw new IllegalStateException("Spec declaration ended prematurely");

    return suite;
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
}
