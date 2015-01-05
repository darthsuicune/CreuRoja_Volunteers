package net.creuroja.android.model.directions;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lapuente on 02.01.15.
 */
public class DirectionsRoute {
	public List<DirectionsLeg> legs;

	public List<LatLng> path() {
		List<LatLng> points = new ArrayList<>();
		for (DirectionsLeg leg : legs) {
			points.addAll(leg.path());
		}
		return points;
	}
}
