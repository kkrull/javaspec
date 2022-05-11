
import info.javaspec.api.JavaSpec;
import info.javaspec.api.SpecClass;
import org.junit.platform.commons.annotation.Testable;

@Testable
public class MinimaxSpecs implements SpecClass {
	@Override
	public void declareSpecs(JavaSpec javaspec) {
		javaspec.pending("tries out JavaSpec");
	}
}
