package net.creuroja.android.model.directions;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lapuente on 02.01.15.
 */
public class DirectionsStep {
	public LatLng startLocation;
	public LatLng endLocation;
	public List<DirectionsStep> subSteps;
	public String polyline;

	public List<LatLng> path() {
		List<LatLng> points = new ArrayList<>();
		points.add(new LatLng(this.startLocation.latitude, this.startLocation.longitude));
		for (LatLng latlng : this.decodePolyline()) {
			points.add(new LatLng(latlng.latitude, latlng.longitude));
		}
		for (DirectionsStep subStep : this.subSteps) {
			points.addAll(subStep.path());
		}
		points.add(new LatLng(this.endLocation.latitude, this.endLocation.longitude));
		return points;
	}

	/**
	 * This method was completely copied from some point in stackoverflow.com
	 * If you wanna know what it does, good luck finding it again.
	 * <p/>
	 * It converts the List<LatLng> encoded in the response from Google into an array of points.
	 *
	 * @return
	 */
	private List<LatLng> decodePolyline() {
		List<LatLng> poly = new ArrayList<>();
		int index = 0, len = polyline.length();
		int lat = 0, lng = 0;
		while (index < len) {
			int b, shift = 0, result = 0;
			do {
				b = polyline.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lat += dlat;
			shift = 0;
			result = 0;
			do {
				b = polyline.charAt(index++) - 63;
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

}
