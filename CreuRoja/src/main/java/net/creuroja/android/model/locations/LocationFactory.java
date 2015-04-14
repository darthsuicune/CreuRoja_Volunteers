package net.creuroja.android.model.locations;

import android.content.SharedPreferences;
import android.database.Cursor;

import net.creuroja.android.model.db.CreuRojaContract;
import net.creuroja.android.model.services.Service;
import net.creuroja.android.model.services.ServiceFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;

/**
 * Created by lapuente on 21.11.14.
 */
public class LocationFactory {
	private static final String remoteId = "id";
	private static final String name = "name";
	private static final String description = "description";
	private static final String phone = "phone";
	private static final String address = "address";
	private static final String latitude = "latitude";
	private static final String longitude = "longitude";
	private static final String locationType = "location_type";
	private static final String updatedAt = "updated_at";
	private static final String active = "active";
	private static final String services = "active_services";

	protected static final String adapted = "adaptadas";
	protected static final String assembly = "asamblea";
	protected static final String bravo = "bravo";
	protected static final String cuap = "cuap";
	protected static final String gasStation = "gasolinera";
	protected static final String hospital = "hospital";
	protected static final String seaService = "maritimo";
	protected static final String nostrum = "nostrum";
	protected static final String seaBase = "salvamento";
	protected static final String terrestrial = "terrestre";


	public static Location fromJson(JSONObject json) throws JSONException, ParseException {
		Location location = new Location(json.getInt(remoteId), json.getString(name),
				(json.has(description)) ? json.getString(description) : "",
				(json.has(phone) ? json.getString(phone) : ""),
				(json.has(address)) ? json.getString(address) : "", json.getDouble(latitude),
				json.getDouble(longitude), LocationType.getType(json.getString(locationType)),
				json.getString(updatedAt), json.getBoolean(active));
		JSONArray services = json.getJSONArray(LocationFactory.services);
		for (int i = 0; i < services.length(); i++) {
			Service service = ServiceFactory.fromJson(services.getJSONObject(i));
			if (!service.archived()) {
				location.addService(service);
			}
		}
		return location;
	}

	public static Location fromCursor(Cursor cursor) {
		Location location = new Location(
				cursor.getInt(cursor.getColumnIndex(CreuRojaContract.Locations.REMOTE_ID)),
				cursor.getString(cursor.getColumnIndex(CreuRojaContract.Locations.NAME)),
				cursor.getString(cursor.getColumnIndex(CreuRojaContract.Locations.DESCRIPTION)),
				cursor.getString(cursor.getColumnIndex(CreuRojaContract.Locations.PHONE)),
				cursor.getString(cursor.getColumnIndex(CreuRojaContract.Locations.ADDRESS)),
				cursor.getDouble(cursor.getColumnIndex(CreuRojaContract.Locations.LATITUD)),
				cursor.getDouble(cursor.getColumnIndex(CreuRojaContract.Locations.LONGITUD)),
				LocationType.getType(
						cursor.getInt(cursor.getColumnIndex(CreuRojaContract.Locations.TYPE))),
				cursor.getString(cursor.getColumnIndex(CreuRojaContract.Locations.UPDATED_AT)),
				cursor.getInt(cursor.getColumnIndex(CreuRojaContract.Locations.UPDATED_AT)) == 1);

		return location;
	}

	public static Locations fromWebResponse(String response, SharedPreferences prefs)
			throws IOException, JSONException, ParseException {
		Locations list = new RailsLocations(prefs);

		JSONArray array = new JSONArray(response);

		for (int i = 0; i < array.length(); i++) {
			JSONObject object = array.getJSONObject(i);
			Location location = fromJson(object);
			list.addLocation(location);
		}
		return list;
	}

	public static Locations fromCursor(Cursor cursor, SharedPreferences prefs) {
		Locations list = new RailsLocations(prefs);
		if (cursor.moveToFirst()) {
			do {
				list.addLocation(fromCursor(cursor));
			} while (cursor.moveToNext());
		}
		return list;
	}
}
