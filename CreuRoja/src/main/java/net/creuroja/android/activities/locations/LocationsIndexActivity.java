package net.creuroja.android.activities.locations;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.location.LocationManager;
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
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationClient;

import net.creuroja.android.R;
import net.creuroja.android.activities.general.SettingsActivity;
import net.creuroja.android.activities.users.UserProfileActivity;
import net.creuroja.android.model.Settings;
import net.creuroja.android.model.db.CreuRojaProvider;
import net.creuroja.android.model.locations.Location;
import net.creuroja.android.model.locations.LocationList;
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

import static net.creuroja.android.view.fragments.locations.LocationDetailFragment.OnLocationDetailsInteractionListener;
import static net.creuroja.android.view.fragments.locations.LocationDetailFragment.newInstance;
import static net.creuroja.android.view.fragments.locations.NavigationDrawerFragment.MapNavigationDrawerCallbacks;
import static net.creuroja.android.view.fragments.locations.maps.GoogleMapFragmentHandler.MapInteractionListener;
import static net.creuroja.android.view.fragments.locations.maps.LocationCardFragment.OnLocationCardInteractionListener;

public class LocationsIndexActivity extends ActionBarActivity
		implements LoginManager, MapNavigationDrawerCallbacks,
		LocationListFragment.LocationsListListener, OnLocationCardInteractionListener,
		MapInteractionListener, OnDirectionsRequestedListener,
		OnLocationDetailsInteractionListener {
	private static final String TAG_MAP = "CreuRojaMap";
	private static final String TAG_LIST = "CreuRojaLocationList";
	private static final String TAG_CARD = "CreuRojaLocationCard";
	private static final String TAG_DETAILS = "CreuRojaDetail";

	// This are the fragments that the activity handles
	private NavigationDrawerFragment mNavigationDrawerFragment;
	private MapFragmentHandler mapFragmentHandler;
	private LocationListFragment listFragment;
	private LocationCardFragment cardFragment;

	private LocationClient mLocationClient;

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

		mLocationClient =
				new LocationClient(this, new GooglePlayServicesClient.ConnectionCallbacks() {
					@Override public void onConnected(Bundle bundle) {
					}

					@Override public void onDisconnected() {
					}
				}, new GoogleApiClient.OnConnectionFailedListener() {
					@Override public void onConnectionFailed(ConnectionResult connectionResult) {
					}
				});
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
		if (Configuration.ORIENTATION_LANDSCAPE == getResources().getConfiguration().orientation) {
			//noinspection ResourceType
			findViewById(R.id.location_details_container)
					.setVisibility(currentViewMode.getDetailsBlockVisibility());
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

	@Override public void onDirectionsRequested(Location destination) {
		if (ViewMode.LIST == currentViewMode) {
			onViewModeChanged(ViewMode.MAP);
		}
		android.location.Location origin = getCurrentLocation();
		mapFragmentHandler.getDirections(origin, destination);
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

	public void onLocationListUpdated(LocationList list) {
		if (mNavigationDrawerFragment != null) {
			mNavigationDrawerFragment.prepareLegendItems();
		}
		if (mapFragmentHandler != null) {
			mapFragmentHandler.updateList(list);
		}
		if (listFragment != null) {
			listFragment.updateList(list);
		}
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
				searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
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
			case R.id.action_view_profile:
				viewProfile();
				return true;
			case R.id.action_settings:
				openSettings();
				return true;
			case R.id.action_refresh:
				performSync();
				return true;
			case R.id.action_search:
				//Handled upon menu creation
				return true;
			case R.id.action_locate:
				locate();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void viewProfile() {
		Intent intent = new Intent(this, UserProfileActivity.class);
		startActivity(intent);
	}

	private void locate() {
		android.location.Location location = getCurrentLocation();
		if (location != null) {
			if (currentViewMode == ViewMode.MAP) {
				mapFragmentHandler.locate(location);
			}
		}
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

	private android.location.Location getCurrentLocation() {
		if (areLocationServicesEnabled()) {
			if (mLocationClient.getLastLocation() != null) {
				return mLocationClient.getLastLocation();
			} else {
				Toast.makeText(getApplicationContext(), R.string.locating, Toast.LENGTH_SHORT)
						.show();
			}
		} else {
			showLocationSettings();
		}
		return null;
	}

	private boolean areLocationServicesEnabled() {
		LocationManager lm =
				(LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
		return (lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
				lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
	}

	private void openLocationDetails(Location location) {
		//Viewing the list in portrait, or the map, a new activity must be launched
		if (currentViewMode == ViewMode.MAP ||
			getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			Intent intent = new Intent(this, LocationDetailsActivity.class);
			intent.putExtra(LocationDetailsActivity.EXTRA_LOCATION_ID, location.mRemoteId);
			startActivity(intent);
		} else {
			FragmentManager manager = getSupportFragmentManager();
			LocationDetailFragment fragment =
					(LocationDetailFragment) manager.findFragmentByTag(TAG_DETAILS);
			if (fragment == null) {
				fragment = newInstance(location.mRemoteId);
				manager.beginTransaction()
						.add(R.id.location_details_container, fragment, TAG_DETAILS).commit();
			} else {
				fragment.setLocation(location);
			}
		}
	}

	private void showLocationSettings() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.location_disabled_title);
		builder.setMessage(R.string.location_disabled_message);
		builder.setPositiveButton(R.string.open_location_settings,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialogInterface, int i) {
						startActivity(new Intent(
								android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
					}
				});
		builder.setNegativeButton(R.string.cancel, null);
		builder.create().show();
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

		public int getDetailsBlockVisibility() {
			switch (this) {
				case LIST:
					return View.VISIBLE;
				case MAP:
				default:
					return View.GONE;
			}
		}
	}
}
