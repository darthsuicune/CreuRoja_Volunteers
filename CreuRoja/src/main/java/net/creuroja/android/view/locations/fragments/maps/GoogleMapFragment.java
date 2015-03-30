package net.creuroja.android.view.locations.fragments.maps;

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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.algo.PreCachingAlgorithmDecorator;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import net.creuroja.android.R;
import net.creuroja.android.model.directions.Directions;
import net.creuroja.android.model.directions.loader.DirectionsLoader;
import net.creuroja.android.model.locations.Location;
import net.creuroja.android.model.locations.LocationType;
import net.creuroja.android.model.locations.Locations;
import net.creuroja.android.view.locations.fragments.LocationsHandlerFragment.OnLocationsListUpdated;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GoogleMapFragment extends SupportMapFragment
        implements MapFragmentHandler, OnLocationsListUpdated {
    private static final double DEFAULT_LATITUDE = 41.3958;
    private static final double DEFAULT_LONGITUDE = 2.1739;
    private static final int DEFAULT_ZOOM = 12;
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

    public GoogleMapFragment() {
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

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                       Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        initMap();
        setCameraPosition(savedInstanceState);
        if (locations != null) {
            drawMarkers();
        }
        return v;
    }

    public void initMap() {
        if (map != null) {
            return;
        }
        map = getMap();
        setClusterOptions();
        setMapOptions();
        map.setOnCameraChangeListener(cluster);
        map.setOnMarkerClickListener(cluster);
    }

    private void setClusterOptions() {
        cluster = new ClusterManager<>(getActivity(), map);
        cluster.setAlgorithm(
                new PreCachingAlgorithmDecorator<>(
                        new DistanceAlgorithmWithRemoval<ClusterMarker>()));
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
                        currentZoom += 2;
                        moveCameraTo(cluster.getPosition());
                        return true;
                    }
                });
        cluster.setRenderer(new DefaultClusterRenderer<ClusterMarker>(getActivity(), map, cluster) {
            @Override protected void onBeforeClusterItemRendered(ClusterMarker item,
                                                                 MarkerOptions markerOptions) {
                super.onBeforeClusterItemRendered(item, markerOptions);
                markerOptions.icon(BitmapDescriptorFactory.fromResource(item.icon()));
            }
        });
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

    public void drawMarkers() {
        if (locations != null) {
            cluster.addItems(createCollectionForCluster());
        }
    }

    private Collection<ClusterMarker> createCollectionForCluster() {
        Collection<ClusterMarker> markers = new ArrayList<>();
        for (Location location : locations) {
            if (locations.isTypeVisible(location.type)) {
                markers.add(new ClusterMarker(location));
            }
        }
        return markers;
    }

    private void setCameraPosition(Bundle state) {
        currentZoom = (state == null) ? DEFAULT_ZOOM : state.getInt(ARG_ZOOM);
        double latitude = (state == null) ? DEFAULT_LATITUDE : state.getDouble(ARG_LATITUDE);
        double longitude = (state == null) ? DEFAULT_LONGITUDE : state.getDouble(ARG_LONGITUDE);
        LatLng position = new LatLng(latitude, longitude);
        moveCameraTo(position);
    }

    @Override public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        CameraPosition position = map.getCameraPosition();
        outState.putInt(ARG_ZOOM, Math.round(position.zoom));
        outState.putDouble(ARG_LATITUDE, position.target.latitude);
        outState.putDouble(ARG_LONGITUDE, position.target.longitude);
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

    @Override public void activateLocationsOfType(LocationType type) {
        addMarkersOfType(type);
        cluster.cluster();
    }

    @Override public void deactivateLocationsOfType(LocationType type) {
        removeMarkersOfType(type);
        cluster.cluster();
    }

    private void addMarkersOfType(LocationType type) {
        List<ClusterMarker> markers = new ArrayList<>();
        for (Location location : locations.ofType(type)) {
            ClusterMarker marker;
            if (currentMarkers.containsKey(location)) {
                marker = currentMarkers.get(location);
            } else {
                marker = new ClusterMarker(location);
                currentMarkers.put(location, marker);
            }
            markers.add(marker);
        }
        cluster.addItems(markers);
    }

    private void removeMarkersOfType(LocationType type) {
        for (ClusterMarker marker : currentMarkers.values()) {
            if (marker.isOneOf(type)) {
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

    @Override public OnLocationsListUpdated getOnLocationsListUpdatedListener() {
        return this;
    }

    @Override public void onLocationsListUpdated(Locations list) {
        if (cluster == null) {
            initMap();
        }
        cluster.clearItems();
        this.locations = list;
        for (Location location : list.locations()) {
            ClusterMarker marker = new ClusterMarker(location);
            cluster.addItem(marker);
            currentMarkers.put(location, marker);
        }
        if (this.isAdded()) {
            drawMarkers();
        }
    }

    @Override public Fragment getFragment() {
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
            drawMarkers();
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
            return new DirectionsLoader(getFragment().getActivity(), args);
        }

        @Override public void onLoadFinished(Loader<Directions> loader, Directions directions) {
            GoogleMapFragment.this.directions = directions;
            drawDirections();
        }

        @Override public void onLoaderReset(Loader<Directions> directionsLoader) {

        }
    }

    private class ClusterMarker implements ClusterItem {
        Location location;

        public ClusterMarker(Location location) {
            this.location = location;
        }

        @Override public LatLng getPosition() {
            return new LatLng(location.latitude, location.longitude);
        }

        public boolean isOneOf(LocationType type) {
            return location.type == type;
        }

        public int icon() {
            return location.type.icon;
        }
    }
}
