package net.creuroja.android.view.locations.fragments.maps;


import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import net.creuroja.android.model.locations.Location;
import net.creuroja.android.model.locations.LocationType;

public class ClusterMarker implements ClusterItem {
	Location location;

	public ClusterMarker(Location location) {
		this.location = location;
	}

	@Override public LatLng getPosition() {
		return new LatLng(location.latitude, location.longitude);
	}

	public boolean isOneOf(LocationType type) {
		return location.type == type;
	}

	public int icon() {
		return location.type.icon;
	}
}
