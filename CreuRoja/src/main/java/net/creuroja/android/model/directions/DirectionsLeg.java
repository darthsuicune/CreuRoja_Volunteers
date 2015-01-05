package net.creuroja.android.model.directions;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lapuente on 02.01.15.
 */
public class DirectionsLeg {
	public LatLng startLocation;
	public LatLng endLocation;
	public List<DirectionsStep> steps;

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
