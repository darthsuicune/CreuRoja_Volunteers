package net.creuroja.android.model.directions;

import com.google.android.gms.maps.model.LatLng;

import junit.framework.TestCase;

public class DirectionsRequestTest extends TestCase {
    private LatLng origin;
    private LatLng destination;
    DirectionsRequest request;

	public void setUp() throws Exception {
        super.setUp();
        origin = new LatLng(0.0, 0.0);
        destination = new LatLng(1.0, 1.0);
        request = new DirectionsRequest(origin, destination);

	}

	public void testMake() throws Exception {
		assertNotNull(directions);
	}
}