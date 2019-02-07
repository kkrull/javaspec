package info.javaspec.lang.lambda;

import info.javaspec.Spec;
import info.javaspec.SequentialSuite;
import info.javaspec.Suite;

import java.util.Optional;
import java.util.Stack;

import static info.javaspec.lang.lambda.Exceptions.*;

/** Groups recently-declared specs into a suite of specs that can be run together */
final class SpecDeclaration {
  private static SpecDeclaration _instance;
  private final Stack<SequentialSuite> declarationSuites;

  /* Singleton */

  public static void beginDeclaration() {
    if(_instance != null)
      throw new DeclarationAlreadyStarted();

    _instance = new SpecDeclaration();
  }

  public static Suite endDeclaration() {
    Suite suite = _instance.declarationSuites.pop();
    if(!_instance.declarationSuites.isEmpty())
      throw new IllegalStateException("Spec declaration ended prematurely");

    _instance = null;
    return suite;
  }

  public static SpecDeclaration getInstance() {
    return Optional.ofNullable(_instance)
      .orElseThrow(DeclarationNotStarted::new);
  }

  static void reset() {
    _instance = null;
  }

  /* Instance */

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
      .orElseThrow(() -> NoSubjectDefined.forSpec(intendedBehavior))
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
}
