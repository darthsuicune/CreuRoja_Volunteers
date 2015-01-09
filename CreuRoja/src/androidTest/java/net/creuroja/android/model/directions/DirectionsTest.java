package net.creuroja.android.model.directions;


import com.google.android.gms.maps.model.LatLng;

import junit.framework.TestCase;

public class DirectionsTest extends TestCase {
    private LatLng origin;
    private LatLng destination;
	Directions directions;

	public void setUp() throws Exception {
		origin = new LatLng(0.0, 0.0);
        destination = new LatLng(1.0, 1.0);
        directions = new Directions(origin, destination);
	}

	public void testGet() throws Exception {
        withDirections(origin, destination);
        itShouldContainAFewSteps();
	}

    private void withDirections(LatLng origin, LatLng destination) {

    }

    private void itShouldContainAFewSteps() {

    }


}