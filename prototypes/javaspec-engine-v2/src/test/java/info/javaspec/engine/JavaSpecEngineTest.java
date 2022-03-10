package info.javaspec.engine;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.platform.engine.TestEngine;
import org.junit.platform.testkit.engine.EngineTestKit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Nested
public class JavaSpecEngineTest {
	@Test
	@DisplayName("instantiates")
	public void instantiates() throws Exception {
		new JavaSpecEngine();
	}

	@Test
	@DisplayName("returns an unique ID")
	public void identifiesItselfAsTheEngineForJavaSpec() throws Exception {
	  TestEngine subject = new JavaSpecEngine();
		assertEquals("javaspec-engine-v2", subject.getId());
	}

	@Test
	@DisplayName("runs as a TestEngine")
	@Disabled
	public void runsAsATestEngine () throws Exception {
		EngineTestKit.engine(new JavaSpecEngine()).execute();
	}

	@Test
	@DisplayName("can be loaded with ServiceLoader and located by ID")
	@Disabled
	public void isRegisteredWithServiceLoader() throws Exception {
	}
}
