package net.creuroja.android.model.locations;

import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lapuente on 30.09.14.
 */
public class Directions {
	public static final String DIRECTIONS_API_BASE_URL =
			"https://maps.googleapis.com/maps/api/directions/json?region=es&";
	public static final String ORIGIN_URL = "origin=";
	public static final String DESTINATION_URL = "destination=";
	public static final String SENSOR_URL = "sensor=";

	public static final int STATUS_OK = 0;
	public static final int STATUS_NOT_OK = 1;
	public static final int STATUS_LIMIT_REACHED = 2;

	public static final String sStatus = "status";
	public static final String sRoutes = "routes";
	public static final String sOverviewPolyline = "overview_polyline";
	public static final String sPoints = "points";

	public String polyline;
	private List<LatLng> points;

	public Directions(String polyline) {
		this.polyline = polyline;
		if (!polyline.equals("")) {
			points = decodePoly(polyline);
		}
	}

	public Directions(HttpResponse response) throws JSONException {
		if (response.getStatusLine() != null) {
			switch (response.getStatusLine().getStatusCode()) {
				case 200:
					// Get everything as a string ready to parse
					BufferedReader reader;
					try {
						reader = new BufferedReader(
								new InputStreamReader(response.getEntity().getContent()));
						StringBuilder builder = new StringBuilder();
						String line;
						while ((line = reader.readLine()) != null) {
							builder.append(line);
						}
						reader.close();
						parseResponse(builder.toString());
					} catch (IllegalStateException | IOException e) {
						e.printStackTrace();
					}
					break;
				default:
					break;
			}
		}
	}

	public static Directions get(double originLat, double originLng, double destinationLat,
								 double destinationLng) throws IOException, JSONException {
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(
				DIRECTIONS_API_BASE_URL + ORIGIN_URL + originLat + "," + originLng + "&" +
				DESTINATION_URL + destinationLat + "," + destinationLng + "&" +
				SENSOR_URL + true);

		return new Directions(client.execute(request));
	}

	private static int parseStatus(String status) {
		switch (status) {
			case "OK":
			case "ZERO_RESULTS":
				return STATUS_OK;
			case "OVER_QUERY_LIMIT":
				return STATUS_LIMIT_REACHED;
			default:
				return STATUS_NOT_OK;
		}
	}

	/**
	 * This method was completely copied from
	 * http://stackoverflow.com/questions/15924834/decoding-polyline-with-new-google-maps-api
	 * It converts the List<LatLng> encoded in the response from Google into an array of points.
	 *
	 * @param encoded encoded string with the polyline to decode
	 * @return a list with all the lines from the encoded polyline
	 */
	private static List<LatLng> decodePoly(String encoded) {
		List<LatLng> poly = new ArrayList<>();
		int index = 0, len = encoded.length();
		int lat = 0, lng = 0;
		while (index < len) {
			int b, shift = 0, result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lat += dlat;
			shift = 0;
			result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lng += dlng;

			LatLng position = new LatLng((double) lat / 1E5, (double) lng / 1E5);
			poly.add(position);
		}
		return poly;
	}

	public List<LatLng> getPoints() {
		return points;
	}

	private void parseResponse(String jsonResponse) throws JSONException {
		JSONObject response = new JSONObject(jsonResponse);
		points = new ArrayList<>();
		int status = parseStatus(response.getString(sStatus));
		if (status == STATUS_OK) {
			JSONArray routes = response.getJSONArray(sRoutes);
			for (int i = 0; i < routes.length(); i++) {
				JSONObject route = routes.getJSONObject(i);
				points.addAll(
						decodePoly(route.getJSONObject(sOverviewPolyline).getString(sPoints)));
			}
		}
	}
}
