package net.creuroja.android.model.services;

import android.database.Cursor;

import net.creuroja.android.model.db.CreuRojaContract;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lapuente on 21.11.14.
 */
public class ServiceFactory {
	private static final String sId = "id";
	private static final String sName = "name";
	private static final String sDescription = "description";
	private static final String sBaseTime = "base_time";
	private static final String sStartTime = "start_time";
	private static final String sEndTime = "end_time";
	private static final String sCode = "code";
	private static final String sUpdatedAt = "updated_at";
	private static final String sArchived = "archived";

	public static Service fromJson(JSONObject object) throws JSONException, ParseException {
		return new Service(object.getInt(sId), object.getString(sName),
				object.getString(sDescription), object.getString(sBaseTime),
				object.getString(sStartTime), object.getString(sEndTime), object.getString(sCode),
				object.getString(sUpdatedAt), object.getBoolean(sArchived));
	}

	public static Service fromCursor(Cursor cursor) {
		return new Service(
				cursor.getInt(cursor.getColumnIndex(CreuRojaContract.Services.REMOTE_ID)),
				cursor.getString(cursor.getColumnIndex(CreuRojaContract.Services.NAME)),
				cursor.getString(cursor.getColumnIndex(CreuRojaContract.Services.DESCRIPTION)),
				cursor.getString(cursor.getColumnIndex(CreuRojaContract.Services.BASETIME)),
				cursor.getString(cursor.getColumnIndex(CreuRojaContract.Services.STARTTIME)),
				cursor.getString(cursor.getColumnIndex(CreuRojaContract.Services.ENDTIME)),
				cursor.getString(cursor.getColumnIndex(CreuRojaContract.Services.CODE)),
				cursor.getString(cursor.getColumnIndex(CreuRojaContract.Services.UPDATED_AT)),
				(cursor.getInt(cursor.getColumnIndex(CreuRojaContract.Services.ARCHIVED)) == 1));
	}

	public static List<Service> listFromCursor(Cursor cursor) {
		List<Service> list = new ArrayList<>();
		if (cursor.moveToFirst()) {
			do {
				list.add(fromCursor(cursor));
			} while (cursor.moveToNext());
		}
		return list;
	}
}
