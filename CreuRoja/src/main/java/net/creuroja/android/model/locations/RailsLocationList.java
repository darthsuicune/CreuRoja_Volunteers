package net.creuroja.android.model.locations;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.net.Uri;

import net.creuroja.android.model.locationservices.LocationService;
import net.creuroja.android.model.db.CreuRojaContract;
import net.creuroja.android.model.services.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by denis on 19.06.14.
 */
public class RailsLocationList implements LocationList {
	private List<Location> locationList = new ArrayList<>();
	private List<Integer> idList = new ArrayList<>();
	private List<LocationType> mTypeList = new ArrayList<>();
	private String lastUpdateTime = "";
	private Map<LocationType, Boolean> mToggledLocations;
	private SharedPreferences prefs;

	public RailsLocationList(SharedPreferences prefs) {
		this.prefs = prefs;
		mToggledLocations = new HashMap<>();
		for (LocationType type : LocationType.values()) {
			mToggledLocations.put(type, type.getViewable(prefs));
		}
	}

	@Override public void addLocation(Location location) {
		locationList.add(location);
		idList.add(location.remoteId);
		if (!mTypeList.contains(location.type)) {
			mTypeList.add(location.type);
		}
	}

	@Override
	public List<Location> getLocations() {
		List<Location> result = new ArrayList<>();
		for (Location location : locationList) {
			if (mToggledLocations.get(location.type)) {
				result.add(location);
			}
		}
		return result;
	}

	@Override public List<LocationType> getLocationTypes() {
		return mTypeList;
	}

	@Override public Location getById(long id) {
		for (Location location : locationList) {
			if (location.remoteId == id) {
				return location;
			}
		}
		return null;
	}

	@Override public Location get(int position) {
		return locationList.get(position);
	}

	@Override public void save(ContentResolver cr) {
		Uri uri = CreuRojaContract.Locations.CONTENT_URI;
		LocationList currentLocations =
				LocationFactory.fromCursor(cr.query(uri, null, null, null, null), prefs);
		List<ContentValues> forInsert = new ArrayList<>();
		for (Location location : locationList) {
			if (location.newerThan(lastUpdateTime)) {
				lastUpdateTime = location.updatedAt;
			}
			if (location.active) {
				if (currentLocations.has(location)) {
					location.update(cr);
				} else {
					forInsert.add(location.getAsValues());
				}
				saveServices(cr, location);
			} else {
				location.delete(cr);
			}
		}
		if (forInsert.size() > 0) {
			cr.bulkInsert(uri, forInsert.toArray(new ContentValues[forInsert.size()]));
		}
	}

	public boolean has(Location location) {
		for (Integer current : idList) {
			if (current == location.remoteId) {
				return true;
			}
		}
		return false;
	}

	@Override public String getLastUpdateTime() {
		return lastUpdateTime;
	}

	@Override public void toggleLocationType(LocationType type, boolean newState) {
		mToggledLocations.put(type, newState);
	}

	@Override public boolean isVisible(int position) {
		return locationList.get(position).isVisible(prefs);
	}

	public void saveServices(ContentResolver cr, Location location) {
		for (Service service : location.serviceList) {
			LocationService ls = new LocationService(location, service);
			if(service.archived()) {
				service.delete(cr);
				ls.delete(cr);
				continue;
			}
			if (Service.count(cr, service.id) > 0) {
				service.update(cr);
			} else {
				cr.insert(CreuRojaContract.Services.CONTENT_URI, service.asValues());
			}

			if (LocationService.count(cr, service.id, location.remoteId) == 0) {
				cr.insert(CreuRojaContract.LocationServices.CONTENT_URI, ls.asValues());
			}
		}
	}
}
