package net.creuroja.android.model.locations;


import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;

import net.creuroja.android.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lapuente on 30.09.14.
 */
public class Directions {
	GeoApiContext apiContext;
	private List<LatLng> points;

	public Directions(Context context) {
		String apiKey = context.getString(R.string.api_key);
		apiContext = new GeoApiContext().setApiKey(apiKey);
		points = new ArrayList<>();
	}

	public Directions get(double originLat, double originLng, double destinationLat,
							double destinationLng) {
		String origin = originLat + "," + originLng;
		String destination = destinationLat + "," + destinationLng;
		DirectionsApiRequest request = DirectionsApi.getDirections(apiContext, origin, destination);
		try {
			takeRoute(request.await());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this;
	}

	private void takeRoute(DirectionsRoute[] directionsRoutes) {
		for (DirectionsRoute route : directionsRoutes) {
			for (DirectionsLeg leg : route.legs) {
				addLeg(leg);
			}
		}
	}

	private void addLeg(DirectionsLeg leg) {
		points.add(new LatLng(leg.startLocation.lat, leg.startLocation.lng));
		for (DirectionsStep step : leg.steps) {
			addStep(step);
		}
		points.add(new LatLng(leg.endLocation.lat, leg.endLocation.lng));

	}

	private void addStep(DirectionsStep step) {
		points.add(new LatLng(step.startLocation.lat, step.startLocation.lng));
		for (DirectionsStep subStep : step.subSteps) {
			addStep(subStep);
		}
		points.add(new LatLng(step.endLocation.lat, step.endLocation.lng));

	}

	public List<LatLng> points() {
		return this.points;
	}

	public int pointCount() {
		return points.size();
	}
}
