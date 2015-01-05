package net.creuroja.android.model.directions;

import junit.framework.TestCase;

public class DirectionsRequestTest extends TestCase {
	Directions directions;

	public void setUp() throws Exception {
		super.setUp();
		directions = new Directions();

	}

	public void testMake() throws Exception {
		assertNotNull(directions);
	}
}