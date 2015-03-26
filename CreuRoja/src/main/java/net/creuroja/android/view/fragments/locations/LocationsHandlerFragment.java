package net.creuroja.android.view.fragments.locations;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import net.creuroja.android.model.db.CreuRojaContract;
import net.creuroja.android.model.locations.Locations;
import net.creuroja.android.model.locations.loaders.LocationsLoader;
import net.creuroja.android.view.fragments.locations.maps.MapFragmentHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LocationsHandlerFragment.OnLocationsListUpdated} interface
 * to handle interaction events.
 */
public class LocationsHandlerFragment extends Fragment {

	private static final int LOADER_LOCATIONS = 1;
	private List<OnLocationsListUpdated> listeners = new ArrayList<>();
	private SharedPreferences prefs;
	private Locations locations;

	public LocationsHandlerFragment() {
		// Required empty public constructor
	}

	public static LocationsHandlerFragment newInstance() {
		return new LocationsHandlerFragment();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		prefs = PreferenceManager.getDefaultSharedPreferences(activity);
		getLoaderManager().restartLoader(LOADER_LOCATIONS, null, new LocationListCallbacks());
	}

	@Override
	public void onDetach() {
		super.onDetach();
		listeners.clear();
	}

	public void registerListener(OnLocationsListUpdated listener) {
		listeners.add(listener);
		if (locations != null) {
			listener.onLocationsListUpdated(locations);
		}
	}

	public void search(String query) {
		Bundle args = null;
		if (query != null) {
			args = new Bundle();
			args.putString(MapFragmentHandler.ARG_SEARCH_QUERY, query);
		}
		getLoaderManager().restartLoader(LOADER_LOCATIONS, args, new LocationListCallbacks());
	}

	private void notifyListeners() {
		if (locations != null) {
			for (OnLocationsListUpdated listener : listeners) {
				listener.onLocationsListUpdated(locations);
			}
		}
	}

	public interface OnLocationsListUpdated {
		void onLocationsListUpdated(Locations list);
	}

	private class LocationListCallbacks implements LoaderManager.LoaderCallbacks<Locations> {
		@Override public Loader<Locations> onCreateLoader(int id, Bundle args) {
			String selection = null;
			String[] selectionArgs = null;
			if (args != null && args.containsKey(MapFragmentHandler.ARG_SEARCH_QUERY)) {
				String query = args.getString(MapFragmentHandler.ARG_SEARCH_QUERY);
				selection = CreuRojaContract.Locations.NAME + " LIKE ? OR " +
							CreuRojaContract.Locations.DESCRIPTION + " LIKE ? OR " +
							CreuRojaContract.Locations.ADDRESS + " LIKE ?";
				selectionArgs =
						new String[]{"%" + query + "%", "%" + query + "%", "%" + query + "%"};
			}
			return new LocationsLoader(getActivity(), CreuRojaContract.Locations.CONTENT_URI,
					null, selection, selectionArgs, null, prefs);
		}

		@Override public void onLoadFinished(Loader<Locations> loader, Locations data) {
			locations = data;
			notifyListeners();
		}

		@Override public void onLoaderReset(Loader<Locations> loader) {
			//Nothing to do here
		}
	}
}
