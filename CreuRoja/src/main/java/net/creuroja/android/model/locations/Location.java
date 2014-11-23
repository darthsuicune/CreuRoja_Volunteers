package net.creuroja.android.model.locations;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.net.Uri;
import android.text.TextUtils;

import net.creuroja.android.model.services.Service;
import net.creuroja.android.model.db.CreuRojaContract;
import net.creuroja.android.utils.DateTimeUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by denis on 14.06.14.
 */
public class Location {
	public final int remoteId;
	public final String name;
	public final String description;
	public final String phone;
	public final String address;
	public final double latitude;
	public final double longitude;
	public final LocationType type;
	public final String updatedAt;
	public final boolean active;
	public final List<Service> serviceList;

	public Location(int remoteId, String name, String description, String phone, String address,
					double latitude, double longitude, LocationType type, String updatedAt,
					boolean active) {
		this.remoteId = remoteId;
		this.name = name;
		this.description = description;
		this.phone = phone;
		this.address = address;
		this.latitude = latitude;
		this.longitude = longitude;
		this.type = type;
		this.updatedAt = updatedAt;
		this.active = active;
		serviceList = new ArrayList<>();
	}

	public void addService(Service service) {
		if (!serviceList.contains(service)) {
			serviceList.add(service);
		}
	}

	public ContentValues getAsValues() {
		ContentValues values = new ContentValues();
		values.put(CreuRojaContract.Locations.ACTIVE, (active) ? 1 : 0);
		values.put(CreuRojaContract.Locations.ADDRESS, address);
		values.put(CreuRojaContract.Locations.DESCRIPTION, description);
		values.put(CreuRojaContract.Locations.LATITUD, latitude);
		values.put(CreuRojaContract.Locations.LONGITUD, longitude);
		values.put(CreuRojaContract.Locations.NAME, name);
		values.put(CreuRojaContract.Locations.PHONE, phone);
		values.put(CreuRojaContract.Locations.REMOTE_ID, remoteId);
		values.put(CreuRojaContract.Locations.TYPE, type.toString());
		values.put(CreuRojaContract.Locations.UPDATED_AT, updatedAt);
		return values;
	}

	public void update(ContentResolver cr) {
		Uri uri = CreuRojaContract.Locations.CONTENT_URI;
		String where = CreuRojaContract.Locations.REMOTE_ID + "=?";
		String[] selectionArgs = {Integer.toString(remoteId)};
		cr.update(uri, this.getAsValues(), where, selectionArgs);
	}

	public void delete(ContentResolver cr) {
		Uri uri = CreuRojaContract.Locations.CONTENT_URI;
		String where = CreuRojaContract.Locations.REMOTE_ID + "=?";
		String[] selectionArgs = {Integer.toString(remoteId)};
		cr.delete(uri, where, selectionArgs);
	}

	public boolean newerThan(String lastUpdate) {
		if (TextUtils.isEmpty(lastUpdate)) {
			return true;
		}
		try {
			Date updatedAt = DateTimeUtils.parse(this.updatedAt);
			Date saved = DateTimeUtils.parse(lastUpdate);
			return updatedAt.after(saved);
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean isVisible(SharedPreferences prefs) {
		return type.getViewable(prefs);
	}
}
