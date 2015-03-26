package net.creuroja.android.view.fragments.locations.maps;

import android.support.v4.app.Fragment;

import net.creuroja.android.model.locations.Location;
import net.creuroja.android.model.locations.LocationType;

import static net.creuroja.android.view.fragments.locations.LocationsHandlerFragment.OnLocationsListUpdated;

/**
 * Created by denis on 10.08.14.
 */
public interface MapFragmentHandler {
	String ARG_SEARCH_QUERY = "searchQuery";

	void setMapType(MapType mapType);

	void getDirections(android.location.Location origin, Location destination);

	void toggleLocations(LocationType type, boolean active);

	boolean locate(android.location.Location location);

	OnLocationsListUpdated getOnLocationsListUpdatedListener();

	Fragment getFragment();

	void removeDirections();

	boolean hasDirections();

	enum MapType {
		MAP_TYPE_NORMAL(0), MAP_TYPE_TERRAIN(1), MAP_TYPE_SATELLITE(2), MAP_TYPE_HYBRID(3);

		private final int value;

		MapType(int value) {
			this.value = value;
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
			return value;
		}
	}
}
