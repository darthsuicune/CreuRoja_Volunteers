package net.creuroja.android.model.directions.loader;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import net.creuroja.android.model.directions.Directions;

/**
 * Loader for retrieving directions. Requires
 */
public class DirectionsLoader extends AsyncTaskLoader<Directions> {
	public static final String ARG_ORIG_LAT = "originLatitude";
	public static final String ARG_ORIG_LONG = "originLongitude";
	public static final String ARG_DEST_LAT = "destinationLatitude";
	public static final String ARG_DEST_LONG = "destinationLongitude";

	/**
	 * This 4 parameters are required for proper working of the call. They will be assumed to be
	 * present, either through the args bundle or through the setters.
	 */
	private double originLat;
	private double originLng;
	private double destinationLat;
	private double destinationLng;

	private Directions directions;

	/**
	 * @param context Running context
	 * @param args REQUIRED for calculating the directions
	 */
	public DirectionsLoader(Context context, Bundle args) {
		super(context);
		if (args != null) {
			originLat = args.getDouble(ARG_ORIG_LAT);
			originLng = args.getDouble(ARG_ORIG_LONG);
			destinationLat = args.getDouble(ARG_DEST_LAT);
			destinationLng = args.getDouble(ARG_DEST_LONG);
		}
	}

	public void originLatitude(double originLatitude) {
		this.originLat = originLatitude;
	}

	public void originLongitude(double originLongitude) {
		this.originLng = originLongitude;
	}

	public void destinationLatitude(double destinationLatitude) {
		this.destinationLat = destinationLatitude;
	}

	public void destinationLongitude(double destinationLongitude) {
		this.destinationLng = destinationLongitude;
	}

	@Override
	protected void onStartLoading() {
		super.onStartLoading();
		if (directions == null) {
			forceLoad();
		}
	}

	@Override public Directions loadInBackground() {
		directions = new Directions();
		directions.get(originLat, originLng, destinationLat, destinationLng);
		return directions;
	}
}
