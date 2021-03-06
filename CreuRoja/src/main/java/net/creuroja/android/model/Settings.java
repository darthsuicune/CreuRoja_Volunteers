package net.creuroja.android.model;

import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;

import net.creuroja.android.model.db.CreuRojaContract;
import net.creuroja.android.model.webservice.auth.AccountUtils;

/**
 * Created by denis on 14.06.14.
 */
public class Settings {
	public final static String VIEW_MODE = "view_mode";
	public final static String MAP_TYPE = "map_type";
	public static final String LAST_UPDATE_TIME = "last_update_time";
	public static final String SHOW_CUAP = "show_cuap";
	public static final String SHOW_BRAVO = "show_bravo";
	public static final String SHOW_ASSEMBLY = "show_assembly";
	public static final String SHOW_ADAPTED = "show_adapted";
	public static final String SHOW_GAS_STATION = "show_gas_station";
	public static final String SHOW_HOSPITAL = "show_hospital";
	public static final String SHOW_SEA_SERVICE = "show_sea_service";
	public static final String SHOW_NOSTRUM = "show_nostrum";
	public static final String SHOW_SEA_BASE = "show_sea_base";
	public static final String SHOW_TERRESTRIAL = "show_terrestrial";
	/**
	 * Per the design guidelines, you should show the drawer on launch until the user manually
	 * expands it. This shared preference tracks this.
	 */
	public static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

	public static boolean isConnected(Context context) {
		ConnectivityManager connectivity =
				(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		return (connectivity != null && connectivity.getActiveNetworkInfo() != null &&
				connectivity.getActiveNetworkInfo().isAvailable() &&
				connectivity.getActiveNetworkInfo().isConnected());
	}

	public static void clean(SharedPreferences prefs, ContentResolver cr) {
		prefs.edit().clear().apply();
		cr.delete(CreuRojaContract.Locations.CONTENT_URI, null, null);
		cr.delete(CreuRojaContract.Services.CONTENT_URI, null, null);
		cr.delete(CreuRojaContract.Vehicles.CONTENT_URI, null, null);
		cr.delete(CreuRojaContract.Users.CONTENT_URI, null, null);
	}

	public static void removeAccount(AccountManager accountManager, String accessToken) {
		accountManager.invalidateAuthToken(AccountUtils.ACCOUNT_TYPE, accessToken);
	}
}
