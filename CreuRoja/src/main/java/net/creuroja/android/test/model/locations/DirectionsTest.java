package net.creuroja.android.test.model.locations;

import android.test.AndroidTestCase;

import net.creuroja.android.model.locations.Directions;

import org.junit.Before;
import org.junit.Test;

public class DirectionsTest extends AndroidTestCase {
	Directions directions;

	@Before
	public void setUp() throws Exception {
		directions = new Directions(getContext());
	}

	@Test
	public void testGet() throws Exception {
		assertNotNull(directions);
		directions.get(0.0, 0.0, 0.0, 0.0);
	}
}