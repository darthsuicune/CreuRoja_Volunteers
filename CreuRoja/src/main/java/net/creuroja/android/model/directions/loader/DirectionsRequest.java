package net.creuroja.android.model.directions.loader;

import net.creuroja.android.model.directions.DirectionsRoute;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lapuente on 02.01.15.
 */
public class DirectionsRequest {
	public static final String DIRECTIONS_API_BASE_URL =
			"https://maps.googleapis.com/maps/api/directions/json?region=es&";
	public static final String ORIGIN_URL = "origin=";
	public static final String DESTINATION_URL = "destination=";
	public static final String SENSOR_URL = "sensor=";
	public static final int STATUS_OK = 0;
	public static final int STATUS_NOT_OK = 1;
	public static final int STATUS_LIMIT_REACHED = 2;
	public static final String STATUS = "status";
	public static final String ROUTES = "routes";
	public static final String LEGS = "legs";
	public static final String STEPS = "steps";
	public static final String START_LOCATION = "start_location";
	public static final String END_LOCATION = "end_location";
	public static final String POLYLINE = "polyline";
	public static final String POINTS = "points";

	public String status;
	public List<DirectionsRoute> routes;

	public DirectionsRequest() {

	}

	public List<DirectionsRoute> make(double originLat, double originLng,
									  double destinationLat, double destinationLng) {
		List<DirectionsRoute> routes = new ArrayList<>();
		return routes;
	}
}
