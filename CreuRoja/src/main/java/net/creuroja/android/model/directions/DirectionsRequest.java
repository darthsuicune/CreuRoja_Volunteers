package net.creuroja.android.model.directions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by lapuente on 02.01.15.
 */
public class DirectionsRequest {
	public static final String DIRECTIONS_API_BASE_URL =
			"https://maps.googleapis.com/maps/api/directions/json?region=es&";
	public static final String ORIGIN_URL = "origin=";
	public static final String DESTINATION_URL = "destination=";

	private List<DirectionsRoute> routes;

	private String url;

	public DirectionsRequest() {

	}

	public List<DirectionsRoute> make(double originLat, double originLng, double destinationLat,
									  double destinationLng) {

		buildConnectionUrl(originLat, originLng, destinationLat, destinationLng);
		DirectionsResponse response = connect();
		routes = response.routes();
		return routes;
	}

	public List<DirectionsRoute> routes() {
		return routes;
	}

	private DirectionsResponse connect() {
		HttpURLConnection connection;
		try {
			URL url = new URL(this.url);
			connection = (HttpURLConnection) url.openConnection();
			connection.connect();
			return new DirectionsResponse(read(connection.getInputStream()));
		} catch (IOException e) {
			throw new DirectionsException(e);
		}
	}

	private String read(InputStream stream) {
		StringBuilder builder;
		try {
			BufferedReader reader =
					new BufferedReader(new InputStreamReader(stream));
			builder = new StringBuilder();
			String line;
			for(line = reader.readLine(); line != null; line = reader.readLine()) {
				builder.append(line);
			}

		} catch (IOException e) {
			throw new DirectionsException(e);
		}
		return builder.toString();
	}

	private void buildConnectionUrl(double originLat, double originLng, double destinationLat,
									double destinationLng) {
		url = DIRECTIONS_API_BASE_URL + ORIGIN_URL + originLat + "," + originLng + "&" +
			  DESTINATION_URL + destinationLat + "," + destinationLng;
	}
}
