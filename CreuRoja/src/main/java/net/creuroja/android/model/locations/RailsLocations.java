package net.creuroja.android.model.locations;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.net.Uri;

import net.creuroja.android.model.locationservices.LocationService;
import net.creuroja.android.model.db.CreuRojaContract;
import net.creuroja.android.model.services.Service;
import net.creuroja.android.model.services.Services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class RailsLocations implements Locations {
	Map<Integer, Location> locationList = new HashMap<>();
	List<LocationType> typeList = new ArrayList<>();
	String lastUpdateTime = "";
	Map<LocationType, Boolean> toggledLocations;
	SharedPreferences prefs;

	public RailsLocations(SharedPreferences prefs) {
		this.prefs = prefs;
		toggledLocations = new HashMap<>();
		for (LocationType type : LocationType.values()) {
			toggledLocations.put(type, type.getViewable(prefs));
		}
	}

	@Override public void addLocation(Location location) {
		locationList.put(location.remoteId, location);
		if (!typeList.contains(location.type)) {
			typeList.add(location.type);
		}
	}

	@Override public Collection<Location> locations() {
		Collection<Location> locations = new ArrayList<>();
		for(Location location : locationList.values()) {
			if(toggledLocations.get(location.type)) {
				locations.add(location);
			}
		}
		return locations;
	}

	@Override public List<LocationType> locationTypes() {
		return typeList;
	}

	@Override public void save(ContentResolver cr) {
		Uri uri = CreuRojaContract.Locations.CONTENT_URI;
		Locations currentLocations =
				LocationFactory.fromCursor(cr.query(uri, null, null, null, null), prefs);
		List<ContentValues> forInsert = new ArrayList<>();
		for (Location location : locationList.values()) {
			updateLastUpdateTime(location);
			decideLocationAction(location, currentLocations, forInsert, cr);
		}
		if (forInsert.size() > 0) {
			cr.bulkInsert(uri, forInsert.toArray(new ContentValues[forInsert.size()]));
		}
	}

	public void updateLastUpdateTime(Location location) {
		if (location.newerThan(lastUpdateTime)) {
			lastUpdateTime = location.updatedAt;
		}
	}

	private void decideLocationAction(Location location, Locations currentLocations,
									  List<ContentValues> forInsert, ContentResolver cr) {
		if (location.active) {
			if (currentLocations.has(location)) {
				if(currentLocations.wasUpdated(location)) {
					location.update(cr);
				}
			} else {
				forInsert.add(location.getAsValues());
			}
			saveServices(cr, location);
		} else {
			if(currentLocations.has(location)) {
				location.delete(cr);
			}
		}
	}

	public boolean has(Location location) {
		return locationList.containsKey(location.remoteId);
	}

	@Override public boolean wasUpdated(Location location) {
		return locationList.get(location.remoteId).newerThan(location.updatedAt);
	}

	public void saveServices(ContentResolver cr, Location location) {
		for (Service service : location.serviceList) {
			LocationService ls = new LocationService(location, service);
			if (service.archived()) {
				service.delete(cr);
				ls.delete(cr);
				continue;
			}
			if (Services.count(cr, service.id) > 0) {
				service.update(cr);
			} else {
				cr.insert(CreuRojaContract.Services.CONTENT_URI, service.asValues());
			}

			if (LocationService.count(cr, service.id, location.remoteId) == 0) {
				cr.insert(CreuRojaContract.LocationServices.CONTENT_URI, ls.asValues());
			}
		}
	}

	@Override public String lastUpdateTime() {
		return lastUpdateTime;
	}

	@Override public void toggleLocationType(LocationType type, boolean newState) {
		toggledLocations.put(type, newState);
	}

	@Override public boolean isVisible(int position) {
		return locationList.get(position).isVisible(prefs);
	}

	@Override public List<Location> ofType(LocationType type) {
		List<Location> list = new ArrayList<>();
		for (Location location : locationList.values()) {
			if (location.type == type) {
				list.add(location);
			}
		}
		return list;
	}

	@Override public boolean isTypeVisible(LocationType type) {
		return toggledLocations.get(type);
	}

	@Override public Iterator<Location> iterator() {
		return locations().iterator();
	}
}
