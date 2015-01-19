package net.creuroja.android.model.directions.loader;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import com.google.android.gms.maps.model.LatLng;

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
    private LatLng origin;
    private LatLng destination;

    private Directions directions;

    /**
     * @param context Running context
     * @param args    REQUIRED for calculating the directions
     */
    public DirectionsLoader(Context context, Bundle args) {
        super(context);
        if (args != null) {
            origin = new LatLng(args.getDouble(ARG_ORIG_LAT), args.getDouble(ARG_ORIG_LONG));
            destination = new LatLng(args.getDouble(ARG_DEST_LAT), args.getDouble(ARG_DEST_LONG));
        }
    }

    public void origin(LatLng origin) {
        this.origin = origin;
    }

    public void destination(LatLng destination) {
        this.destination = destination;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if (directions == null) {
            forceLoad();
        }
    }

    @Override
    public Directions loadInBackground() {
        directions = new Directions(origin, destination);
        return directions.get();
    }
}
