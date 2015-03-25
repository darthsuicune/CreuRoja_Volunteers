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
	private static final String sRemoteId = "id";
	private static final String sName = "name";
	private static final String sDescription = "description";
	private static final String sPhone = "phone";
	private static final String sAddress = "address";
	private static final String sLatitude = "latitude";
	private static final String sLongitude = "longitude";
	private static final String sLocationType = "location_type";
	private static final String sUpdatedAt = "updated_at";
	private static final String sActive = "active";
	private static final String sServices = "active_services";

	protected static final String sAdapted = "adaptadas";
	protected static final String sAssembly = "asamblea";
	protected static final String sBravo = "bravo";
	protected static final String sCuap = "cuap";
	protected static final String sGasStation = "gasolinera";
	protected static final String sHospital = "hospital";
	protected static final String sSeaService = "maritimo";
	protected static final String sNostrum = "nostrum";
	protected static final String sSeaBase = "salvamento";
	protected static final String sTerrestrial = "terrestre";


	public static Location fromJson(JSONObject json) throws JSONException, ParseException {
		Location location = new Location(json.getInt(sRemoteId), json.getString(sName),
				(json.has(sDescription)) ? json.getString(sDescription) : "",
				(json.has(sPhone) ? json.getString(sPhone) : ""),
				(json.has(sAddress)) ? json.getString(sAddress) : "", json.getDouble(sLatitude),
				json.getDouble(sLongitude), LocationType.getType(json.getString(sLocationType)),
				json.getString(sUpdatedAt), json.getBoolean(sActive));
		JSONArray services = json.getJSONArray(sServices);
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
