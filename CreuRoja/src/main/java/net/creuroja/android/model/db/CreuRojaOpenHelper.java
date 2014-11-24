package net.creuroja.android.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CreuRojaOpenHelper extends SQLiteOpenHelper {

	private static final String DB_NAME = "CreuRoja";
	private static final int DB_VERSION = 3;

	private static final String CREATE = "CREATE TABLE ";
	private static final String KEY = " INTEGER PRIMARY KEY AUTOINCREMENT, ";

	public CreuRojaOpenHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		if (db.isReadOnly()) {
			db = getWritableDatabase();
		}
		createLocationsTable(db);
		createServicesTable(db);
		createVehiclesTable(db);
		createUsersTable(db);
		createLocationServicesTable(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		switch (oldVersion) {
			case 1:
				removeTable(db, CreuRojaContract.Users.TABLE_NAME);
				createUsersTable(db);
			case 2:
				removeTable(db, CreuRojaContract.Services.TABLE_NAME);
				removeTable(db, CreuRojaContract.Locations.TABLE_NAME);
				createLocationsTable(db);
				createServicesTable(db);
				createLocationServicesTable(db);
				break;
		}

	}

	private void createLocationsTable(SQLiteDatabase db) {
		db.execSQL(CREATE + CreuRojaContract.Locations.TABLE_NAME + " (" +
				   CreuRojaContract.Locations._ID + KEY +
				   CreuRojaContract.Locations.NAME + " TEXT NOT NULL, " +
				   CreuRojaContract.Locations.DESCRIPTION + " TEXT, " +
				   CreuRojaContract.Locations.ADDRESS + " TEXT, " +
				   CreuRojaContract.Locations.PHONE + " TEXT, " +
				   CreuRojaContract.Locations.LATITUD + " DOUBLE NOT NULL, " +
				   CreuRojaContract.Locations.LONGITUD + " DOUBLE NOT NULL, " +
				   CreuRojaContract.Locations.TYPE + " INTEGER NOT NULL, " +
				   CreuRojaContract.Locations.REMOTE_ID + " INTEGER NOT NULL, " +
				   CreuRojaContract.Locations.UPDATED_AT + " DATETIME NOT NULL, " +
				   CreuRojaContract.Locations.ACTIVE + " BOOLEAN)");
	}

	private void createServicesTable(SQLiteDatabase db) {
		db.execSQL(CREATE + CreuRojaContract.Services.TABLE_NAME + " (" +
				   CreuRojaContract.Services._ID + KEY +
				   CreuRojaContract.Services.REMOTE_ID + " INTEGER NOT NULL, " +
				   CreuRojaContract.Services.NAME + " TEXT NOT NULL, " +
				   CreuRojaContract.Services.DESCRIPTION + " TEXT NOT NULL, " +
				   CreuRojaContract.Services.BASETIME + " DATETIME NOT NULL, " +
				   CreuRojaContract.Services.STARTTIME + " DATETIME NOT NULL, " +
				   CreuRojaContract.Services.ENDTIME + " DATETIME NOT NULL, " +
				   CreuRojaContract.Services.CODE + " TEXT, " +
				   CreuRojaContract.Services.ARCHIVED + " INTEGER," +
				   CreuRojaContract.Services.UPDATED_AT + " DATETIME NOT NULL)");
	}

	private void createLocationServicesTable(SQLiteDatabase db) {
		db.execSQL(CREATE + CreuRojaContract.LocationServices.TABLE_NAME + " (" +
				   CreuRojaContract.LocationServices._ID + KEY +
				   CreuRojaContract.LocationServices.LOCATION_ID + " INTEGER NOT NULL, " +
				   CreuRojaContract.LocationServices.SERVICE_ID + " INTEGER NOT NULL)");
	}

	private void createVehiclesTable(SQLiteDatabase db) {
		db.execSQL(CREATE + CreuRojaContract.Vehicles.TABLE_NAME + " (" +
				   CreuRojaContract.Vehicles._ID + KEY +
				   CreuRojaContract.Vehicles.BRAND + " TEXT NOT NULL, " +
				   CreuRojaContract.Vehicles.MODEL + " TEXT NOT NULL, " +
				   CreuRojaContract.Vehicles.LICENSE + " TEXT NOT NULL, " +
				   CreuRojaContract.Vehicles.INDICATIVE + " TEXT NOT NULL, " +
				   CreuRojaContract.Vehicles.VEHICLE_TYPE + " TEXT NOT NULL, " +
				   CreuRojaContract.Vehicles.PLACES + " INTEGER NOT NULL, " +
				   CreuRojaContract.Vehicles.NOTES + " TEXT NOT NULL, " +
				   CreuRojaContract.Vehicles.OPERATIVE + " BOOLEAN NOT NULL, " +
				   CreuRojaContract.Vehicles.CREATED_AT + " DATETIME NOT NULL, " +
				   CreuRojaContract.Vehicles.UPDATED_AT + " DATETIME NOT NULL)");
	}

	private void createUsersTable(SQLiteDatabase db) {
		db.execSQL(CREATE + CreuRojaContract.Users.TABLE_NAME + " (" +
				   CreuRojaContract.Users._ID + KEY +
				   CreuRojaContract.Users.NAME + " TEXT NOT NULL, " +
				   CreuRojaContract.Users.SURNAME + " TEXT NOT NULL, " +
				   CreuRojaContract.Users.EMAIL + " TEXT NOT NULL, " +
				   CreuRojaContract.Users.ROLE + " TEXT NOT NULL, " +
				   CreuRojaContract.Users.ACCESS_TOKEN + " TEXT NOT NULL, " +
				   CreuRojaContract.Users.REMOTE_ID + " INTEGER NOT NULL, " +
				   CreuRojaContract.Users.TYPES + " TEXT NOT NULL, " +
				   CreuRojaContract.Users.PHONE + " TEXT NOT NULL, " +
				   CreuRojaContract.Users.ACTIVE + " BOOLEAN NOT NULL)");
	}

	private void removeTable(SQLiteDatabase db, String tableName) {
		db.execSQL("DROP TABLE " + tableName);
	}
}
