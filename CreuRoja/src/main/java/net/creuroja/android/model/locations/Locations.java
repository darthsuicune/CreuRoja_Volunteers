package net.creuroja.android.model.locations;

import android.content.ContentResolver;

import java.util.Collection;
import java.util.List;

/**
 * Created by denis on 19.06.14.
 */
public interface Locations extends Iterable<Location> {
	void addLocation(Location location);
	Collection<Location> locations();
	List<LocationType> locationTypes();
	void save(ContentResolver cr);
	boolean has(Location location);
	boolean wasUpdated(Location location);
	String lastUpdateTime();
	void toggleLocationType(LocationType type, boolean newState);
	boolean isVisible(int position);

	List<Location> ofType(LocationType type);

	boolean isTypeVisible(LocationType type);
}
