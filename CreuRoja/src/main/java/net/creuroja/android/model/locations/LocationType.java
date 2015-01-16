package net.creuroja.android.model.locations;

import android.content.SharedPreferences;
import android.database.Cursor;

import net.creuroja.android.R;
import net.creuroja.android.model.Settings;
import net.creuroja.android.model.db.CreuRojaContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lapuente on 08.08.14.
 */

public enum LocationType {
	NONE(0, R.string.marker_type_not_available, 0, ""),
	ADAPTED(R.id.navigation_legend_adaptadas, R.string.marker_type_adaptadas, R.drawable.adaptadas,
			Settings.SHOW_ADAPTED),
	ASSEMBLY(R.id.navigation_legend_asamblea, R.string.marker_type_asamblea, R.drawable.asamblea,
			Settings.SHOW_ASSEMBLY),
	BRAVO(R.id.navigation_legend_bravo, R.string.marker_type_bravo, R.drawable.bravo,
			Settings.SHOW_BRAVO),
	CUAP(R.id.navigation_legend_cuap, R.string.marker_type_cuap, R.drawable.cuap,
			Settings.SHOW_CUAP),
	GAS_STATION(R.id.navigation_legend_gasolinera, R.string.marker_type_gasolinera,
			R.drawable.gasolinera, Settings.SHOW_GAS_STATION),
	HOSPITAL(R.id.navigation_legend_hospital, R.string.marker_type_hospital, R.drawable.hospital,
			Settings.SHOW_HOSPITAL),
	SEA_SERVICE(R.id.navigation_legend_maritimo, R.string.marker_type_maritimo, R.drawable.maritimo,
			Settings.SHOW_SEA_SERVICE),
	NOSTRUM(R.id.navigation_legend_nostrum, R.string.marker_type_nostrum, R.drawable.nostrum,
			Settings.SHOW_NOSTRUM),
	SEA_BASE(R.id.navigation_legend_salvamento, R.string.marker_type_salvamento,
			R.drawable.salvamento, Settings.SHOW_SEA_BASE),
	TERRESTRIAL(R.id.navigation_legend_terrestre, R.string.marker_type_terrestre,
			R.drawable.terrestre, Settings.SHOW_TERRESTRIAL);

	public int legendViewId;
	public int nameString;
	public int icon;
	public String prefs;
	public String reference;

	LocationType(int legendId, int nameString, int icon, String prefs) {
		legendViewId = legendId;
		this.nameString = nameString;
		this.icon = icon;
		this.prefs = prefs;
	}

	public static LocationType getType(int resId) {
		switch (resId) {
			case R.string.marker_type_adaptadas:
				return ADAPTED;
			case R.string.marker_type_asamblea:
				return ASSEMBLY;
			case R.string.marker_type_bravo:
				return BRAVO;
			case R.string.marker_type_cuap:
				return CUAP;
			case R.string.marker_type_gasolinera:
				return GAS_STATION;
			case R.string.marker_type_hospital:
				return HOSPITAL;
			case R.string.marker_type_maritimo:
				return SEA_SERVICE;
			case R.string.marker_type_nostrum:
				return NOSTRUM;
			case R.string.marker_type_salvamento:
				return SEA_BASE;
			case R.string.marker_type_terrestre:
				return TERRESTRIAL;
			default:
				return NONE;
		}
	}

	public static LocationType getType(String string) {
		switch (string.toLowerCase()) {
			case LocationFactory.sAdapted:
				return ADAPTED;
			case LocationFactory.sAssembly:
				return ASSEMBLY;
			case LocationFactory.sBravo:
				return BRAVO;
			case LocationFactory.sCuap:
				return CUAP;
			case LocationFactory.sGasStation:
				return GAS_STATION;
			case LocationFactory.sHospital:
				return HOSPITAL;
			case LocationFactory.sSeaService:
				return SEA_SERVICE;
			case LocationFactory.sNostrum:
				return NOSTRUM;
			case LocationFactory.sSeaBase:
				return SEA_BASE;
			case LocationFactory.sTerrestrial:
				return TERRESTRIAL;
			default:
				return NONE;
		}
	}

	public static List<LocationType> getCurrentTypes(Cursor cursor) {
		List<LocationType> currentTypes = new ArrayList<>();
		if (cursor != null && cursor.moveToFirst()) {
			do {
				LocationType type = getType(cursor
						.getInt(cursor.getColumnIndex(CreuRojaContract.Locations.TYPE)));
				if (!currentTypes.contains(type)) {
					currentTypes.add(type);
				}
			} while (cursor.moveToNext());
		}
		return currentTypes;
	}

	public boolean getViewable(SharedPreferences prefs) {
		return prefs.getBoolean(this.prefs, true);
	}
}