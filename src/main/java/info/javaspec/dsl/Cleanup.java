package info.javaspec.dsl;

/**
 * A thunk that executes after the test to clean up whatever side effects may have been created by the test fixture
 * or (gasp!) the production code itself.
 * <p>
 * For example if you create a File in an Establish step so that your production code has something to read during the
 * long test, be a good citizen and clean up after yourself by writing one of these functions.
 * Otherwise some poor, unsuspecting test down the line may fail for no apparent reason.  It will also keep your fellow
 * developers from having to wonder where all those extra files came from or why their database just got dropped.
 * <p>
 * <span><strong>This step runs any time any part of a test or its fixture has run.</strong></span>
 * <p>
 * As such, your Cleanup function has to be written with handling an incomplete execution in mind.  For example, check
 * that the File you intended to create or the transaction you thought you started earlier actually exists before you
 * try deleting and rolling back.
 * <p>
 * Why is this necessary?  Well, JavaSpec has no way of telling that you were only checking your watch and haven't
 * created that File yet.  It tries to give you the opportunity to keep state from leaking out of your test, in case you
 * did do something to the environment that you don't want to last into the next test.
 */
@FunctionalInterface
public interface Cleanup {
  void run() throws Exception;
}