package org.creuroja.android.model.ws;

import android.content.ContentResolver;

import org.creuroja.android.model.Location;

import java.util.List;

/**
 * Created by denis on 19.06.14.
 */
public interface LocationList {
	public List<Location> getLocations();
	public String save(ContentResolver cr);
}