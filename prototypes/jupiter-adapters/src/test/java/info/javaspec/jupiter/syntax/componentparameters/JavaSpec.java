package info.javaspec.jupiter.syntax.componentparameters;

import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.function.Executable;

import java.util.LinkedList;
import java.util.List;

final class JavaSpec {
	public static DynamicContainer describe(String what, LeafDeclaration block) {
		List<DynamicNode> nodes = new LinkedList<>();
		block.declare(new It() {
			@Override
			public void declare(String behavior, Executable verification) {
				DynamicTest test = DynamicTest.dynamicTest(behavior, verification);
				nodes.add(test);
			}
		});

		return DynamicContainer.dynamicContainer(what, nodes);
	}

	@FunctionalInterface
	public interface LeafDeclaration {
		void declare(It it);
	}

	@FunctionalInterface
	public interface It {
		void declare(String behavior, Executable verification);
	}
}
