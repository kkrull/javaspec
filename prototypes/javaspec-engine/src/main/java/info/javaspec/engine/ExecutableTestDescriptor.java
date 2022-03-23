package info.javaspec.engine;

import org.junit.platform.engine.EngineExecutionListener;

//Adapter for JavaSpec containers or tests that runs on Jupiter.
interface ExecutableTestDescriptor {
	void execute(EngineExecutionListener listener);
}
