package net.creuroja.android.model.directions;


import junit.framework.TestCase;

public class DirectionsTest extends TestCase {
	Directions directions;

	public void setUp() throws Exception {
		directions = new Directions();
	}

	public void testGet() throws Exception {
		directions.get(0.0, 0.0, 0.0, 0.0);
		assertNotNull(directions);
	}
}