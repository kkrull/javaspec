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

import static info.javaspec.engine.ConfigurationParametersFactory.nullConfigurationParameters;
import static java.util.stream.Collectors.toList;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.junit.platform.engine.ConfigurationParameters;
import org.junit.platform.engine.DiscoveryFilter;
import org.junit.platform.engine.DiscoverySelector;
import org.junit.platform.engine.EngineDiscoveryRequest;
import org.junit.platform.engine.discovery.ClassSelector;
import org.junit.platform.engine.discovery.DiscoverySelectors;

//Test data factory for different kinds of EngineDiscoveryRequest.
public final class EngineDiscoveryRequestFactory {
	private EngineDiscoveryRequestFactory() { /* static class */ }

	public static EngineDiscoveryRequest classEngineDiscoveryRequest(Class<?> specClass) {
		return new ClassEngineDiscoveryRequest(specClass);
	}

	public static EngineDiscoveryRequest nullEngineDiscoveryRequest() {
		return new NullEngineDiscoveryRequest();
	}

	private static final class ClassEngineDiscoveryRequest implements EngineDiscoveryRequest {
		private final List<DiscoverySelector> selectors;

		public ClassEngineDiscoveryRequest(Class<?> specClass) {
			this.selectors = new LinkedList<>();
			this.selectors.add(DiscoverySelectors.selectClass(specClass));
		}

		@Override
		public ConfigurationParameters getConfigurationParameters() {
			return nullConfigurationParameters();
		}

		@Override
		public <T extends DiscoveryFilter<?>> List<T> getFiltersByType(Class<T> filterType) {
			return Collections.emptyList();
		}

		@Override
		public <T extends DiscoverySelector> List<T> getSelectorsByType(Class<T> selectorType) {
			if (!ClassSelector.class.equals(selectorType)) {
				return Collections.emptyList();
			}

			return this.selectors.stream()
				.map(selectorType::cast)
				.collect(toList());
		}
	}

	private static final class NullEngineDiscoveryRequest implements EngineDiscoveryRequest {
		@Override
		public ConfigurationParameters getConfigurationParameters() {
			return nullConfigurationParameters();
		}

		@Override
		public <T extends DiscoveryFilter<?>> List<T> getFiltersByType(Class<T> filterType) {
			return Collections.emptyList();
		}

		@Override
		public <T extends DiscoverySelector> List<T> getSelectorsByType(Class<T> selectorType) {
			return Collections.emptyList();
		}
	}
}
