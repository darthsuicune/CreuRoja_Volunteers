package net.creuroja.android.view.locations.fragments;

import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import net.creuroja.android.R;
import net.creuroja.android.model.db.CreuRojaContract;
import net.creuroja.android.model.db.CreuRojaProvider;
import net.creuroja.android.model.locations.LocationFactory;
import net.creuroja.android.model.locations.Locations;
import net.creuroja.android.model.webservice.auth.AccountsHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LocationsHandlerFragment.OnLocationsListUpdated} interface
 * to handle interaction events.
 */
public class LocationsHandlerFragment extends Fragment {
	String ARG_SEARCH_QUERY = "searchQuery";
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

	@Override public void onAttach(Context context) {
		super.onAttach(context);
		setHasOptionsMenu(true);
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		getLoaderManager().restartLoader(LOADER_LOCATIONS, null, new LocationListCallbacks());
	}

	@Override public void onDetach() {
		super.onDetach();
		listeners.clear();
		setHasOptionsMenu(false);
	}

	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Do sync on starting the app, don't do it after each device rotation
		if(savedInstanceState == null) {
			performSync();
		}
	}

	public void performSync() {
		//TODO: Check for sync preferences
		Bundle bundle = new Bundle();
		bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
		bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
		ContentResolver.requestSync(new AccountsHelper(getActivity()).getAccount(),
				CreuRojaProvider.CONTENT_NAME, bundle);
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
			args.putString(ARG_SEARCH_QUERY, query);
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

	@Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.locations, menu);
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
			SearchManager searchManager =
					(SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
			SearchView searchView =
					(SearchView) menu.findItem(R.id.action_search).getActionView();
			searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
			searchView.setIconifiedByDefault(true);
			searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
				@Override public boolean onQueryTextSubmit(String s) {
					performSearch(s);
					return true;
				}

				@Override public boolean onQueryTextChange(String s) {
					performSearch(s);
					return true;
				}
			});
		}
	}

	@Override public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_refresh:
				performSync();
				return true;
			case R.id.action_search:
				//Handled upon menu creation
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void performSearch(String query) {
		search(query);
	}

	public interface OnLocationsListUpdated {
		void onLocationsListUpdated(Locations list);
	}

	private class LocationListCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {
		@Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			String selection = null;
			String[] selectionArgs = null;
			if (args != null && args.containsKey(ARG_SEARCH_QUERY)) {
				String query = args.getString(ARG_SEARCH_QUERY);
				selection = CreuRojaContract.Locations.NAME + " LIKE ? OR " +
							CreuRojaContract.Locations.DESCRIPTION + " LIKE ? OR " +
							CreuRojaContract.Locations.ADDRESS + " LIKE ?";
				selectionArgs =
						new String[]{"%" + query + "%", "%" + query + "%", "%" + query + "%"};
			}
			return new CursorLoader(getActivity(), CreuRojaContract.Locations.CONTENT_URI,
					null, selection, selectionArgs, null);
		}

		@Override public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
			locations = LocationFactory.fromCursor(data, prefs);
			notifyListeners();
		}

		@Override public void onLoaderReset(Loader<Cursor> loader) {
			//Nothing to do here
		}
	}
}
