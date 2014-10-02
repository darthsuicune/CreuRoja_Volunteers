package net.creuroja.android.view.fragments.locations.maps;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import net.creuroja.android.activities.locations.LocationsIndexActivity;
import net.creuroja.android.model.db.CreuRojaContract;
import net.creuroja.android.model.locations.Directions;
import net.creuroja.android.model.locations.Location;
import net.creuroja.android.model.locations.LocationList;
import net.creuroja.android.model.locations.LocationType;
import net.creuroja.android.model.locations.RailsLocationList;
import net.creuroja.android.model.locations.loaders.DirectionsLoader;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lapuente on 08.08.14.
 */
public class GoogleMapFragmentHandler implements MapFragmentHandler {
	private static final LatLng DEFAULT_POSITION = new LatLng(41.3958, 2.1739);
	private static final int LOADER_LOCATIONS = 1;
	private static final int LOADER_DIRECTIONS = 2;

	SupportMapFragment mMapFragment;
	GoogleMap map;

	MapInteractionListener listener;

	LocationList mLocationList;
	Map<Marker, Location> mCurrentMarkers;
	SharedPreferences prefs;

	public GoogleMapFragmentHandler(Fragment fragment, MapInteractionListener listener,
									SharedPreferences prefs) {
		this.mMapFragment = (SupportMapFragment) fragment;
		this.listener = listener;
		this.prefs = prefs;
		mCurrentMarkers = new HashMap<>();
	}

	public static GoogleMapOptions getMapOptions() {
		GoogleMapOptions options = new GoogleMapOptions();
		CameraPosition.Builder cameraBuilder = new CameraPosition.Builder();
		cameraBuilder.target(DEFAULT_POSITION).zoom(12);
		options.compassEnabled(false).rotateGesturesEnabled(true).zoomControlsEnabled(false)
				.zoomGesturesEnabled(true).scrollGesturesEnabled(true).tiltGesturesEnabled(true)
				.camera(cameraBuilder.build());
		return options;
	}

	@Override public void setMapType(MapType mapType) {
		if (setUpMap()) {
			map.setMapType(getMapType(mapType));
		}
	}

	private int getMapType(MapType mapType) {
		switch (mapType) {
			case MAP_TYPE_TERRAIN:
				return GoogleMap.MAP_TYPE_TERRAIN;
			case MAP_TYPE_SATELLITE:
				return GoogleMap.MAP_TYPE_SATELLITE;
			case MAP_TYPE_HYBRID:
				return GoogleMap.MAP_TYPE_HYBRID;
			case MAP_TYPE_NORMAL:
			default:
				return GoogleMap.MAP_TYPE_NORMAL;
		}
	}

	public void drawMarkers() {
		if (setUpMap() && mLocationList != null) {
			map.clear();
			for (Location location : mLocationList.getLocations()) {
				drawMarker(location);
			}
		}
	}

	private boolean setUpMap() {
		if (map == null) {
			map = mMapFragment.getMap();
		}
		return map != null;
	}

	@Override public void getDirections(android.location.Location origin, Location destination) {
		Bundle bundle = new Bundle();
		bundle.putDouble(DirectionsLoader.ARG_ORIG_LAT, origin.getLatitude());
		bundle.putDouble(DirectionsLoader.ARG_ORIG_LONG, origin.getLongitude());
		bundle.putDouble(DirectionsLoader.ARG_DEST_LAT, destination.mLatitude);
		bundle.putDouble(DirectionsLoader.ARG_DEST_LONG, destination.mLongitude);
		getFragment().getLoaderManager()
				.restartLoader(LOADER_DIRECTIONS, bundle, new DirectionsCallbacks());
	}

	private void drawDirections(Directions directions) {
		PolylineOptions directionsOptions = new PolylineOptions();
		directionsOptions.addAll(directions.getPoints());
		directionsOptions.color(Color.parseColor("#CC0000"));
		map.addPolyline(directionsOptions);
	}

	@Override public void toggleLocations(LocationType type, boolean newState) {
		for (Marker marker : mCurrentMarkers.keySet()) {
			if (mCurrentMarkers.get(marker).mType == type) {
				marker.setVisible(newState);
			}
		}
	}

	@Override public void search(String query) {
		Bundle args = null;
		if (query != null) {
			args = new Bundle();
			args.putString(MapFragmentHandler.ARG_SEARCH_QUERY, query);
		}
		getFragment().getLoaderManager()
				.restartLoader(LOADER_LOCATIONS, args, new LocationListCallbacks());
	}

	@Override public boolean locate(android.location.Location location) {
		if (map != null) {
			map.animateCamera(CameraUpdateFactory
					.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
			return true;
		}
		return false;
	}

	@Override public void updateList(LocationList list) {
		mLocationList = list;
		drawMarkers();
	}

	@Override public void setUp() {
		search(null);
	}

	@Override public Fragment getFragment() {
		return mMapFragment;
	}

	private void drawMarker(Location location) {
		MarkerOptions options = new MarkerOptions();
		options.position(new LatLng(location.mLatitude, location.mLongitude));
		options.icon(BitmapDescriptorFactory.fromResource(location.mType.mIcon));
		mCurrentMarkers.put(map.addMarker(options), location);
		map.setInfoWindowAdapter(new OnLocationClickAdapter());
	}

	public interface MapInteractionListener {
		public void onLocationClicked(Location location);
	}

	private class OnLocationClickAdapter implements GoogleMap.InfoWindowAdapter {
		@Override public View getInfoWindow(Marker marker) {
			return null;
		}

		@Override public View getInfoContents(Marker marker) {
			listener.onLocationClicked(mCurrentMarkers.get(marker));
			return null;
		}
	}

	private class LocationListCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {
		@Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			CursorLoader loader = new CursorLoader(getFragment().getActivity());
			loader.setUri(CreuRojaContract.Locations.CONTENT_LOCATIONS);
			if (args != null && args.containsKey(MapFragmentHandler.ARG_SEARCH_QUERY)) {
				String query = args.getString(MapFragmentHandler.ARG_SEARCH_QUERY);
				loader.setSelection(CreuRojaContract.Locations.NAME + " LIKE ? OR " +
									CreuRojaContract.Locations.DESCRIPTION + " LIKE ? OR " +
									CreuRojaContract.Locations.ADDRESS + " LIKE ?");
				loader.setSelectionArgs(
						new String[]{"%" + query + "%", "%" + query + "%", "%" + query + "%"});
			}
			return loader;
		}

		@Override public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
			mLocationList = new RailsLocationList(data, prefs);
			((LocationsIndexActivity) getFragment().getActivity())
					.onLocationListUpdated(mLocationList);
		}

		@Override public void onLoaderReset(Loader<Cursor> loader) {
			//Nothing to do here
		}
	}

	private class DirectionsCallbacks implements LoaderManager.LoaderCallbacks<Directions> {

		@Override public Loader<Directions> onCreateLoader(int id, Bundle args) {
			return new DirectionsLoader(getFragment().getActivity(), args);
		}

		@Override public void onLoadFinished(Loader<Directions> directionsLoader,
											 Directions directions) {
			drawDirections(directions);
		}

		@Override public void onLoaderReset(Loader<Directions> directionsLoader) {

		}
	}
}
