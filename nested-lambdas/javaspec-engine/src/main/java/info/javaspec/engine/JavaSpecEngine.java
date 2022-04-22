/**
 * MIT License
 *
 * Copyright (c) 2014–2022 Kyle Krull
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package info.javaspec.engine;

import info.javaspec.api.SpecClass;
import java.util.Optional;
import java.util.ServiceLoader;
import org.junit.platform.engine.*;
import org.junit.platform.engine.discovery.ClassSelector;

/**
 * Orchestrates the process of discovering and running specs on the JUnit
 * Platform. Add the artifact containing this class to the runtime classpath, so
 * that the JUnit Platform can find and execute specs using this engine.
 *
 * <h2>Use with JUnit Platform Console</h2>
 *
 * Users of the JUnit Platform Console can include the engine by adding
 * classpaths for the JavaSpec API and this TestEngine, as in this script
 * example:
 *
 * <pre>
 * {@code
 * junit_console_jar='junit-platform-console-standalone-1.8.1.jar'
 * java -jar "$junit_console_jar" \
 *   --classpath=info.javaspec.javaspec-api-0.0.1.jar \
 *   --classpath=<compiled production code and its dependencies> \
 *   --classpath=<compiled specs and their dependencies> \
 *   --classpath=info.javaspec.javaspec-engine-0.0.1.jar \
 *   --include-engine=javaspec-engine \
 *   ...
 * }
 * </pre>
 *
 * <h2>Use with Gradle</h2>
 *
 * Users who are already using Gradle need to add some dependencies and tell
 * Gradle to use the JUnit Platform for running tests:
 *
 * <pre>
 * {@code
 * //build.gradle
 * plugins {
 *   id 'java' //or one of the other Java plugins like 'java-library'
 * }
 *
 * dependencies {
 *   //Add these dependencies for JavaSpec
 *   testImplementation 'info.javaspec:javaspec-api:<version>'
 *   testRuntimeOnly 'info.javaspec:javaspec-engine:<version>'
 *
 *   //Add an assertion library (JUnit 5's assertions shown here)
 *   testImplementation 'org.junit.jupiter:junit-jupiter-api:5.8.2'
 * }
 *
 * test {
 *   useJUnitPlatform()
 * }
 * }
 * </pre>
 */
public class JavaSpecEngine implements TestEngine {
	private final EngineDiscoveryRequestListenerProvider loader;

	/**
	 * Default constructor called by the JUnit Platform. Configures itself to use
	 * {@link ServiceLoader} to find an {@link EngineDiscoveryRequestListener}.
	 */
	public JavaSpecEngine() {
		this.loader = () -> ServiceLoader
			.load(EngineDiscoveryRequestListener.class)
			.findFirst();
	}

	JavaSpecEngine(EngineDiscoveryRequestListenerProvider loader) {
		this.loader = loader;
	}

	/**
	 * Discovers specs in any instances of {@link SpecClass} that are identified by
	 * each given {@link ClassSelector}. This causes JavaSpec syntax to be converted
	 * into its JUnit counterparts:
	 * <ul>
	 * <li>JUnit test containers for each {@code SpecClass}</li>
	 * <li>Nested JUnit test containers for context blocks like
	 * {@link info.javaspec.api.JavaSpec#describe} and
	 * {@link info.javaspec.api.JavaSpec#given}</li>
	 * <li>JUnit tests for specs declared with
	 * {@link info.javaspec.api.JavaSpec#it}</li>
	 * </ul>
	 *
	 * <strong>Note: The JUnit Platform automatically prunes tests containers that
	 * contain no tests.</strong> If a container is missing in the test output, it
	 * may be due to not having declared any specs inside of it.
	 *
	 * <h2>Debugging</h2>
	 *
	 * Users may optionally provide a {@link EngineDiscoveryRequestListener} through
	 * the {@link ServiceLoader} mechanism. When present, this will pass the given
	 * {@link EngineDiscoveryRequest} to the listener for debugging purposes.
	 *
	 * <h2>Gradle note</h2>
	 *
	 * Gradle tends to select all classes in the test source set, even those that do
	 * not end in {@code Spec} or {@code Test}. This engine therefore ignores any
	 * selected class that is not a {@link SpecClass}.
	 *
	 * @return a {@link TestDescriptor} containing 0..n selected specs, which this
	 *         engine can then execute later.
	 */
	@Override
	public TestDescriptor discover(EngineDiscoveryRequest discoveryRequest, UniqueId engineId) {
		this.loader.findFirst()
			.ifPresent(listener -> listener.onDiscover(discoveryRequest));

		ExecutableTestDescriptor engineDescriptor = ContextDescriptor.forEngine(engineId);
		discoveryRequest.getSelectorsByType(ClassSelector.class)
			.stream()
			.map(ClassSelector::getJavaClass)
			.map(selectedClass -> new SpecClassDeclaration(selectedClass))
			.map(declaration -> declaration.run(engineId))
			.filter(Optional::isPresent)
			.map(Optional::orElseThrow)
			.forEach(engineDescriptor::addChild);

		return engineDescriptor;
	}

	/**
	 * Runs the specs that this engine discovered.
	 *
	 * @param request Contains the root {@link TestDescriptor} returned from
	 *                {@link #discover(EngineDiscoveryRequest, UniqueId)}, during
	 *                the discovery process.
	 */
	@Override
	public void execute(ExecutionRequest request) {
		ExecutableTestDescriptor engineDescriptor = ExecutableTestDescriptor.class.cast(request.getRootTestDescriptor());
		engineDescriptor.execute(request.getEngineExecutionListener());
	}

	/**
	 * @return {@code "javaspec-engine"}. <strong>This is the string you will need
	 *         to provide to the {@code includeEngine} option</strong>, when running
	 *         specs under the JUnit Platform.
	 */
	@Override
	public String getId() {
		return "javaspec-engine";
	}
}
