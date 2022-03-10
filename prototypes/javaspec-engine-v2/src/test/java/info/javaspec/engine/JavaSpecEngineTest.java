package info.javaspec.engine;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.platform.testkit.engine.EngineTestKit;

@Nested
public class JavaSpecEngineTest {
	@Test
	@DisplayName("runs as a TestEngine")
	public void instantiates() throws Exception {
		new JavaSpecEngine();
		EngineTestKit.engine("javaspec-engine-v2").execute();
	}
}
