package net.creuroja.android.model.locationservices;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;

import net.creuroja.android.model.db.CreuRojaContract;
import net.creuroja.android.model.locations.Location;
import net.creuroja.android.model.services.Service;

/**
 * Created by denis on 22.11.14.
 */
public class LocationService {
	Location location;
	Service service;

	public LocationService(Location location, Service service) {
		this.location = location;
		this.service = service;
	}

	public ContentValues asValues() {
		ContentValues values = new ContentValues();
		values.put(CreuRojaContract.LocationServices.LOCATION_ID, location.remoteId);
		values.put(CreuRojaContract.LocationServices.SERVICE_ID, service.id);
		return values;
	}

	public void delete(ContentResolver cr) {
		String where = CreuRojaContract.LocationServices.SERVICE_ID + "=? AND " +
					   CreuRojaContract.LocationServices.LOCATION_ID + "=?";
		String[] whereArgs = {Integer.toString(service.id), Integer.toString(location.remoteId)};
		cr.delete(CreuRojaContract.LocationServices.CONTENT_URI, where, whereArgs);
	}

	public static String[] serviceIds(Cursor cursor) {
		String[] result = new String[cursor.getCount()];
		if (cursor.moveToFirst()) {
			int i = 0;
			do {
				result[i++] = cursor.getString(
						cursor.getColumnIndex(CreuRojaContract.LocationServices.SERVICE_ID));
			} while (cursor.moveToNext());
		}
		return result;
	}

	public static int count(ContentResolver cr, int serviceId, int locationId) {
		String where = CreuRojaContract.LocationServices.SERVICE_ID + "=? AND " +
					   CreuRojaContract.LocationServices.LOCATION_ID + "=?";
		String[] whereArgs =
				new String[]{Integer.toString(serviceId), Integer.toString(locationId)};
		Cursor cursor =
				cr.query(CreuRojaContract.LocationServices.CONTENT_URI, null, where, whereArgs,
						null);
		final int count = cursor.getCount();
		cursor.close();
		return count;
	}
}
