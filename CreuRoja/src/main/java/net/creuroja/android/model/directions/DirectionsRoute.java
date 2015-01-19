package net.creuroja.android.model.directions;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lapuente on 02.01.15.
 */
public class DirectionsRoute {
	public static final String LEGS = "legs";
	public static final String COPYRIGHT = "copyrights";
	public static final String OVERVIEW_POLYLINE = "overview_polyline";
	public static final String POINTS = "points";
	public static final String WARNINGS = "warnings";
	public static final String WAYPOINT_ORDER = "waypoint_order";
	public static final String BOUNDS = "bounds";
	public static final String SOUTHWEST = "southwest";
	public static final String NORTHEAST = "northeast";
	public static final String LAT = "lat";
	public static final String LNG = "lng";
	public final String copyright;
	public final String overviewPolyline;
	public final LatLng northEastBound;
	public final LatLng southWestBound;
	public List<DirectionsLeg> legs;
	public List<String> warnings;
	public int[] waypointOrder;

	public DirectionsRoute(JSONObject route) {
		try {
			legs = new ArrayList<>();
			warnings = new ArrayList<>();
			readLegs(route);
			copyright = route.getString(COPYRIGHT);
			overviewPolyline = route.getJSONObject(OVERVIEW_POLYLINE).getString(POINTS);
			readWarnings(route);
			readWaypointOrder(route);
			JSONObject bounds = route.getJSONObject(BOUNDS);
			northEastBound = readBound(bounds.getJSONObject(NORTHEAST));
			southWestBound = readBound(bounds.getJSONObject(SOUTHWEST));
		} catch (JSONException e) {
			throw new DirectionsException(e);
		}
	}

	private void readLegs(JSONObject route) throws JSONException {
		JSONArray subLegs = route.getJSONArray(LEGS);
		for (int i = 0; i < subLegs.length(); i++) {
			legs.add(new DirectionsLeg(subLegs.getJSONObject(i)));
		}
	}

	private void readWaypointOrder(JSONObject route) throws JSONException {
		JSONArray order = route.getJSONArray(WAYPOINT_ORDER);
		waypointOrder = new int[order.length()];
		for (int i = 0; i < order.length(); i++) {
			waypointOrder[i] = order.getInt(i);
		}
	}

	private void readWarnings(JSONObject route) throws JSONException {
		JSONArray routeWarnings = route.getJSONArray(WARNINGS);
		for (int i = 0; i < routeWarnings.length(); i++) {
			warnings.add(routeWarnings.getString(i));
		}
	}

	private LatLng readBound(JSONObject jsonObject) throws JSONException {
		double lat = jsonObject.getDouble(LAT);
		double lng = jsonObject.getDouble(LNG);
		return new LatLng(lat, lng);
	}

	public List<LatLng> path() {
		List<LatLng> points = new ArrayList<>();
		for (DirectionsLeg leg : legs) {
			points.addAll(leg.path());
		}
		return points;
	}
}
