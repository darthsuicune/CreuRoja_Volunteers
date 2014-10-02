package net.creuroja.android.view.fragments.locations;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import net.creuroja.android.model.db.CreuRojaContract;
import net.creuroja.android.model.locations.LocationList;
import net.creuroja.android.model.locations.RailsLocationList;
import net.creuroja.android.view.fragments.locations.maps.MapFragmentHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link net.creuroja.android.view.fragments.locations.LocationsHandlerFragment.OnLocationsListUpdated} interface
 * to handle interaction events.
 */
public class LocationsHandlerFragment extends Fragment {

	private static final int LOADER_LOCATIONS = 1;
	private List<OnLocationsListUpdated> mListeners;
	private SharedPreferences prefs;

	public LocationsHandlerFragment() {
		// Required empty public constructor
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mListeners = new ArrayList<>();
		try {
			mListeners.add((OnLocationsListUpdated) activity);
		} catch (ClassCastException e) {
			throw new ClassCastException(
					activity.toString() + " must implement OnLocationsListUpdated");
		}
		prefs = PreferenceManager.getDefaultSharedPreferences(activity);
		getLoaderManager().restartLoader(LOADER_LOCATIONS, null, new LocationListCallbacks());
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListeners = null;
	}

	public void registerListener(OnLocationsListUpdated listener) {
		mListeners.add(listener);
	}

	public void unregisterListener(OnLocationsListUpdated listener) {
		mListeners.remove(listener);
	}

	public void search(String query) {
		Bundle args = null;
		if (query != null) {
			args = new Bundle();
			args.putString(MapFragmentHandler.ARG_SEARCH_QUERY, query);
		}
		getLoaderManager().restartLoader(LOADER_LOCATIONS, args, new LocationListCallbacks());
	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated
	 * to the activity and potentially other fragments contained in that
	 * activity.
	 * <p/>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnLocationsListUpdated {
		public void onLocationsListUpdated(LocationList list);
	}

	private class LocationListCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {
		@Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			String selection = null;
			String[] selectionArgs = null;
			if (args != null && args.containsKey(MapFragmentHandler.ARG_SEARCH_QUERY)) {
				String query = args.getString(MapFragmentHandler.ARG_SEARCH_QUERY);
				selection = CreuRojaContract.Locations.NAME + " LIKE ? OR " +
							CreuRojaContract.Locations.DESCRIPTION + " LIKE ? OR " +
							CreuRojaContract.Locations.ADDRESS + " LIKE ?";
				selectionArgs = new String[3];
				selectionArgs[0] = "%" + query + "%";
				selectionArgs[1] = "%" + query + "%";
				selectionArgs[2] = "%" + query + "%";
			}
			Uri uri = CreuRojaContract.Locations.CONTENT_LOCATIONS;
			return new CursorLoader(getActivity(), uri, null, selection, selectionArgs, null);
		}

		@Override public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
			LocationList locationsList = new RailsLocationList(data, prefs);
			for (OnLocationsListUpdated listener : mListeners) {
				listener.onLocationsListUpdated(locationsList);
			}
		}

		@Override public void onLoaderReset(Loader<Cursor> loader) {
			//Nothing to do here
		}
	}
}
