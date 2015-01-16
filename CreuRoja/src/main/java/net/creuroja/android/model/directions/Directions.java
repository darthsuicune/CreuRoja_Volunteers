package net.creuroja.android.model.directions;


import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lapuente on 30.09.14.
 */
public class Directions {
    LatLng origin;
    LatLng destination;
	private List<LatLng> points;
	private List<DirectionsRoute> routes;

	public Directions(LatLng origin, LatLng destination) {
        points = new ArrayList<>();
        this.origin = origin;
        this.destination = destination;
    }

	public Directions get() {
		getRoutes(origin.latitude, origin.longitude, destination.latitude, destination.longitude);
		createPointList();
		return this;
	}

	public List<LatLng> points() {
		return this.points;
	}

	private void createPointList() {
		if(!routes.isEmpty()) {
			points.addAll(routes.get(0).path());
		}
	}

	public int pointCount() {
		return points.size();
	}

	private void getRoutes(double originLat, double originLng, double destinationLat,
						   double destinationLng) {
		try {
			DirectionsRequest request = new DirectionsRequest();
			routes = request.make(originLat, originLng, destinationLat, destinationLng);
		} catch (DirectionsException e) {
			e.printStackTrace();
			routes = new ArrayList<>();
		}
	}

	public boolean areValid() {
		return pointCount() > 0;
	}
}
