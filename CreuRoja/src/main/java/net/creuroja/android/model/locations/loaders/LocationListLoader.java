package net.creuroja.android.model.locations.loaders;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;

import net.creuroja.android.model.locations.LocationFactory;
import net.creuroja.android.model.locations.LocationList;

/**
 * Created by lapuente on 24.11.14.
 */
public class LocationListLoader extends AsyncTaskLoader<LocationList> {
	ContentResolver cr;
	Uri uri;
	String[] projection;
	String selection;
	String[] selectionArgs;
	String sortOrder;
	SharedPreferences prefs;

	public LocationListLoader(Context context, Uri uri, String[] projection, String selection,
							  String[] selectionArgs, String sortOrder, SharedPreferences prefs) {
		super(context);
		this.uri = uri;
		this.projection = projection;
		this.selection = selection;
		this.selectionArgs = selectionArgs;
		this.sortOrder = sortOrder;
		this.prefs = prefs;
	}

	@Override protected void onStartLoading() {
		super.onStartLoading();
		if(cr == null) {
			forceLoad();
		}
	}

	@Override public LocationList loadInBackground() {
		cr = getContext().getContentResolver();
		Cursor cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
		return LocationFactory.fromCursor(cursor, prefs);
	}
}
