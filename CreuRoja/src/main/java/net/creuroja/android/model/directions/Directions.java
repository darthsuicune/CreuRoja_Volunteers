package net.creuroja.android.model.directions;


import com.google.android.gms.maps.model.LatLng;

import net.creuroja.android.model.directions.loader.DirectionsRequest;

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
		try {
			List<DirectionsRoute> routes =
					getRoutes(originLat, originLng, destinationLat, destinationLng);
			parseRoutes(routes);
		} catch (IOException | JSONException e) {
			e.printStackTrace();
			cantRetrieve(e);
		}
		return this;
	}

	public List<LatLng> points() {
		return this.points;
	}

	public int pointCount() {
		return points.size();
	}

	private List<DirectionsRoute> getRoutes(double originLat, double originLng,
											double destinationLat, double destinationLng) {
		DirectionsRequest request = new DirectionsRequest();
		return request.make(originLat, originLng, destinationLat, destinationLng);
	}

	private void parseRoutes(List<DirectionsRoute> routes) throws IOException, JSONException {
		for (DirectionsRoute route : routes) {
			points.addAll(route.path());
		}
	}

	private void cantRetrieve(Exception e) {

	}
}
