package com.cruzroja.android.model.ws;

import android.content.ContentResolver;

import com.cruzroja.android.model.Location;

import java.util.List;

/**
 * Created by denis on 19.06.14.
 */
public interface LocationList {
	public List<Location> getLocations();
	public String save(ContentResolver cr);
}