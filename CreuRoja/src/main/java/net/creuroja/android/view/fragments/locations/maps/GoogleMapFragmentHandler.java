package net.creuroja.android.view.fragments.locations.maps;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import net.creuroja.android.model.db.CreuRojaContract;
import net.creuroja.android.model.locations.Location;
import net.creuroja.android.model.locations.LocationList;
import net.creuroja.android.model.locations.LocationType;
import net.creuroja.android.model.locations.RailsLocationList;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lapuente on 08.08.14.
 */
public class GoogleMapFragmentHandler implements MapFragmentHandler {
	private static final LatLng DEFAULT_POSITION = new LatLng(41.3958, 2.1739);
	private static final int LOADER_LOCATIONS = 1;

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

	@Override public void drawDirections(Location location) {
		//TODO: Calculate your current position and draw route
		Toast.makeText(mMapFragment.getActivity(), "NOT YET IMPLEMENTED", Toast.LENGTH_LONG).show();
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
			return new CursorLoader(mMapFragment.getActivity(), uri, null, selection,
					selectionArgs, null);
		}

		@Override public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
			mLocationList = new RailsLocationList(data, prefs);
			drawMarkers();
		}

		@Override public void onLoaderReset(Loader<Cursor> loader) {
			//Nothing to do here
		}
	}
}
