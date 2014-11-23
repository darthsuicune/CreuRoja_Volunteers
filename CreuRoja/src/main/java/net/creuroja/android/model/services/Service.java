package net.creuroja.android.model.services;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import net.creuroja.android.model.db.CreuRojaContract;

/**
 * id, name, updatedAt, code, description, baseTime, startTime, endTime, archived)
 */
public class Service {
	public final int id;
	private final String name;
	private final String description;
	private final String code;
	private final String baseTime;
	private final String startTime;
	private final String endTime;
	private final String updatedAt;
	private final boolean archived;

	public Service(int id, String name, String description, String baseTime,
				   String startTime, String endTime, String code, String updatedAt,
				   boolean archived) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.baseTime = baseTime;
		this.startTime = startTime;
		this.endTime = endTime;
		this.code = code;
		this.updatedAt = updatedAt;
		this.archived = archived;
	}

	@Override public boolean equals(Object o) {
		return ((Service) o).id == id;
	}

	public boolean archived() {
		return archived;
	}

	public ContentValues asValues() {
		ContentValues values = new ContentValues();
		values.put(CreuRojaContract.Services.REMOTE_ID, id);
		values.put(CreuRojaContract.Services.NAME, name);
		values.put(CreuRojaContract.Services.DESCRIPTION, description);
		values.put(CreuRojaContract.Services.CODE, code);
		values.put(CreuRojaContract.Services.BASETIME, baseTime);
		values.put(CreuRojaContract.Services.STARTTIME, startTime);
		values.put(CreuRojaContract.Services.ENDTIME, endTime);
		values.put(CreuRojaContract.Services.UPDATED_AT, updatedAt);
		values.put(CreuRojaContract.Services.ARCHIVED, (archived) ? 1 : 0);
		return values;
	}

	public void update(ContentResolver cr) {
		Uri uri = CreuRojaContract.Services.CONTENT_URI;
		String where = CreuRojaContract.Services.REMOTE_ID + "=?";
		String[] selectionArgs = { Integer.toString(id) };
		cr.update(uri, this.asValues(), where, selectionArgs);
	}

	public void delete(ContentResolver cr) {
		Uri uri = CreuRojaContract.Services.CONTENT_URI;
		String where = CreuRojaContract.Services.REMOTE_ID + "=?";
		String[] selectionArgs = {Integer.toString(id)};
		cr.delete(uri, where, selectionArgs);
	}

	public static int count(ContentResolver cr, int serviceId) {
		String where = CreuRojaContract.Services.REMOTE_ID + "=?";
		String[] whereArgs = {Integer.toString(serviceId)};
		Cursor services =
				cr.query(CreuRojaContract.Services.CONTENT_URI, null, where, whereArgs, null);
		final int count = services.getCount();
		services.close();
		return count;
	}
}
