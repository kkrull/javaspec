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

import org.junit.platform.engine.EngineDiscoveryRequest;

/**
 * A listener for what the JUnit Platform passes to {@link JavaSpecEngine} to
 * configure the process of test discovery. Providing an implementation at
 * runtime can help in debugging, by clarifying exactly what the platform is
 * passing to the engine.
 *
 * <p>
 * <strong>Note: This only works with JavaSpecEngine. It does not work with
 * other JUnit TestEngines.</strong>
 * </p>
 */
public interface EngineDiscoveryRequestListener {
	/**
	 * Receives an EngineDiscoveryRequest, upon
	 * {@link JavaSpecEngine#discover(EngineDiscoveryRequest, UniqueId)}.
	 *
	 * @param request The discovery request received from the JUnit Platform
	 */
	void onDiscover(EngineDiscoveryRequest request);
}
