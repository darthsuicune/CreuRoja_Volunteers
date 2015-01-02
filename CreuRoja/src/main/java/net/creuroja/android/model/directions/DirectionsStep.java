package net.creuroja.android.model.directions;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by lapuente on 02.01.15.
 */
public class DirectionsStep {
	public LatLng startLocation;
	public LatLng endLocation;
	public List<DirectionsStep> subSteps;
	public String polyline;

	//TODO
	public List<LatLng> decodePath() {
		return null;
	}
}
