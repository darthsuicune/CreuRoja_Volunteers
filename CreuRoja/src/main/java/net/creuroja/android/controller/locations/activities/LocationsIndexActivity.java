package net.creuroja.android.controller.locations.activities;

import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import net.creuroja.android.R;
import net.creuroja.android.controller.general.SettingsActivity;
import net.creuroja.android.controller.locations.LocationsListListener;
import net.creuroja.android.model.Settings;
import net.creuroja.android.model.db.CreuRojaProvider;
import net.creuroja.android.model.locations.Location;
import net.creuroja.android.model.locations.LocationType;
import net.creuroja.android.model.webservice.auth.AccountUtils;
import net.creuroja.android.model.webservice.auth.AccountUtils.LoginManager;
import net.creuroja.android.view.fragments.locations.LocationDetailFragment;
import net.creuroja.android.view.fragments.locations.LocationListFragment;
import net.creuroja.android.view.fragments.locations.NavigationDrawerFragment;
import net.creuroja.android.view.fragments.locations.OnDirectionsRequestedListener;
import net.creuroja.android.view.fragments.locations.maps.LocationCardFragment;
import net.creuroja.android.view.fragments.locations.maps.MapFragmentHandler;
import net.creuroja.android.view.fragments.locations.maps.MapFragmentHandlerFactory;

import static net.creuroja.android.view.fragments.locations.NavigationDrawerFragment.MapNavigationDrawerCallbacks;
import static net.creuroja.android.view.fragments.locations.maps.GoogleMapFragmentHandler.MapInteractionListener;
import static net.creuroja.android.view.fragments.locations.maps.LocationCardFragment.OnLocationCardInteractionListener;

public class LocationsIndexActivity extends ActionBarActivity
		implements LoginManager, MapNavigationDrawerCallbacks, LocationsListListener,
		OnLocationCardInteractionListener, MapInteractionListener, OnDirectionsRequestedListener,
		LocationDetailFragment.OnLocationDetailsInteractionListener,
		SearchView.OnQueryTextListener {
	private static final String TAG_MAP = "CreuRojaMap";
	private static final String TAG_LIST = "CreuRojaLocationList";
	private static final String TAG_CARD = "CreuRojaLocationCard";
	private static final String TAG_DETAILS = "CreuRojaDetail";

	// This are the fragments that the activity handles
	private NavigationDrawerFragment mNavigationDrawerFragment;
	private MapFragmentHandler mapFragmentHandler;
	private LocationListFragment listFragment;
	private LocationCardFragment cardFragment;

	// Used to store the last screen title. For use in {@link #restoreActionBar()}.
	private CharSequence mTitle;
	private SharedPreferences prefs;

	private ViewMode currentViewMode;

	// Callbacks for when the auth token is returned
	@Override
	public void successfulLogin() {
		if (currentViewMode == null) {
			String preferredMode =
					prefs.getString(Settings.LOCATIONS_INDEX_TYPE, ViewMode.MAP.toString());
			currentViewMode = ViewMode.getViewMode(preferredMode);
		}
		startUi();
		bootSync();
	}

	@Override
	public void failedLogin() {
	}

	private void startUi() {
		setContentView(R.layout.activity_locations);
		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment
				.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));

		setMainFragment();
	}

	@Override public void onViewModeChanged(ViewMode newViewMode) {
		if (newViewMode == currentViewMode) {
			return;
		}
		switch (newViewMode) {
			case LIST:
				currentViewMode = ViewMode.LIST;
				break;
			case MAP:
				currentViewMode = ViewMode.MAP;
				break;
			default:
				//Nothing to do here
				break;
		}
		prefs.edit().putString(Settings.LOCATIONS_INDEX_TYPE, currentViewMode.toString()).apply();
		setMainFragment();
	}

	private void setMainFragment() {
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		switch (currentViewMode) {
			case LIST:
				if (listFragment == null) {
					listFragment =
							(LocationListFragment) fragmentManager.findFragmentByTag(TAG_LIST);
					if (listFragment == null) {
						listFragment = LocationListFragment.newInstance();
					}
				}
				if (cardFragment != null && cardFragment.isVisible()) {
					transaction.remove(cardFragment);
				}
				transaction.replace(R.id.locations_container, listFragment, TAG_LIST);
				break;
			case MAP:
			default:
				if (mapFragmentHandler == null) {
					Fragment fragment = fragmentManager.findFragmentByTag(TAG_MAP);
					mapFragmentHandler =
							MapFragmentHandlerFactory.getHandler(fragment, this, prefs);
				}
				transaction.replace(R.id.locations_container, mapFragmentHandler.getFragment(),
						TAG_MAP);
				break;
		}
		transaction.commit();
		fragmentManager.executePendingTransactions();
		if (currentViewMode == ViewMode.MAP) {
			mapFragmentHandler.setUp();
		}
	}

	@Override public void onLocationListItemSelected(Location location) {
		openLocationDetails(location);
	}

	@Override public void onCardDetailsRequested(Location location) {
		openLocationDetails(location);
	}

	@Override public void onDirectionsRequested(Location location) {
		mapFragmentHandler.drawDirections(location);
	}

	@Override public void onCardCloseRequested() {
		getSupportFragmentManager().beginTransaction().remove(cardFragment).commit();
	}

	@Override public void onNavigationLegendItemSelected(LocationType type, boolean newState) {
		if (ViewMode.LIST == currentViewMode) {
			listFragment.toggleLocations(type, newState);
		} else {
			mapFragmentHandler.toggleLocations(type, newState);
		}

	}

	@Override public void onNavigationMapTypeSelected(MapFragmentHandler.MapType mapType) {
		if (ViewMode.MAP == currentViewMode) {
			mapFragmentHandler.setMapType(mapType);
		}
	}

	@Override public void onLocationClicked(Location location) {
		FragmentManager manager = getSupportFragmentManager();
		if (cardFragment == null) {
			cardFragment = (LocationCardFragment) manager.findFragmentByTag(TAG_CARD);
			if (cardFragment == null) {
				cardFragment = LocationCardFragment.newInstance(location);
			}
			manager.beginTransaction().add(R.id.location_card_container, cardFragment, TAG_CARD)
					.commit();
		}
		cardFragment.setLocation(location);
	}

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		boolean alreadyCreated = (savedInstanceState != null);
		if (alreadyCreated && AccountUtils.getAccount(this) != null) {
			successfulLogin();
		} else {
			AccountUtils.validateLogin(this, this);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (mNavigationDrawerFragment != null && !mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen if the drawer is not
			// showing. Otherwise, let the drawer decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.locations, menu);
			if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
				SearchManager searchManager =
						(SearchManager) getSystemService(Context.SEARCH_SERVICE);
				SearchView searchView =
						(SearchView) menu.findItem(R.id.action_search).getActionView();
				// Assumes current activity is the searchable activity
				searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
				searchView.setIconifiedByDefault(true);
				searchView.setOnQueryTextListener(this);
			}
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
			case R.id.action_settings:
				openSettings();
				return true;
			case R.id.action_refresh:
				performSync();
				return true;
			case R.id.action_search:
				return true;
			case R.id.action_locate:
				//TODO: Change to true after implementation
				locate();
				return super.onOptionsItemSelected(item);
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void locate() {
		//TODO: implement
	}

	private void bootSync() {
		//TODO: Check for sync preferences
		performSync();
	}

	private void performSync() {
		Bundle bundle = new Bundle();
		bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
		bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
		ContentResolver.requestSync(AccountUtils.getAccount(getApplicationContext()),
				CreuRojaProvider.CONTENT_NAME, bundle);
	}

	private void openSettings() {
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivity(intent);
	}

	private void openLocationDetails(Location location) {
		int orientation = getResources().getConfiguration().orientation;
		//Map should be full screen on landscape. Hide the details container for it.
		if (Configuration.ORIENTATION_LANDSCAPE == orientation) {
			findViewById(R.id.location_details_container)
					.setVisibility((currentViewMode == ViewMode.MAP) ? View.GONE : View.VISIBLE);
		}
		//Viewing the list in portrait, or the map, a new activity must be launched
		if (currentViewMode == ViewMode.MAP || orientation == Configuration.ORIENTATION_PORTRAIT) {
			Intent intent = new Intent(this, LocationDetailsActivity.class);
			intent.putExtra(LocationDetailsActivity.EXTRA_LOCATION_ID, location.mRemoteId);
			startActivity(intent);
		} else {
			FragmentManager manager = getSupportFragmentManager();
			LocationDetailFragment fragment =
					(LocationDetailFragment) manager.findFragmentByTag(TAG_DETAILS);
			if (fragment == null) {
				fragment = LocationDetailFragment.newInstance(location.mRemoteId);
				manager.beginTransaction()
						.add(R.id.location_details_container, fragment, TAG_DETAILS).commit();
			} else {
				fragment.setLocation(location);
			}
		}
	}

	@Override public boolean onQueryTextSubmit(String s) {
		performSearch(s);
		return true;
	}

	@Override public boolean onQueryTextChange(String s) {
		performSearch(s);
		return true;
	}

	private void performSearch(String query) {
		if (mapFragmentHandler != null && mapFragmentHandler.getFragment().isAdded()) {
			mapFragmentHandler.search(query);
		}
		if (listFragment != null && listFragment.isAdded()) {
			listFragment.search(query);
		}
	}

	public enum ViewMode {
		MAP(0), LIST(1);

		final int mValue;

		ViewMode(final int value) {
			mValue = value;
		}

		public static ViewMode getViewMode(String mode) {
			if (mode.equals(LIST.toString())) {
				return LIST;
			} else {
				return MAP;
			}
		}

		public int getValue() {
			return mValue;
		}
	}
}
