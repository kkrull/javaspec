package info.javaspec.jupiter.syntax.declarationparameter;

import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.function.Executable;

import java.util.LinkedList;
import java.util.List;

final class JavaSpec {
	public static DynamicNode describe(String what, DescribeBlock block) {
		List<DynamicNode> nodes = new LinkedList<>();

		block.declareSpecs(new SpecDeclaration() {
			@Override
			public void describe(String innerWhat, DescribeBlock innerBlock) {
				DynamicNode container = JavaSpec.describe(innerWhat, innerBlock);
				nodes.add(container);
			}

			@Override
			public void it(String behavior, Executable verification) {
				DynamicTest test = DynamicTest.dynamicTest(behavior, verification);
				nodes.add(test);
			}
		});

		return DynamicContainer.dynamicContainer(what, nodes);
	}

	@FunctionalInterface
	public interface DescribeBlock {
		void declareSpecs(SpecDeclaration declaration);
	}

	public interface SpecDeclaration {
		void describe(String what, DescribeBlock block);
		void it(String behavior, Executable verification);
	}
}
