package net.creuroja.android.model.directions;

import net.creuroja.android.model.webservice.util.ResponseFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


public class DirectionsRequest {
	public static final String DIRECTIONS_API_BASE_URL =
			"https://maps.googleapis.com/maps/api/directions/json?region=es&";
	public static final String ORIGIN_URL = "origin=";
	public static final String DESTINATION_URL = "destination=";

	private List<DirectionsRoute> routes;

	HttpURLConnection connection;

	public DirectionsRequest() {

	}

	public List<DirectionsRoute> make(double originLat, double originLng, double destinationLat,
									  double destinationLng) {

		try {
			buildConnectionUrl(originLat, originLng, destinationLat, destinationLng);
			DirectionsResponse response = connect();
			routes = response.routes();
		} catch (IOException e) {
			throw new DirectionsException(e);
		}
		return routes;
	}

	public List<DirectionsRoute> routes() {
		return routes;
	}

	private DirectionsResponse connect() throws IOException {
		connection.connect();
		return new DirectionsResponse(ResponseFactory.asString(connection.getInputStream()));
	}

	private void buildConnectionUrl(double originLat, double originLng, double destinationLat,
									double destinationLng) throws IOException {
		connection = (HttpURLConnection) new URL(
				DIRECTIONS_API_BASE_URL + ORIGIN_URL + originLat + "," + originLng + "&" +
				DESTINATION_URL + destinationLat + "," + destinationLng).openConnection();
	}
}
