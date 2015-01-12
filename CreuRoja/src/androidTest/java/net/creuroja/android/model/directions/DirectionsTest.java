package net.creuroja.android.model.directions;


import junit.framework.TestCase;

public class DirectionsTest extends TestCase {
	Directions directions;

	public void setUp() throws Exception {
		directions = new Directions();
	}

	public void testGet() throws Exception {
		whenWeAskForValidDirections();
		expectTheObjectToBePopulated();
	}

	public void testInvalid() throws Exception {
		try {
			whenWeAskForInvalidDirections();
		} catch (DirectionsException e) {
			expectAValidException(e);
		}
	}

	private void whenWeAskForValidDirections() {
		directions.get(35.4675602, -97.5164276, 34.0522342, -118.2436849);
	}

	private void whenWeAskForInvalidDirections() {
		directions.get(0.0, 0.0, 1.0, 1.0);
	}

	private void expectTheObjectToBePopulated() {
		assertNotNull(directions.points());
		assertFalse(directions.points().isEmpty());
	}

	private void expectAValidException(DirectionsException e) {
		assertNotNull(e);
	}
}