package net.creuroja.android.view.fragments.locations;

import net.creuroja.android.model.locations.Location;

/**
 * Created by lapuente on 30.09.14.
 */
public interface OnDirectionsRequestedListener {
	public boolean onDirectionsRequested(Location location);
	public void onRemoveRouteRequested();

	boolean hasDirections();
}
