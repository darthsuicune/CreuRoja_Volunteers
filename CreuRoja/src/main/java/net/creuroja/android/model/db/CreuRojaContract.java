package net.creuroja.android.model.db;

import android.net.Uri;
import android.provider.BaseColumns;

public final class CreuRojaContract {

	public static final class Locations implements BaseColumns {
		public static final String TABLE_NAME = "locations";
		public static final Uri CONTENT_URI =
				Uri.parse("content://" + CreuRojaProvider.CONTENT_NAME + "/" + TABLE_NAME);
		public static final String DEFAULT_ORDER = _ID + " DESC";

		public static final String NAME = "name";
		public static final String REMOTE_ID = "remote_id";
		public static final String DESCRIPTION = "description";
		public static final String ADDRESS = "address";
		public static final String PHONE = "phone";
		public static final String LATITUD = "latitud";
		public static final String LONGITUD = "longitud";
		public static final String TYPE = "location_type";
		public static final String UPDATED_AT = "updated_at";
		public static final String ACTIVE = "active";

		//Distinct values for location types.
		protected static final String DISTINCT_LOCATIONS = "distinctLocations";
		public static final Uri CONTENT_DISTINCT_LOCATIONS =
				Uri.parse("content://" + CreuRojaProvider.CONTENT_NAME + "/" + DISTINCT_LOCATIONS);
	}

	public static final class Services implements BaseColumns {
		public static final String TABLE_NAME = "services";
		public static final Uri CONTENT_URI =
				Uri.parse("content://" + CreuRojaProvider.CONTENT_NAME + "/" + TABLE_NAME);
		public static final String DEFAULT_ORDER = _ID + " DESC";

		public static final String REMOTE_ID = "remote_id";
		public static final String NAME = "name";
		public static final String DESCRIPTION = "description";
		public static final String BASETIME = "basetime";
		public static final String STARTTIME = "starttime";
		public static final String ENDTIME = "endtime";
		public static final String CODE = "code";
		public static final String ARCHIVED = "archived";
		public static final String UPDATED_AT = "updated_at";
	}

	public static final class Vehicles implements BaseColumns {
		public static final String TABLE_NAME = "vehicles";
		public static final Uri CONTENT_URI =
				Uri.parse("content://" + CreuRojaProvider.CONTENT_NAME + "/" + TABLE_NAME);
		public static final String DEFAULT_ORDER = _ID + " DESC";

		public static final String BRAND = "brand";
		public static final String MODEL = "model";
		public static final String LICENSE = "license";
		public static final String INDICATIVE = "indicative";
		public static final String VEHICLE_TYPE = "vehicle_type";
		public static final String PLACES = "places";
		public static final String NOTES = "notes";
		public static final String OPERATIVE = "operative";
		public static final String CREATED_AT = "created_at";
		public static final String UPDATED_AT = "updated_at";
	}

	public static final class Users implements BaseColumns {
		public static final String TABLE_NAME = "users";
		public static final Uri CONTENT_URI =
				Uri.parse("content://" + CreuRojaProvider.CONTENT_NAME + "/" + TABLE_NAME);
		public static final String DEFAULT_ORDER = _ID + " DESC";

		public static final String NAME = "name";
		public static final String SURNAME = "surname";
		public static final String EMAIL = "email";
		public static final String ROLE = "role";
		public static final String ACTIVE = "active";
		public static final String PHONE = "phone";
		//New (version 2)
		public static final String REMOTE_ID = "remote_id";
		public static final String TYPES = "types";
		public static final String ACCESS_TOKEN = "accessToken";
	}

	public static final class LocationServices implements BaseColumns{
		public static final String TABLE_NAME = "locationservices";
		public static final Uri CONTENT_URI =
				Uri.parse("content://" + CreuRojaProvider.CONTENT_NAME + "/" + TABLE_NAME);
		public static final String DEFAULT_ORDER = _ID + " DESC";

		public static final String LOCATION_ID = "location_id";
		public static final String SERVICE_ID = "service_id";
	}
}
