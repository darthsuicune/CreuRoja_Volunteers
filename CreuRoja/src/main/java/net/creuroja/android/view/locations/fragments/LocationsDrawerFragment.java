package net.creuroja.android.view.locations.fragments;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.creuroja.android.R;
import net.creuroja.android.view.locations.activities.LocationsIndexActivity;
import net.creuroja.android.model.Settings;
import net.creuroja.android.model.locations.Locations;
import net.creuroja.android.model.locations.LocationType;
import net.creuroja.android.view.locations.fragments.maps.MapFragmentHandler;

import java.util.List;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class LocationsDrawerFragment extends Fragment
		implements LocationsHandlerFragment.OnLocationsListUpdated {
	private MapNavigationDrawerCallbacks mapDrawerCallbacks;

	// Helper component that ties the action bar to the navigation drawer.
	private ActionBarDrawerToggle drawerToggle;

	private DrawerLayout drawerLayout;
	private View fragmentContainerView;
	private LocationsIndexActivity.ViewMode currentViewMode;

	private TextView listViewTypeToggle;
	private TextView mapViewTypeToggle;

	TextView normal;
	TextView terrain;
	TextView satellite;
	TextView hybrid;

	private boolean fromSavedInstanceState;

	private SharedPreferences prefs;

	public LocationsDrawerFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

		if (savedInstanceState != null) {
			fromSavedInstanceState = true;
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// Indicate that this fragment would like to influence the set of actions in the action bar.
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_locations_drawer, container, false);
		prepareViewModes(v);
		prepareMapTypes(v);
		return v;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mapDrawerCallbacks = (MapNavigationDrawerCallbacks) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException("Activity must implement MapNavigationDrawerCallbacks.");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mapDrawerCallbacks = null;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Forward the new configuration the drawer toggle component.
		drawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// If the drawer is open, show the global app actions in the action bar. See also
		// showGlobalContextActionBar, which controls the top-left area of the action bar.
		if (drawerLayout != null && isDrawerOpen()) {
			inflater.inflate(R.menu.global, menu);
		}
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (drawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		switch (item.getItemId()) {
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void prepareViewModes(View v) {
		mapViewTypeToggle = (TextView) v.findViewById(R.id.navigation_drawer_section_map);
		listViewTypeToggle = (TextView) v.findViewById(R.id.navigation_drawer_section_list);

		prepareViewMode(mapViewTypeToggle, LocationsIndexActivity.ViewMode.MAP);
		prepareViewMode(listViewTypeToggle, LocationsIndexActivity.ViewMode.LIST);
		if (currentViewMode == null) {
			currentViewMode = LocationsIndexActivity.ViewMode.getViewMode(
					prefs.getInt(Settings.VIEW_MODE,
							LocationsIndexActivity.ViewMode.MAP.getValue()));
		}
		toggleViewMode(
				(currentViewMode == LocationsIndexActivity.ViewMode.LIST) ? listViewTypeToggle :
						mapViewTypeToggle);
	}

	private void prepareViewMode(final TextView v, final LocationsIndexActivity.ViewMode mode) {
		v.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View view) {
				toggleViewMode(view);
				mapDrawerCallbacks.onViewModeChanged(mode);
				currentViewMode = mode;
				prefs.edit().putInt(Settings.VIEW_MODE, mode.getValue()).apply();
				drawerLayout.closeDrawers();
			}
		});
	}

	private void toggleViewMode(View v) {
		if (v != null) {
			changeToggleBackground(mapViewTypeToggle, v == mapViewTypeToggle);
			changeToggleBackground(listViewTypeToggle, v == listViewTypeToggle);
		}
	}

	public void prepareLegendItem(final TextView v, final LocationType type) {
		if (v != null) {
			v.setVisibility(View.VISIBLE);
			v.setOnClickListener(new View.OnClickListener() {
				@Override public void onClick(View view) {
					boolean newState = !type.getViewable(prefs);
					prefs.edit().putBoolean(type.prefs, newState).apply();
					mapDrawerCallbacks.onNavigationLegendItemSelected(type, newState);
					changeToggleBackground(view, newState);
				}
			});
			changeToggleBackground(v, type.getViewable(prefs));
		}
	}

	private void changeToggleBackground(final View v, boolean newState) {
		v.setBackgroundColor(newState ? getResources().getColor(R.color.drawer_item_selected) :
				Color.TRANSPARENT);
	}

	private void prepareMapTypes(View v) {
		normal = (TextView) v.findViewById(R.id.navigation_map_type_normal);
		terrain = (TextView) v.findViewById(R.id.navigation_map_type_terrain);
		satellite = (TextView) v.findViewById(R.id.navigation_map_type_satellite);
		hybrid = (TextView) v.findViewById(R.id.navigation_map_type_hybrid);

		prepareMapType(normal, MapFragmentHandler.MapType.MAP_TYPE_NORMAL);
		prepareMapType(terrain, MapFragmentHandler.MapType.MAP_TYPE_TERRAIN);
		prepareMapType(satellite, MapFragmentHandler.MapType.MAP_TYPE_SATELLITE);
		prepareMapType(hybrid, MapFragmentHandler.MapType.MAP_TYPE_HYBRID);
	}

	public void prepareMapType(final TextView v, final MapFragmentHandler.MapType mapType) {
		if (v != null) {
			v.setOnClickListener(new View.OnClickListener() {
				@Override public void onClick(View view) {
					mapDrawerCallbacks.onNavigationMapTypeSelected(mapType);
					prefs.edit().putInt(Settings.MAP_TYPE, mapType.getValue()).apply();
					toggleMapType();
				}
			});
		}
	}

	private void toggleMapType() {
		if (getActivity() != null) {
			MapFragmentHandler.MapType type = MapFragmentHandler.MapType.fromValue(
					prefs.getInt(Settings.MAP_TYPE,
							MapFragmentHandler.MapType.MAP_TYPE_NORMAL.getValue()));

			changeToggleBackground(normal, MapFragmentHandler.MapType.MAP_TYPE_NORMAL == type);
			changeToggleBackground(terrain, MapFragmentHandler.MapType.MAP_TYPE_TERRAIN == type);
			changeToggleBackground(satellite,
					MapFragmentHandler.MapType.MAP_TYPE_SATELLITE == type);
			changeToggleBackground(hybrid, MapFragmentHandler.MapType.MAP_TYPE_HYBRID == type);
		}
	}

	public boolean isDrawerOpen() {
		return drawerLayout != null && drawerLayout.isDrawerOpen(fragmentContainerView);
	}

	/**
	 * Users of this fragment must call this method to set up the navigation drawer interactions.
	 *
	 * @param fragmentId   The android:id of this fragment in its activity's layout.
	 * @param drawerLayout The DrawerLayout containing this fragment's UI.
	 */
	public void setUp(int fragmentId, DrawerLayout drawerLayout) {

		fragmentContainerView = getActivity().findViewById(fragmentId);
		this.drawerLayout = drawerLayout;

		// set a custom shadow that overlays the main content when the drawer opens
		this.drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

		Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.location_index_toolbar);
		// ActionBarDrawerToggle ties together the the proper interactions
		// between the navigation drawer and the action bar app icon.
		drawerToggle = new MyDrawerToggle(getActivity(), this.drawerLayout, toolbar);

		// If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
		// per the navigation drawer design guidelines.

		boolean userLearnedDrawer = prefs.getBoolean(Settings.PREF_USER_LEARNED_DRAWER, false);
		if (!userLearnedDrawer && !fromSavedInstanceState) {
			this.drawerLayout.openDrawer(fragmentContainerView);
		}

		// Defer code dependent on restoration of previous instance state.
		this.drawerLayout.post(new Runnable() {
			@Override
			public void run() {
				drawerToggle.syncState();
			}
		});

		this.drawerLayout.setDrawerListener(drawerToggle);
		toggleMapType();
	}

	@Override public void onLocationsListUpdated(Locations list) {
		List<LocationType> types = list.getLocationTypes();
		for (LocationType type : types) {
			prepareLegendItem((TextView) getActivity().findViewById(type.legendViewId), type);
		}
	}

	/**
	 * Callbacks interface that all activities using this fragment must implement.
	 */
	public static interface MapNavigationDrawerCallbacks {
		/**
		 * Called when an item in the navigation drawer is selected.
		 */
		void onViewModeChanged(LocationsIndexActivity.ViewMode newMode);

		void onNavigationLegendItemSelected(final LocationType type, final boolean newState);

		void onNavigationMapTypeSelected(final MapFragmentHandler.MapType mapType);
	}

	private class MyDrawerToggle extends ActionBarDrawerToggle {
		Activity activity;

		public MyDrawerToggle(Activity activity, DrawerLayout layout, Toolbar toolbar) {
			super(activity,                    /* host Activity */
					layout,                    /* DrawerLayout object */
					toolbar,             /* nav drawer image to replace 'Up' caret */
					R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
					R.string.navigation_drawer_close);  /* "close drawer" description for accessibility */
			this.activity = activity;
		}

		@Override public void onDrawerClosed(View drawerView) {
			super.onDrawerClosed(drawerView);
			if (!isAdded()) {
				return;
			}
			if (Build.VERSION.SDK_INT >= 11) {
				activity.invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
			}
		}

		@Override public void onDrawerOpened(View drawerView) {
			super.onDrawerOpened(drawerView);
			if (!isAdded()) {
				return;
			}
			// The user manually opened the drawer; store this flag to prevent auto-showing
			// the navigation drawer automatically in the future.
			if (!prefs.getBoolean(Settings.PREF_USER_LEARNED_DRAWER, false)) {
				prefs.edit().putBoolean(Settings.PREF_USER_LEARNED_DRAWER, true).apply();
			}

			if (Build.VERSION.SDK_INT >= 11) {
				getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
			}
		}
	}
}
