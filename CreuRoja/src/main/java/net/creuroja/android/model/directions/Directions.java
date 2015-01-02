package net.creuroja.android.model.directions;


import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lapuente on 30.09.14.
 */
public class Directions {
	private List<LatLng> points;

	public Directions() {
		points = new ArrayList<>();
	}

	public Directions get(double originLat, double originLng, double destinationLat,
							double destinationLng) {
		String origin = originLat + "," + originLng;
		String destination = destinationLat + "," + destinationLng;
		try {
			List<DirectionsRoute> routes = getRoutes(origin, destination);
			parseRoutes(routes);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this;
	}

	public List<LatLng> points() {
		return this.points;
	}

	public int pointCount() {
		return points.size();
	}

	private List<DirectionsRoute> getRoutes(String origin, String destination) {
		List<DirectionsRoute> routes = new ArrayList<>();
		try {
			
		} catch (IOException | JSONException e) {

		}
		return routes;
	}

	private void parseRoutes(List<DirectionsRoute> routes) {
		for (DirectionsRoute route : routes) {
			for (DirectionsLeg leg : route.legs) {
				addLeg(leg);
			}
		}
	}

	private void addLeg(DirectionsLeg leg) {
		points.add(new LatLng(leg.startLocation.latitude, leg.startLocation.longitude));
		for (DirectionsStep step : leg.steps) {
			addStep(step);
		}
		points.add(new LatLng(leg.endLocation.latitude, leg.endLocation.longitude));

	}

	private void addStep(DirectionsStep step) {
		points.add(new LatLng(step.startLocation.latitude, step.startLocation.longitude));
		for(LatLng latlng : step.decodePath()) {
			points.add(new LatLng(latlng.latitude, latlng.longitude));
		}
		for (DirectionsStep subStep : step.subSteps) {
			addStep(subStep);
		}
		points.add(new LatLng(step.endLocation.latitude, step.endLocation.longitude));

	}

}
