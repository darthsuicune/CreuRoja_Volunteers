package net.creuroja.android.model.directions;

import junit.framework.TestCase;

/**
 * This test basically validates the Google Directions API
 */
public class DirectionsRequestTest extends TestCase {
	DirectionsRequest request;

	public void setUp() throws Exception {
		super.setUp();
		request = new DirectionsRequest();
	}

	public void testMake() throws Exception {
		try {
			whenWeMakeARequest();
			expectResponseToBeValid();
		} catch (DirectionsException e) {
			orThrowAnException(e);
		}
	}

	private void whenWeMakeARequest() {
		request.make(35.4675602, -97.5164276, 34.0522342, -118.2436849);
	}

	private void expectResponseToBeValid() {
		assertNotNull(request.routes());
		assertFalse(request.routes().isEmpty());
	}

	private void orThrowAnException(DirectionsException e) {
		assertNotNull(e);
	}
}