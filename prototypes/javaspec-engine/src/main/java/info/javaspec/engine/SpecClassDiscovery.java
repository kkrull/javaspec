package info.javaspec.engine;

import info.javaspec.api.SpecClass;
import java.lang.reflect.Constructor;
import java.util.Optional;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;

// Discovers specs declared in a SpecClass, converting them to a TestDescriptor that Jupiter can recognize.
final class SpecClassDiscovery {
	private final Class<?> selectedClass;

	public SpecClassDiscovery(Class<?> selectedClass) {
		this.selectedClass = selectedClass;
	}

	public Optional<TestDescriptor> discover(UniqueId engineId) {
		return Optional.of(this.selectedClass)
			.filter(SpecClass.class::isAssignableFrom)
			.map(this::instantiate)
			.map(SpecClass.class::cast)
			.map(declaringInstance ->
			{
				SpecClassDescriptor specClassDescriptor = SpecClassDescriptor.of(engineId, declaringInstance);
				specClassDescriptor.discover();
				return specClassDescriptor;
			});
	}

	private Object instantiate(Class<?> clazz) {
		try {
			Constructor<?> constructor = clazz.getDeclaredConstructor();
			return constructor.newInstance();
		} catch (Exception e) {
			throw new RuntimeException("Failed to instantiate spec class", e);
		}
	}
}
