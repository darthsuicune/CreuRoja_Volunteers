package net.creuroja.android.model.directions;


import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lapuente on 30.09.14.
 */
public class Directions {
	private List<LatLng> points;
	private List<DirectionsRoute> routes;

	public Directions() {
		points = new ArrayList<>();
	}

	public Directions get(double originLat, double originLng, double destinationLat,
						  double destinationLng) {
		getRoutes(originLat, originLng, destinationLat, destinationLng);
		createPointList();
		return this;
	}

	public List<LatLng> points() {
		return this.points;
	}

	private void createPointList() {
		points.addAll(routes.get(0).path());
	}

	public int pointCount() {
		return points.size();
	}

	private void getRoutes(double originLat, double originLng, double destinationLat,
						   double destinationLng) {
		DirectionsRequest request = new DirectionsRequest();
		routes = request.make(originLat, originLng, destinationLat, destinationLng);
	}
}
