package net.creuroja.android.model.locations;

import android.content.ContentResolver;

import java.util.List;

/**
 * Created by denis on 19.06.14.
 */
public interface LocationList {
	public List<Location> getLocations();
	public Location get(long id);
	public void save(ContentResolver cr);
	public boolean has(Location location);
	public String getLastUpdateTime();
}
