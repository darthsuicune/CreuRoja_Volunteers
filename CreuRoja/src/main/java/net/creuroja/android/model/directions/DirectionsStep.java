package net.creuroja.android.model.directions;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lapuente on 02.01.15.
 * Sample step:
 * {
 * "travel_mode": "DRIVING",
 * "start_location": {
 * "lat": 41.8507300,
 * "lng": -87.6512600
 * },
 * "end_location": {
 * "lat": 41.8525800,
 * "lng": -87.6514100
 * },
 * "polyline": {
 * "points": "a~l~Fjk~uOwHJy@P"
 * },
 * "duration": {
 * "value": 19,
 * "text": "1 min"
 * },
 * "html_instructions": "Head \u003cb\u003enorth\u003c/b\u003e on \u003cb\u003eS Morgan St\u003c/b\u003e toward \u003cb\u003eW Cermak Rd\u003c/b\u003e",
 * "distance": {
 * "value": 207,
 * "text": "0.1 mi"
 * }
 */
public class DirectionsStep {
	public static final String SUBSTEPS = "substeps";
	public static final String START_LOCATION = "start_location";
	public static final String END_LOCATION = "end_location";
	public static final String DURATION = "duration";
	public static final String DISTANCE = "distance";
	public static final String POLYLINE = "polyline";
	public static final String POINTS = "points";
	public static final String HTML_INSTRUCTIONS = "html_instructions";
	public static final String VALUE = "value";
	public static final String TEXT = "text";
	public static final String LAT = "lat";
	public static final String LNG = "lng";

	public LatLng startLocation;
	public LatLng endLocation;
	public long durationValue;
	public String durationText;
	public long distanceValue;
	public String distanceText;
	public List<DirectionsStep> subSteps;
	public String polyline;
	public String htmlInstructions;

	public DirectionsStep(JSONObject step) {
		try {
			subSteps = new ArrayList<>();
			if (step.has(SUBSTEPS)) {
				readSubSteps(step);
			}
			startLocation = readLocation(step.getJSONObject(START_LOCATION));
			endLocation = readLocation(step.getJSONObject(END_LOCATION));
			readDuration(step);
			readDistance(step);
			htmlInstructions = step.getString(HTML_INSTRUCTIONS);
			polyline = step.getJSONObject(POLYLINE).getString(POINTS);
		} catch (JSONException e) {
			throw new DirectionsException(e);
		}
	}

	private void readSubSteps(JSONObject step) throws JSONException {
		JSONArray stepSubSteps = step.getJSONArray(SUBSTEPS);
		for (int i = 0; i < stepSubSteps.length(); i++) {
			subSteps.add(new DirectionsStep(stepSubSteps.getJSONObject(i)));
		}
	}

	private LatLng readLocation(JSONObject location) throws JSONException {
		double lat = location.getDouble(LAT);
		double lng = location.getDouble(LNG);
		return new LatLng(lat, lng);
	}

	private void readDuration(JSONObject step) throws JSONException {
		JSONObject duration = step.getJSONObject(DURATION);
		durationValue = duration.getLong(VALUE);
		durationText = duration.getString(TEXT);
	}

	private void readDistance(JSONObject step) throws JSONException {
		JSONObject distance = step.getJSONObject(DISTANCE);
		distanceValue = distance.getLong(VALUE);
		distanceText = distance.getString(TEXT);
	}

	public List<LatLng> path() {
		List<LatLng> points = new ArrayList<>();
		points.add(new LatLng(this.startLocation.latitude, this.startLocation.longitude));
		for (LatLng latlng : PolyUtil.decode(polyline)) {
			points.add(new LatLng(latlng.latitude, latlng.longitude));
		}
		if (!subSteps.isEmpty()) {
			for (DirectionsStep subStep : this.subSteps) {
				points.addAll(subStep.path());
			}
		}
		points.add(new LatLng(this.endLocation.latitude, this.endLocation.longitude));
		return points;
	}
}
