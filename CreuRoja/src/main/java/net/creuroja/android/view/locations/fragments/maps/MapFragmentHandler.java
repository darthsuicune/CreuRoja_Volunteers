package net.creuroja.android.view.locations.fragments.maps;

import android.support.v4.app.Fragment;

import net.creuroja.android.model.directions.Directions;
import net.creuroja.android.model.locations.Location;
import net.creuroja.android.model.locations.LocationType;

import static net.creuroja.android.view.locations.fragments.LocationsHandlerFragment.OnLocationsListUpdated;

public interface MapFragmentHandler {
	String ARG_SEARCH_QUERY = "searchQuery";

	void setMapType(MapType mapType);

	void getDirections(android.location.Location origin, Location destination);

	void activateLocationsOfType(LocationType type);
	void deactivateLocationsOfType(LocationType type);

	boolean locate(android.location.Location location);

	OnLocationsListUpdated getOnLocationsListUpdatedListener();

	Fragment getFragment();

	void removeDirections();

	boolean hasDirections();

	void setMapInteractionListener(MapInteractionListener mapInteractionListener);

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

	interface MapInteractionListener {
		void onLocationClicked(Location location);
	}

	interface DirectionsDrawnListener {
		void onDirectionsDrawn(Directions directions);
	}
}
