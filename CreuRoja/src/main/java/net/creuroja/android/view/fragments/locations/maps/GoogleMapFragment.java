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
import android.widget.Toast;

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
import net.creuroja.android.model.directions.Directions;
import net.creuroja.android.model.directions.loader.DirectionsLoader;
import net.creuroja.android.model.locations.Location;
import net.creuroja.android.model.locations.LocationType;
import net.creuroja.android.model.locations.Locations;
import net.creuroja.android.view.fragments.locations.LocationsHandlerFragment;

import java.util.HashMap;
import java.util.Map;

public class GoogleMapFragment extends SupportMapFragment
        implements MapFragmentHandler, LocationsHandlerFragment.OnLocationsListUpdated {
    private static final double DEFAULT_LATITUDE = 41.3958;
    private static final double DEFAULT_LONGITUDE = 2.1739;
    private static final int DEFAULT_ZOOM = 12;
    private static final int LOADER_DIRECTIONS = 1;
    private static final String ARG_ZOOM = "zoom";
    private static final String ARG_LATITUDE = "latitude";
    private static final String ARG_LONGITUDE = "longitude";

    GoogleMap map;
    private Directions directions;
    MapInteractionListener listener;

    Locations locations;
    Map<Marker, Location> currentMarkers = new HashMap<>();
    SharedPreferences prefs;
    DirectionsDrawnHandler directionsListener;

    public GoogleMapFragment() {
        super();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (MapInteractionListener) activity;
            directionsListener = (DirectionsDrawnHandler) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    activity.toString() + " must implement MapInteractionListener");
        }
        prefs = PreferenceManager.getDefaultSharedPreferences(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        initMap();
        setCameraPosition(savedInstanceState);
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
        if (locations != null) {
            drawMarkers();
        }
    }

    private void setCameraPosition(Bundle state) {
        CameraPosition.Builder cameraBuilder = new CameraPosition.Builder();
        float zoom = (state == null) ? DEFAULT_ZOOM : state.getFloat(ARG_ZOOM);
        double latitude = (state == null) ? DEFAULT_LATITUDE : state.getDouble(ARG_LATITUDE);
        double longitude = (state == null) ? DEFAULT_LONGITUDE : state.getDouble(ARG_LONGITUDE);
        LatLng position = new LatLng(latitude, longitude);
        cameraBuilder.target(position).zoom(zoom);
        map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraBuilder.build()));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        CameraPosition position = map.getCameraPosition();
        outState.putFloat(ARG_ZOOM, position.zoom);
        outState.putDouble(ARG_LATITUDE, position.target.latitude);
        outState.putDouble(ARG_LONGITUDE, position.target.longitude);
    }

    @Override
    public void setMapType(MapType mapType) {
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
        if (locations != null) {
            map.clear();
            for (Location location : locations.getLocations()) {
                drawMarker(location);
            }
        }
    }

    @Override
    public void getDirections(android.location.Location origin, Location destination) {
        Bundle bundle = new Bundle();
        bundle.putDouble(DirectionsLoader.ARG_ORIG_LAT, origin.getLatitude());
        bundle.putDouble(DirectionsLoader.ARG_ORIG_LONG, origin.getLongitude());
        bundle.putDouble(DirectionsLoader.ARG_DEST_LAT, destination.latitude);
        bundle.putDouble(DirectionsLoader.ARG_DEST_LONG, destination.longitude);
        getFragment().getLoaderManager()
                .restartLoader(LOADER_DIRECTIONS, bundle, new DirectionsCallbacks());
    }

    private void drawDirections() {
        if (directions.areValid()) {
            drawMarkers();
            PolylineOptions directionsOptions = new PolylineOptions();
            directionsOptions.addAll(directions.points());
            directionsOptions.color(getResources().getColor(R.color.cruz_roja_main_red));
            map.addPolyline(directionsOptions);
            directionsListener.onDirectionsDrawn(directions);
        } else {
            Toast.makeText(getActivity(), R.string.error_invalid_directions, Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public void toggleLocations(LocationType type, boolean newState) {
        for (Marker marker : currentMarkers.keySet()) {
            if (currentMarkers.get(marker).type == type) {
                marker.setVisible(newState);
            }
        }
    }

    @Override
    public boolean locate(android.location.Location location) {
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

    @Override
    public void onLocationsListUpdated(Locations list) {
        locations = list;
        if (this.isAdded()) {
            drawMarkers();
        }
    }

    @Override
    public Fragment getFragment() {
        return this;
    }

    @Override
    public void removeDirections() {
        drawMarkers();
    }

    @Override
    public boolean hasDirections() {
        return directions != null && directions.areValid();
    }

    private void drawMarker(Location location) {
        MarkerOptions options = new MarkerOptions();
        options.position(new LatLng(location.latitude, location.longitude));
        options.icon(BitmapDescriptorFactory.fromResource(location.type.icon));
        currentMarkers.put(map.addMarker(options), location);
        map.setInfoWindowAdapter(new OnLocationClickAdapter());
    }

    public interface MapInteractionListener {
        void onLocationClicked(Location location);
    }

    public interface DirectionsDrawnHandler {
        void onDirectionsDrawn(Directions directions);
    }

    private class OnLocationClickAdapter implements GoogleMap.InfoWindowAdapter {
        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            listener.onLocationClicked(currentMarkers.get(marker));
            return null;
        }
    }

    private class DirectionsCallbacks implements LoaderManager.LoaderCallbacks<Directions> {

        @Override
        public Loader<Directions> onCreateLoader(int id, Bundle args) {
            return new DirectionsLoader(getFragment().getActivity(), args);
        }

        @Override
        public void onLoadFinished(Loader<Directions> directionsLoader, Directions directions) {
            GoogleMapFragment.this.directions = directions;
            drawDirections();
        }

        @Override
        public void onLoaderReset(Loader<Directions> directionsLoader) {

        }
    }
}
