package net.creuroja.android.view.fragments.locations.maps;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import net.creuroja.android.R;
import net.creuroja.android.model.locations.Directions;
import net.creuroja.android.model.locations.Location;
import net.creuroja.android.model.locations.LocationList;
import net.creuroja.android.model.locations.LocationType;
import net.creuroja.android.model.locations.loaders.DirectionsLoader;
import net.creuroja.android.view.fragments.locations.LocationsHandlerFragment;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lapuente on 08.08.14.
 */
public class GoogleMapFragment extends SupportMapFragment
		implements MapFragmentHandler, LocationsHandlerFragment.OnLocationsListUpdated {
	private static final LatLng DEFAULT_POSITION = new LatLng(41.3958, 2.1739);
	private static final int LOADER_DIRECTIONS = 1;

	GoogleMap map;

	MapInteractionListener listener;

	LocationList mLocationList;
	Map<Marker, Location> mCurrentMarkers = new HashMap<>();
	SharedPreferences prefs;

	public GoogleMapFragment() {
		super();
	}

	@Override public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			listener = (MapInteractionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(
					activity.toString() + " must implement MapInteractionListener");
		}
		prefs = PreferenceManager.getDefaultSharedPreferences(activity);
	}

	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
									   Bundle savedInstanceState) {
		View v = super.onCreateView(inflater, container, savedInstanceState);
		initMap();
		return v;
	}

	public void initMap() {
		map = getMap();
		UiSettings settings = map.getUiSettings();
		settings.setCompassEnabled(false);
		settings.setRotateGesturesEnabled(true);
		settings.setZoomControlsEnabled(false);
		settings.setZoomGesturesEnabled(true);
		settings.setScrollGesturesEnabled(true);
		settings.setTiltGesturesEnabled(true);
		CameraPosition.Builder cameraBuilder = new CameraPosition.Builder();
		cameraBuilder.target(DEFAULT_POSITION).zoom(12);
		map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraBuilder.build()));
		if (mLocationList != null) {
			drawMarkers();
		}
	}

	@Override public void setMapType(MapType mapType) {
		map.setMapType(getMapType(mapType));
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
		if (mLocationList != null) {
			map.clear();
			for (Location location : mLocationList.getLocations()) {
				drawMarker(location);
			}
		}
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
		drawMarkers();
		PolylineOptions directionsOptions = new PolylineOptions();
		directionsOptions.addAll(directions.getPoints());
		directionsOptions.color(getResources().getColor(R.color.cruz_roja_main_red));
		map.addPolyline(directionsOptions);
	}

	@Override public void toggleLocations(LocationType type, boolean newState) {
		for (Marker marker : mCurrentMarkers.keySet()) {
			if (mCurrentMarkers.get(marker).mType == type) {
				marker.setVisible(newState);
			}
		}
	}

	@Override public boolean locate(android.location.Location location) {
		if (map != null) {
			map.animateCamera(CameraUpdateFactory
					.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
			return true;
		}
		return false;
	}

	@Override
	public LocationsHandlerFragment.OnLocationsListUpdated getOnLocationsListUpdatedListener() {
		return this;
	}

	@Override public void onLocationsListUpdated(LocationList list) {
		mLocationList = list;
		if(this.isAdded()) {
			drawMarkers();
		}
	}

	@Override public Fragment getFragment() {
		return this;
	}

	@Override public void removeDirections() {
		drawMarkers();
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
