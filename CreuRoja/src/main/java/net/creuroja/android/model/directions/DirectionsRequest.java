package net.creuroja.android.model.directions;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
		HttpResponse response;
		try {
			HttpClient client = new DefaultHttpClient();
			response = client.execute(new HttpGet(url));
		} catch (IOException e) {
			throw new DirectionsException(e);
		}
		return new DirectionsResponse(read(response));
	}

	private String read(HttpResponse response) {
		StringBuilder builder;
		try {
			BufferedReader reader =
					new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
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
