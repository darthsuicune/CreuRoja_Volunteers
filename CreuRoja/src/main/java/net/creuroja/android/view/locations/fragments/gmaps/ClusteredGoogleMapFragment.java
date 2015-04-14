package net.creuroja.android.view.locations.fragments.gmaps;

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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.algo.PreCachingAlgorithmDecorator;

import net.creuroja.android.R;
import net.creuroja.android.model.directions.Directions;
import net.creuroja.android.model.directions.loader.DirectionsLoader;
import net.creuroja.android.model.locations.Location;
import net.creuroja.android.model.locations.LocationType;
import net.creuroja.android.model.locations.Locations;
import net.creuroja.android.view.locations.fragments.LocationsHandlerFragment.OnLocationsListUpdated;
import net.creuroja.android.view.locations.fragments.MapFragmentHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClusteredGoogleMapFragment extends SupportMapFragment
        implements MapFragmentHandler, OnLocationsListUpdated {
    private static final int LOADER_DIRECTIONS = 1;
    private static final String ARG_ZOOM = "zoom";
    private static final String ARG_LATITUDE = "latitude";
    private static final String ARG_LONGITUDE = "longitude";

    GoogleMap map;
    Directions directions;
    MapInteractionListener listener;

    Locations locations;
    Map<Location, ClusterMarker> currentMarkers = new HashMap<>();
    SharedPreferences prefs;
    DirectionsDrawnListener directionsListener;
    ClusterManager<ClusterMarker> cluster;

    int currentZoom;
    CameraPosition position;

    public ClusteredGoogleMapFragment() {
        super();
    }

    @Override public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (MapInteractionListener) activity;
            directionsListener = (DirectionsDrawnListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    activity.toString() + " must implement MapInteractionListener");
        }
        prefs = PreferenceManager.getDefaultSharedPreferences(activity);
    }

    @Override public void onDetach() {
        super.onDetach();
        position = map.getCameraPosition();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                       Bundle savedInstanceState) {

        View v = super.onCreateView(inflater, container, savedInstanceState);
        initMap(savedInstanceState);
        return v;
    }

    public void initMap(Bundle savedInstanceState) {
        map = getMap();
        setMapOptions();
        setClusterOptions();
        setCameraPosition(savedInstanceState);
        //This will only hold true upon map-list-map
        if (locations != null && savedInstanceState == null) {
            drawMarkers();
        }
    }

    private void setClusterOptions() {
        cluster = new ClusterManager<>(getActivity(), map);
        if (itsMyPhone()) {
            cluster.setRenderer(new IconizedClusterRenderer(getActivity(), map, cluster) {
                @Override protected void onBeforeClusterItemRendered(ClusterMarker item,
                                                                     MarkerOptions markerOptions) {
                    super.onBeforeClusterItemRendered(item, markerOptions);
                    markerOptions.anchor(0.1f, 0.1f);
                }
            });
        } else {
            cluster.setRenderer(new IconizedClusterRenderer(getActivity(), map, cluster));
        }
        int distanceForClusters = 0;
        cluster.setAlgorithm(new PreCachingAlgorithmDecorator<>(
                new DistanceAlgorithmWithRemoval<ClusterMarker>(distanceForClusters)));
        cluster.setOnClusterItemClickListener(
                new ClusterManager.OnClusterItemClickListener<ClusterMarker>() {
                    @Override public boolean onClusterItemClick(ClusterMarker marker) {
                        moveCameraTo(marker.getPosition());
                        listener.onLocationClicked(marker.location);
                        return true;
                    }
                });

        cluster.setOnClusterClickListener(
                new ClusterManager.OnClusterClickListener<ClusterMarker>() {
                    @Override public boolean onClusterClick(Cluster<ClusterMarker> cluster) {
                        currentZoom += 1;
                        moveCameraTo(cluster.getPosition());
                        return true;
                    }
                });
        map.setOnCameraChangeListener(cluster);
        map.setOnMarkerClickListener(cluster);
    }

    private boolean itsMyPhone() {
        return getResources().getDisplayMetrics().densityDpi == 538;
    }

    private void moveCameraTo(LatLng location) {
        CameraPosition.Builder cameraBuilder = new CameraPosition.Builder();
        cameraBuilder.target(location).zoom(currentZoom);
        map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraBuilder.build()));
    }

    private void setMapOptions() {
        UiSettings settings = map.getUiSettings();
        settings.setCompassEnabled(false);
        settings.setRotateGesturesEnabled(true);
        settings.setZoomControlsEnabled(false);
        settings.setZoomGesturesEnabled(true);
        settings.setScrollGesturesEnabled(true);
        settings.setTiltGesturesEnabled(true);
    }

    private void setCameraPosition(Bundle state) {
        double latitude, longitude;
        if (state != null) {
            currentZoom = (state.containsKey(ARG_ZOOM)) ? state.getInt(ARG_ZOOM) : DEFAULT_ZOOM;
            latitude = (state.containsKey(ARG_LATITUDE)) ? state.getDouble(ARG_LATITUDE) :
                    DEFAULT_LATITUDE;
            longitude = (state.containsKey(ARG_LONGITUDE)) ? state.getDouble(ARG_LONGITUDE) :
                    DEFAULT_LONGITUDE;
        } else {
            currentZoom = DEFAULT_ZOOM;
            latitude = DEFAULT_LATITUDE;
            longitude = DEFAULT_LONGITUDE;
        }
        LatLng position = new LatLng(latitude, longitude);
        moveCameraTo(position);
    }

    public void drawMarkers() {
         if (locations != null) {
            cluster.clearItems();
            cluster.addItems(createCollectionForCluster());
            cluster.cluster();
        }
    }

    private Collection<ClusterMarker> createCollectionForCluster() {
        Collection<ClusterMarker> markers = new ArrayList<>();
        for (Location location : locations.locations()) {
            ClusterMarker marker = new ClusterMarker(location);
            markers.add(marker);
            currentMarkers.put(location, marker);
        }
        return markers;
    }

    @Override public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        CameraPosition camera = map.getCameraPosition();
        outState.putInt(ARG_ZOOM, Math.round(camera.zoom));
        outState.putDouble(ARG_LATITUDE, camera.target.latitude);
        outState.putDouble(ARG_LONGITUDE, camera.target.longitude);
    }

    @Override public void setMapType(MapType mapType) {
        map.setMapType(convertToGoogleMapType(mapType));
    }

    private int convertToGoogleMapType(MapType mapType) {
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

    @Override public void getDirections(android.location.Location origin, Location destination) {
        Bundle bundle = new Bundle();
        bundle.putDouble(DirectionsLoader.ARG_ORIG_LAT, origin.getLatitude());
        bundle.putDouble(DirectionsLoader.ARG_ORIG_LONG, origin.getLongitude());
        bundle.putDouble(DirectionsLoader.ARG_DEST_LAT, destination.latitude);
        bundle.putDouble(DirectionsLoader.ARG_DEST_LONG, destination.longitude);
        fragment().getLoaderManager()
                .restartLoader(LOADER_DIRECTIONS, bundle, new DirectionsCallbacks());
    }

    @Override public void activateLocationsOfType(LocationType type) {
        locations.toggleLocationType(type, true);
        addMarkersOfType(type);
        cluster.cluster();
    }

    private void addMarkersOfType(LocationType type) {
        List<ClusterMarker> markers = new ArrayList<>();
        for (Location location : locations.ofType(type)) {
            markers.add(getMarker(location));
        }
        cluster.addItems(markers);
    }

    private ClusterMarker getMarker(Location location) {
        ClusterMarker marker;
        if (currentMarkers.containsKey(location)) {
            marker = currentMarkers.get(location);
        } else {
            marker = new ClusterMarker(location);
            currentMarkers.put(location, marker);
        }
        return marker;
    }

    @Override public void deactivateLocationsOfType(LocationType type) {
        locations.toggleLocationType(type, false);
        removeMarkersOfType(type);
        drawMarkers();
    }

    private void removeMarkersOfType(LocationType type) {
        for (ClusterMarker marker : currentMarkers.values()) {
            if(marker.isOneOf(type)) {
                cluster.removeItem(marker);
            }
        }
    }

    @Override public boolean locate(android.location.Location location) {
        if (map != null) {
            moveCameraTo(new LatLng(location.getLatitude(), location.getLongitude()));
            return true;
        }
        return false;
    }

    @Override public void onLocationsListUpdated(Locations list) {
        this.locations = list;
        if (isAdded() && cluster != null) {
            drawMarkers();
        }
    }

    @Override public Fragment fragment() {
        return this;
    }

    @Override public void removeDirections() {
        if (hasDirections()) {
            map.clear();
            drawMarkers();
        }
    }

    @Override public boolean hasDirections() {
        return directions != null && directions.areValid();
    }

    @Override public void setMapInteractionListener(MapInteractionListener listener) {
        this.listener = listener;
    }

    private void drawDirections() {
        if (directions.areValid()) {
            drawLine();
            directionsListener.onDirectionsDrawn(directions);
        } else {
            Toast.makeText(getActivity(), R.string.error_invalid_directions, Toast.LENGTH_LONG)
                    .show();
        }
    }

    private void drawLine() {
        if (directions != null) {
            PolylineOptions directionsOptions = new PolylineOptions();
            directionsOptions.addAll(directions.points());
            directionsOptions.color(getResources().getColor(R.color.cruz_roja_main_red));
            map.addPolyline(directionsOptions);
        }
    }

    private class DirectionsCallbacks implements LoaderManager.LoaderCallbacks<Directions> {

        @Override public Loader<Directions> onCreateLoader(int id, Bundle args) {
            return new DirectionsLoader(fragment().getActivity(), args);
        }

        @Override public void onLoadFinished(Loader<Directions> loader, Directions directions) {
            ClusteredGoogleMapFragment.this.directions = directions;
            drawDirections();
        }

        @Override public void onLoaderReset(Loader<Directions> directionsLoader) {

        }
    }
}
