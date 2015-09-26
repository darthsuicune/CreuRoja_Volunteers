package com.dlgdev.directions;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lapuente on 02.01.15.
 * Sample leg:
 {
	 "steps": [...],
	 "duration": {
		 "value": 74384,
		 "text": "20 hours 40 mins"
	 },
	 "distance": {
		 "value": 2137146,
		 "text": "1,328 mi"
	 },
	 "start_location": {
		 "lat": 35.4675602,
		 "lng": -97.5164276
	 },
	 "end_location": {
		 "lat": 34.0522342,
		 "lng": -118.2436849
	 },
	 "start_address": "Oklahoma City, OK, USA",
	 "end_address": "Los Angeles, CA, USA"
 }
 */
public class DirectionsLeg {
	public static final String STEPS = "steps";
	public static final String START_LOCATION = "start_location";
	public static final String END_LOCATION = "end_location";
	public static final String START_ADDRESS = "start_address";
	public static final String END_ADDRESS = "end_address";
	public static final String DURATION = "duration";
	public static final String DISTANCE = "distance";
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
	public String startAddress;
	public String endAddress;
	public List<DirectionsStep> steps;

	public DirectionsLeg(JSONObject leg) {
		steps = new ArrayList<>();
		try {
			readSteps(leg);
			startLocation = readLocation(leg.getJSONObject(START_LOCATION));
			endLocation = readLocation(leg.getJSONObject(END_LOCATION));
			readDuration(leg);
			readDistance(leg);
			startAddress = leg.getString(START_ADDRESS);
			endAddress = leg.getString(END_ADDRESS);
		} catch (JSONException e) {
			throw new DirectionsException(e);
		}
	}

	private void readSteps(JSONObject leg) throws JSONException {
		JSONArray legSteps = leg.getJSONArray(STEPS);
		for(int i = 0; i < legSteps.length(); i++) {
			steps.add(new DirectionsStep(legSteps.getJSONObject(i)));
		}
	}

	private LatLng readLocation(JSONObject location) throws JSONException {
		double lat = location.getDouble(LAT);
		double lng = location.getDouble(LNG);
		return new LatLng(lat,lng);
	}

	private void readDuration(JSONObject leg) throws JSONException {
		JSONObject duration = leg.getJSONObject(DURATION);
		durationValue = duration.getLong(VALUE);
		durationText = duration.getString(TEXT);
	}

	private void readDistance(JSONObject leg) throws JSONException {
		JSONObject distance = leg.getJSONObject(DISTANCE);
		distanceValue = distance.getLong(VALUE);
		distanceText = distance.getString(TEXT);
	}

	public List<LatLng> path() {
		List<LatLng> points = new ArrayList<>();
		points.add(new LatLng(this.startLocation.latitude, this.startLocation.longitude));
		for (DirectionsStep step : this.steps) {
			points.addAll(step.path());
		}
		points.add(new LatLng(this.endLocation.latitude, this.endLocation.longitude));
		return points;
	}
}
