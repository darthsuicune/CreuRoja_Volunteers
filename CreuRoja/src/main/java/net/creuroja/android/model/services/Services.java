package net.creuroja.android.model.services;

import android.content.ContentResolver;
import android.database.Cursor;

import net.creuroja.android.model.db.CreuRojaContract;

/**
 * Created by lapuente on 19.01.15.
 */
public class Services {
	public static int count(ContentResolver cr) {
		Cursor services = cr.query(CreuRojaContract.Services.CONTENT_URI, null, null, null, null);
		final int count = services.getCount();
		services.close();
		return count;
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
