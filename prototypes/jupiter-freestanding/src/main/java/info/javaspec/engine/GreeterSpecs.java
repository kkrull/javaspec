package info.javaspec.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.platform.commons.annotation.Testable;

/**
 * How to get IDE to run tests with my new custom TestEngine?
 *
 * Maybe it needs the @Testable annotation to get the run arrow and then these
 * something like these gradle settings. The first step on this might be to
 * extract SpecTestEngine, save artifacts locally, and register a custom test
 * engine (from another module or project). Then I could add the runtime
 * dependency on the engine and make sure it's included when running tests.
 *
 * See here:
 * https://stackoverflow.com/questions/45462987/junit5-with-intellij-and-gradle
 *
 * This issue with cucumber-IntelliJ integration may also have some related
 * information: https://github.com/cucumber/cucumber-jvm/issues/2348
 *
 * dependencies {
 *
 * testImplementation "org.junit.jupiter:junit-jupiter-params:$junitVersion"
 * testImplementation "org.junit.jupiter:junit-jupiter-api:$junitVersion"
 *
 * testRuntimeOnly "org.junit.vintage:junit-vintage-engine:$junitVersion"
 * testRuntimeOnly "org.junit.jupiter:junit-jupiter-engine:$junitVersion" }
 *
 * test { useJUnitPlatform { includeEngines 'junit-jupiter', 'junit-vintage' } }
 */

@Testable
public class GreeterSpecs implements SpecClass {
	private static int _numTimesRun = 0;

	public static void assertRanOnce() {
		assertEquals(1, _numTimesRun, "Expected GreeterSpecs to have been run once");
	}

	public static void incrementRunCount() {
		_numTimesRun++;
	}

	public GreeterSpecs() {
		System.out.println("[GreeterSpecs::GreeterSpecs]");
	}

	@Override
	public JupiterSpecContainer declareSpecs() {
		JupiterSpecContainer container = new JupiterSpecContainer(GreeterSpecs.class);
		container.addSpec(new JupiterSpec("greets the world", () -> GreeterSpecs.incrementRunCount()));
		return container;
	}
}
