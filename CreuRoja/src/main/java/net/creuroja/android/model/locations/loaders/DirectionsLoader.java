package net.creuroja.android.model.locations.loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import net.creuroja.android.model.locations.Directions;

import org.json.JSONException;

import java.io.IOException;

/**
 * Created by lapuente on 30.09.14.
 */
public class DirectionsLoader extends AsyncTaskLoader<Directions> {
	public static final String ARG_ORIG_LAT = "originLatitude";
	public static final String ARG_ORIG_LONG = "originLongitude";
	public static final String ARG_DEST_LAT = "destinationLatitude";
	public static final String ARG_DEST_LONG = "destinationLongitude";
	
	private double originLat;
	private double originLng;
	private double destinationLat;
	private double destinationLng;

	private Directions mDirections;
	
	public DirectionsLoader(Context context, Bundle args) {
		super(context);
		originLat = args.getDouble(ARG_ORIG_LAT);
		originLng = args.getDouble(ARG_ORIG_LONG);
		destinationLat = args.getDouble(ARG_DEST_LAT);
		destinationLng = args.getDouble(ARG_DEST_LONG);
	}

	@Override
	protected void onStartLoading() {
		super.onStartLoading();
		if(mDirections == null){
			forceLoad();
		}
	}

	@Override public Directions loadInBackground() {
		try {
			mDirections = Directions.get(originLat, originLng, destinationLat, destinationLng);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return mDirections;
	}
}
