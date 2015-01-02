package net.creuroja.android.view.fragments.locations.maps;

import android.support.v4.app.Fragment;

import net.creuroja.android.model.locations.Location;
import net.creuroja.android.model.locations.LocationType;

import static net.creuroja.android.view.fragments.locations.LocationsHandlerFragment.OnLocationsListUpdated;

/**
 * Created by denis on 10.08.14.
 */
public interface MapFragmentHandler {
	public static final String ARG_SEARCH_QUERY = "searchQuery";

	public abstract void setMapType(MapType mapType);

	public abstract void getDirections(android.location.Location origin, Location destination);

	public abstract void toggleLocations(LocationType type, boolean active);

	public abstract boolean locate(android.location.Location location);

	public abstract OnLocationsListUpdated getOnLocationsListUpdatedListener();

	public abstract Fragment getFragment();

	public void removeDirections();

	public boolean hasDirections();

	public enum MapType {
		MAP_TYPE_NORMAL(0), MAP_TYPE_TERRAIN(1), MAP_TYPE_SATELLITE(2), MAP_TYPE_HYBRID(3);

		private final int mValue;

		MapType(int value) {
			mValue = value;
		}

		public static MapType fromValue(final int value) {
			switch (value) {
				case 1:
					return MAP_TYPE_TERRAIN;
				case 2:
					return MAP_TYPE_SATELLITE;
				case 3:
					return MAP_TYPE_HYBRID;
				case 0:
				default:
					return MAP_TYPE_NORMAL;
			}
		}

		public int getValue() {
			return mValue;
		}
	}
}
